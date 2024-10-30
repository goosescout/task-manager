package ru.quipy.api

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType(aggregateEventsTableName = "task-status-and-tasks-aggregate")
class TaskStatusAndTasksAggregate : Aggregate