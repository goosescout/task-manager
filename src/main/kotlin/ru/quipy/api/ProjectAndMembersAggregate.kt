package ru.quipy.api

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "project-and-members-aggregate")
class ProjectAndMembersAggregate : Aggregate