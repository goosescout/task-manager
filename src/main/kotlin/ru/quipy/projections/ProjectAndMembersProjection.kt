package ru.quipy.projections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.quipy.api.MemberCreatedEvent
import ru.quipy.api.ProjectAndMembersAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.UserAggregate
import ru.quipy.core.EventSourcingService
import ru.quipy.entities.MemberEntity
import ru.quipy.entities.ProjectEntity
import ru.quipy.logic.ProjectAndMembersAggregateState
import ru.quipy.projections.repository.MemberRepository
import ru.quipy.projections.repository.ProjectRepository
import ru.quipy.streams.AggregateSubscriptionsManager
import ru.quipy.streams.annotation.AggregateSubscriber
import ru.quipy.streams.annotation.SubscribeEvent
import java.util.UUID
import javax.annotation.PostConstruct

@Service
@AggregateSubscriber(
        aggregateClass = UserAggregate::class, subscriberName = "project-projection"
)
class ProjectAndMembersProjection(
    private val projectRepository: ProjectRepository,
    private val memberRepository: MemberRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager,
    val projectEsService: EventSourcingService<UUID, ProjectAndMembersAggregate, ProjectAndMembersAggregateState>,
) {
    private val logger = LoggerFactory.getLogger(ProjectAndMembersProjection::class.java)

    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(ProjectAndMembersAggregate::class, "project:project-projection") {
            `when`(ProjectCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    projectRepository.save(ProjectEntity(event.projectId, event.projectName))
                }
                logger.info("Update project projection, create project ${event.projectId}")
            }
            `when`(MemberCreatedEvent::class) { event ->
                withContext(Dispatchers.IO) {
                    memberRepository.save(MemberEntity(event.memberId, event.memberName, event.memberLogin, event.userId, event.projectId))
                }
                logger.info("Update project projection, create member ${event.memberId}")
            }
        }
    }

    fun getAllProjects(): List<ProjectEntity> {
        return projectRepository.findAll()
    }

    fun getProjectById(id: UUID): ProjectEntity {
        return projectRepository.findById(id).get()
    }

    fun getAllMembersById(ids: List<UUID>): List<MemberEntity> {
        return memberRepository.findAllById(ids)
    }

    fun getMembersByProjectId(id: UUID): List<MemberEntity> {
        return memberRepository.findByProjectId(id)
    }

    fun getAllMembersByNameSubstring(projectId: UUID, substring: String): List<MemberEntity> {
        return memberRepository.findByNameOrLoginContaining(substring, substring).filter {
            it.projectId == projectId
        }
    }

    @SubscribeEvent
    fun projectCreatedEventSubscriber(event: ProjectCreatedEvent) {
        logger.info("Project created.\nId: ${event.projectId}, name: ${event.projectName}")
    }

    @SubscribeEvent
    fun memberCreatedEventSubscriber(event: MemberCreatedEvent) {
        logger.info("Member created.\nMember id: ${event.memberId}, user id: ${event.userId}, project id: ${event.projectId}")
    }
}
