package io.hndrs.annotation.processing.repository

import io.hndrs.annotation.processing.repository.RepsitoryGenerator.Companion.generatedAnnotation
import java.io.File
import java.time.Instant
import javax.annotation.processing.ProcessingEnvironment
import javax.tools.Diagnostic

object MongoRepsitoryGenerator : RepsitoryGenerator {

    override fun writeClass(
        processingEnv: ProcessingEnvironment,
        meta: MetaInformation
    ) {
        val entityTypeName = meta.entityName
        val generationTimeStamp = Instant.now()
        val generatedRepositoryName = "${entityTypeName}Repository"
        val generatedCustomRepositoryName = "${entityTypeName}RepositoryExtension"
        val imports: List<String> = meta.variables.map { it.propertyTypeQualifiedName }
            .filter { !it.startsWith("kotlin.") }
            .filter { !it.startsWith("java.lang") }
            .filter { !it.startsWith("java.util") }
            .distinct()

        File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION], "${entityTypeName}Repository.kt")
            .writer()
            .use {
                it.appendLine("package ${meta.packageName}")
                it.appendLine()
                it.appendLine("import javax.annotation.processing.Generated")
                it.appendLine("import org.springframework.data.mongodb.core.MongoTemplate")
                it.appendLine("import org.springframework.data.mongodb.core.query.Criteria")
                it.appendLine("import org.springframework.data.mongodb.core.query.Query")
                it.appendLine("import org.springframework.data.mongodb.repository.MongoRepository")
                imports.forEach { import ->
                    it.appendLine("import ${import}")
                }
                if (!meta.generateRepository.extensionOnly) {
                    it.appendLine()
                    it.appendLine(generatedAnnotation(generationTimeStamp))
                    it.appendLine("interface $generatedRepositoryName : MongoRepository<$entityTypeName, ${meta.idMeta.propertyTypeSimpleName}>, $generatedCustomRepositoryName")
                    it.appendLine()
                }
                it.appendLine(generatedAnnotation(generationTimeStamp))
                it.appendLine("interface $generatedCustomRepositoryName {")
                it.appendLine()
                it.appendLine(
                    "    fun findOneAndSave(${args(meta.variables, true)}, update: Function1<$entityTypeName, $entityTypeName>\n\t): $entityTypeName?"
                )
                it.appendLine()
                it.appendLine("    fun findBy(${args(meta.variables, true)}\n\t): List<$entityTypeName>")
                it.appendLine("}")
                it.appendLine()
                it.appendLine(generatedAnnotation(generationTimeStamp))
                it.appendLine("class ${generatedCustomRepositoryName}Impl(private val template: MongoTemplate): $generatedCustomRepositoryName{")
                it.appendLine()
                it.appendLine("    override fun findOneAndSave(${args(meta.variables)}, update: Function1<$entityTypeName, $entityTypeName>\n\t): $entityTypeName? {")
                it.appendLine("        val criterias:MutableList<Criteria> = mutableListOf()")
                it.appendLine()
                it.appendCriterias(meta.variables)
                it.appendLine()
                it.appendLine("        val query = Query(Criteria().andOperator(*criterias.toTypedArray()))")
                it.appendLine("        return template.findOne(query, $entityTypeName::class.java)?.let {")
                it.appendLine("            template.save(update.invoke(it)${updateOnSave(meta.variables)})")
                it.appendLine("        }")
                it.appendLine("    }")
                it.appendLine()
                it.appendLine("    override fun findBy(${args(meta.variables)}\n\t): List<$entityTypeName> {")
                it.appendLine("        val criterias:MutableList<Criteria> = mutableListOf()")
                it.appendLine()
                it.appendCriterias(meta.variables)
                it.appendLine()
                it.appendLine("        val query = Query(Criteria().andOperator(*criterias.toTypedArray()))")
                it.appendLine("        return template.find(query, $entityTypeName::class.java)")
                it.appendLine("    }")
                it.appendLine("}")
            }

        if (processingEnv.options[GENERATE_ERROR] == "true") {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Error from annotation processor!")
        }
    }

    val KAPT_KOTLIN_GENERATED_OPTION = "kapt.kotlin.generated"
    val GENERATE_ERROR = "generate.error"

    val EXCLUDE_PARAMETER: (MetaInformation.VariableMeta) -> Boolean = {
        it.options?.exclude?.not() ?: true
    }

    fun Appendable.appendCriterias(parameters: List<MetaInformation.VariableMeta>) {
        parameters.filter(EXCLUDE_PARAMETER)
            .forEach {
                this.appendLine("        ${it.propertyName}?.let{ criterias.add(Criteria.where(\"${it.propertyName}\").`is`(it))}")
                it.options?.let { options ->
                    if (options.withLte) {
                        this.appendLine("        ${it.propertyName}Lte?.let { criterias.add(Criteria.where(\"${it.propertyName}\").lte(it))}")
                    }
                    if (options.withLt) {
                        this.appendLine("        ${it.propertyName}Lt?.let { criterias.add(Criteria.where(\"${it.propertyName}\").lt(it))}")
                    }
                    if (options.withGt) {
                        this.appendLine("        ${it.propertyName}Gt?.let { criterias.add(Criteria.where(\"${it.propertyName}\").gt(it))}")
                    }
                    if (options.withGte) {
                        this.appendLine("        ${it.propertyName}Gte?.let { criterias.add(Criteria.where(\"${it.propertyName}\").gte(it))}")
                    }
                    if (options.withExists) {
                        this.appendLine("        ${it.propertyName}Exists?.let { criterias.add(Criteria.where(\"${it.propertyName}\").exists(it))}")
                    }
                    if (options.withSize) {
                        this.appendLine("        ${it.propertyName}Size?.let { criterias.add(Criteria.where(\"${it.propertyName}\").size(it))}")
                    }
                    if (options.withIn) {
                        this.appendLine("        ${it.propertyName}In?.let { criterias.add(Criteria.where(\"${it.propertyName}\").`in`(it))}")
                    }
                    if (options.withAll) {
                        this.appendLine("        ${it.propertyName}All?.let { criterias.add(Criteria.where(\"${it.propertyName}\").all(it))}")
                    }
                    if (options.withNe) {
                        this.appendLine("        ${it.propertyName}Ne?.let { criterias.add(Criteria.where(\"${it.propertyName}\").ne(it))}")
                    }
                }
            }
    }

    fun args(parameters: List<MetaInformation.VariableMeta>, withDefaults: Boolean = false): String {
        return parameters.filter(EXCLUDE_PARAMETER)
            .flatMap {
                val args = mutableListOf<String>()
                args.add("${it.propertyName}: ${it.propertyTypeSimpleName}?")
                it.options?.let { options ->
                    if (options.withLte) {
                        args.add("${it.propertyName}Lte: ${it.propertyTypeSimpleName}?")
                    }
                    if (options.withLt) {
                        args.add("${it.propertyName}Lt: ${it.propertyTypeSimpleName}?")
                    }
                    if (options.withGt) {
                        args.add("${it.propertyName}Gt: ${it.propertyTypeSimpleName}?")
                    }
                    if (options.withGte) {
                        args.add("${it.propertyName}Gte: ${it.propertyTypeSimpleName}?")
                    }
                    if (options.withExists) {
                        args.add("${it.propertyName}Exists: Boolean?")
                    }
                    if (options.withSize) {
                        args.add("${it.propertyName}Size: Int?")
                    }
                    if (options.withIn) {
                        args.add("${it.propertyName}In: Collection<${it.propertyGenericTypeArgument ?: it.propertyTypeSimpleName}>?")
                    }
                    if (options.withAll) {
                        args.add("${it.propertyName}All: Collection<${it.propertyGenericTypeArgument ?: it.propertyTypeSimpleName}>?")
                    }
                    if (options.withNe) {
                        args.add("${it.propertyName}Ne: ${it.propertyTypeSimpleName}?")
                    }
                }
                args
            }
            .map {
                if (withDefaults) {
                    "$it = null"
                } else {
                    it
                }
            }
            .map {
                "\n\t\t$it"
            }
            .joinToString(separator = ", ")
    }

    fun updateOnSave(parameters: List<MetaInformation.VariableMeta>): String {
        return parameters.filter {
            it.options?.updateOnSave ?: false
        }
            .filter { it.propertyTypeQualifiedName == Instant::class.qualifiedName!! }
            .joinToString(", ") { "${it.propertyName} = Instant.now()" }
            .also {
                if (!it.isBlank()) {
                    return ".copy($it)"
                }
            }

    }

}
