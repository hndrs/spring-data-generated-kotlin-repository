package io.hndrs.annotation

import com.google.auto.service.AutoService
import org.springframework.core.annotation.MergedAnnotation
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Field
import java.io.File
import java.time.Instant
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeKind
import javax.tools.Diagnostic
import kotlin.reflect.KClass


@SupportedAnnotationTypes(
    "io.hndrs.annotation.GenerateRepository"
)
@AutoService(Processor::class)
class MongoRepositoryProcessor : AbstractProcessor() {

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    private companion object {
        val KAPT_KOTLIN_GENERATED_OPTION = "kapt.kotlin.generated"
        val GENERATE_ERROR = "generate.error"
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {

        for (annotation in annotations!!) {
            val annotatedElements: Set<Element> = roundEnv.getElementsAnnotatedWith(annotation)
            annotatedElements.forEach {
                it.getAnnotation(GenerateRepository::class.java)
                writeClass(
                    ModelHelper.getPackageName(it),
                    it.simpleName.toString(),
                    ModelHelper.getIdType(it),
                    ModelHelper.getProperties(it),
                    it.getAnnotation(GenerateRepository::class.java)
                )
            }
        }
        return true
    }

    private fun writeClass(
        packageName: String,
        entityTypeName: String,
        idTypeName: String,
        params: List<ModelHelper.ValueParameterDefinition>,
        generateRepository: GenerateRepository
    ) {

        val generationTimeStamp = Instant.now()
        val generatedRepositoryName = "${entityTypeName}Repository"
        val generatedCustomRepositoryName = "${entityTypeName}RepositoryExtension"
        val imports: List<KClass<*>> = params.map { it.propertyType }.distinct()

        File(processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION], "${entityTypeName}Repository.kt")
            .writer()
            .use {
                it.appendLine("package $packageName")
                it.appendLine()
                it.appendLine("import javax.annotation.processing.Generated")
                it.appendLine("import org.springframework.data.mongodb.core.MongoTemplate")
                it.appendLine("import org.springframework.data.mongodb.core.query.Criteria")
                it.appendLine("import org.springframework.data.mongodb.core.query.Query")
                it.appendLine("import org.springframework.data.mongodb.repository.MongoRepository")
                imports.forEach { import ->
                    it.appendLine("import ${import.qualifiedName}")
                }
                if (!generateRepository.extensionOnly) {
                    it.appendLine()
                    it.appendLine(generatedAnnotation(generationTimeStamp))
                    it.appendLine("interface $generatedRepositoryName : MongoRepository<$entityTypeName, $idTypeName>, $generatedCustomRepositoryName")
                    it.appendLine()
                }
                it.appendLine(generatedAnnotation(generationTimeStamp))
                it.appendLine("interface $generatedCustomRepositoryName {")
                it.appendLine()
                it.appendLine("    fun findOneAndSave(${args(params, true)}, update: Function1<$entityTypeName, $entityTypeName>): $entityTypeName?")
                it.appendLine()
                it.appendLine("    fun findBy(${args(params, true)}): List<$entityTypeName>")
                it.appendLine("}")
                it.appendLine()
                it.appendLine(generatedAnnotation(generationTimeStamp))
                it.appendLine("class ${generatedCustomRepositoryName}Impl(private val template: MongoTemplate): $generatedCustomRepositoryName{")
                it.appendLine()
                it.appendLine("    override fun findOneAndSave(${args(params)}, update: Function1<$entityTypeName, $entityTypeName>): $entityTypeName? {")
                it.appendLine("        val criterias:MutableList<Criteria> = mutableListOf()")
                it.appendLine()
                it.appendWhereCriterias(params)
                it.appendLine()
                it.appendLine("        val query = Query(Criteria().andOperator(*criterias.toTypedArray()))")
                it.appendLine("        return template.findOne(query, $entityTypeName::class.java)?.let {")
                it.appendLine("            template.save(update.invoke(it)${updateOnSave(params)})")
                it.appendLine("        }")
                it.appendLine("    }")
                it.appendLine()
                it.appendLine("    override fun findBy(${args(params)}): List<$entityTypeName> {")
                it.appendLine("        val criterias:MutableList<Criteria> = mutableListOf()")
                it.appendLine()
                it.appendWhereCriterias(params)
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

    private fun generatedAnnotation(timeStamp: Instant): String {
        return "@Generated(value = [\"${this::class.qualifiedName}\"], date = \"$timeStamp\", comments = \"\")"
    }


    fun Appendable.appendWhereCriterias(valueParameters: List<ModelHelper.ValueParameterDefinition>) {
        valueParameters.filter {
            it.options?.exclude?.not() ?: true
        }.forEach {
            this.appendLine("        ${it.propertyName}?.let{ criterias.add(Criteria.where(\"${it.fieldAnnoationName ?: it.propertyName}\").`is`(it))}")
        }
    }

    private fun args(valueParameters: List<ModelHelper.ValueParameterDefinition>, withDefaults: Boolean = false): String {
        return valueParameters.filter {
            it.options?.exclude?.not() ?: true
        }
            .map { "${it.propertyName}: ${it.propertyType.simpleName}?" }
            .map {
                if (withDefaults) {
                    "$it = null"
                } else {
                    it
                }
            }.joinToString(separator = ", ")
    }

    private fun updateOnSave(valueParameters: List<ModelHelper.ValueParameterDefinition>): String {
        return valueParameters.filter {
            it.options?.updateOnSave ?: false
        }
            .filter { it.propertyType == Instant::class }
            .joinToString(", ") { "${it.propertyName} = Instant.now()" }
            .also {
                if (!it.isBlank()) {
                    return ".copy($it)"
                }
            }

    }
}

object ModelHelper {

    fun getPackageName(element: Element): String {
        var foundPackage: Boolean = true
        var nextElement: Element = element
        lateinit var packageName: String
        do {
            val enclosingElement = nextElement.enclosingElement
            if (enclosingElement is PackageElement) {
                packageName = enclosingElement.qualifiedName.toString()
                foundPackage = true
            } else {
                nextElement = enclosingElement
            }
        } while (!foundPackage)
        return packageName
    }

    fun getIdType(element: Element): String {
        return element.enclosedElements.first {
            it.getAnnotation(Id::class.java) != null
        }.let {
            getClass(it).simpleName.toString()
        }
    }

    fun getProperties(element: Element): List<ValueParameterDefinition> {
        return element.enclosedElements
            .filterIsInstance<VariableElement>()
            .map {
                val fieldAnnoationName = it.getAnnotation(Field::class.java)
                    ?.let { MergedAnnotation.from(it) }
                    ?.getString("name")
                ValueParameterDefinition(
                    it.simpleName.toString(),
                    getClass(it),
                    fieldAnnoationName,
                    it.getAnnotation(Options::class.java)
                )
            }
    }

    private fun getClass(it: Element): KClass<*> {
        val type = it.asType()

        return when (type.kind) {
            TypeKind.DECLARED -> Class.forName(type.toString()).kotlin
            TypeKind.BOOLEAN -> Boolean::class
            TypeKind.BYTE -> Byte::class
            TypeKind.SHORT -> Short::class
            TypeKind.INT -> Int::class
            TypeKind.LONG -> Long::class
            TypeKind.CHAR -> Char::class
            TypeKind.FLOAT -> Float::class
            TypeKind.DOUBLE -> Double::class
            else -> throw Exception("Unknown type: $type, kind: ${type.kind}")
        }
    }

    data class ValueParameterDefinition(
        val propertyName: String,
        val propertyType: KClass<*>,
        val fieldAnnoationName: String?,
        val options: Options?
    )
}

