package io.hndrs.sample

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.Instant


@Component
class SampleService(private val userRepository: UserRepository) : CommandLineRunner {


    fun test() {
        val user = User("sampleId", "sampleName", "", listOf(), "", "", Instant.now())
        userRepository.save(user)
        userRepository.findOneAndSave(id = user.id) {
            it.copy(id = "newId")
        }

        userRepository.findAll().let { println(it[0]) }
    }

    override fun run(vararg args: String?) {
        test()
    }
}
