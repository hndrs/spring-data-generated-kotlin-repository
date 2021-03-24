
import io.hndrs.annotation.processing.repository.GenerateRepository
import org.springframework.data.annotation.Id

@GenerateRepository(type = GenerateRepository.Type.MONGO)
data class SimpleEntityMissingPackageName(
    @Id
    val id: String
)
