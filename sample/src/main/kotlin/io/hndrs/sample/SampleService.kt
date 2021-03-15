package io.hndrs.sample

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.Instant
import javax.annotation.processing.Generated

@Component
class SampleService(private val userRepository: UserRepository) : CommandLineRunner {


    fun test() {
        val user = User("sampleId", "sampleName", Instant.now(), Instant.now())
        userRepository.save(user)

        userRepository.findOneAndSave(name = user.name) {
            it.copy(name = "newName")
        }

        userRepository.findAll().let { println(it[0]) }
    }

    override fun run(vararg args: String?) {
        test()
    }
}
