package ru.quipy.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Service
import ru.quipy.entities.UserEntity

@Service
interface UsersRepository : JpaRepository<UserEntity, Long> {
    fun save(user: UserEntity)

    @Query("SELECT * FROM Users WHERE name like :substr or login like :substr")
    fun findAllBySubstr(@Param("substr")substr: String) : List<UserEntity>
}