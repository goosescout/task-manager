package ru.quipy.projections.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.quipy.projections.entities.UserEntity
import java.util.UUID

interface UserRepository: JpaRepository<UserEntity, UUID> {
    fun findByUsernameOrLoginContaining(nameSubstring: String, loginSubstring: String): MutableList<UserEntity>
}
