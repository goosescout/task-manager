package ru.quipy.projections.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.quipy.entities.MemberEntity
import java.util.UUID

interface MemberRepository: JpaRepository<MemberEntity, UUID> {
    fun findByProjectId(id: UUID): MutableList<MemberEntity>

    fun findByNameOrLoginContaining(nameSubstring: String, loginSubstring: String): MutableList<MemberEntity>
}
