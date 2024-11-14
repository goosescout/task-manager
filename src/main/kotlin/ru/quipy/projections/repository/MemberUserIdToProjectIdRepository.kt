package ru.quipy.projections.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.quipy.projections.entities.MemberUserIdToProjectIdEntity
import java.util.UUID

interface MemberUserIdToProjectIdRepository : JpaRepository<MemberUserIdToProjectIdEntity, UUID> {
    fun findAllByUserId(userId: UUID): MutableList<MemberUserIdToProjectIdEntity>
}