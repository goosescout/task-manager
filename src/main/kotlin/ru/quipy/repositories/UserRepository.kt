package ru.quipy.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.quipy.entities.UserEntity

interface UsersRepository : JpaRepository<UserEntity, Long> {
    fun save(user: UserEntity)

    @Query("SELECT u FROM UserEntity u WHERE u.name LIKE :substr OR u.login LIKE :substr")
    fun findAllBySubstr(@Param("substr")substr: String) : List<UserEntity>
}