package io.hndrs.sample

import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String>, UserRepositoryExtension
