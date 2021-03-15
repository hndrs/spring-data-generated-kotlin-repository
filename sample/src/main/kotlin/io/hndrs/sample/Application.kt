package io.hndrs.sample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@EnableMongoRepositories
@SpringBootApplication
open class Application(){

    fun test(){
    }
}


fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
