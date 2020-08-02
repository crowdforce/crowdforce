package space.crowdforce.repository

import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import space.crowdforce.domain.Goal
import space.crowdforce.model.Tables.GOALS
import space.crowdforce.model.tables.records.GoalsRecord
import java.time.LocalDateTime

@Repository
@Transactional(propagation = Propagation.MANDATORY)
class GoalRepository(
    private val dslContext: DSLContext
) {
    companion object {
        val GOAL_MAPPER: (GoalsRecord) -> Goal = { record: GoalsRecord ->
            Goal(
                record.id,
                record.name,
                record.description,
                record.creationTime,
                record.progressBar,
                record.projectId
            )
        }
    }

    fun insert(projectId: Int, name: String, description: String, progress: Int, currentTime: LocalDateTime): Goal =
        GOAL_MAPPER.invoke(dslContext.insertInto(GOALS)
            .columns(GOALS.NAME, GOALS.DESCRIPTION, GOALS.PROGRESS_BAR, GOALS.CREATION_TIME, GOALS.PROJECT_ID)
            .values(name, description, progress, currentTime, projectId)
            .returning()
            .fetchOne())

    fun findGoals(projectId: Int): List<Goal> = dslContext.selectFrom(GOALS)
        .where(GOALS.PROJECT_ID.eq(projectId))
        .fetch(GOAL_MAPPER)

    fun findGoal(goalId: Int): Goal? = dslContext.selectFrom(GOALS)
        .where(GOALS.ID.eq(goalId))
        .fetchOne(GOAL_MAPPER)

    fun delete(goalId: Int): Int = dslContext.delete(GOALS).where(GOALS.ID.eq(goalId)).execute()

    fun update(goalId: Int, name: String, description: String, progress: Int): Int =
        dslContext.update(GOALS)
            .set(GOALS.NAME, name)
            .set(GOALS.DESCRIPTION, description)
            .set(GOALS.PROGRESS_BAR, progress)
            .where(GOALS.ID.eq(goalId))
            .execute()
}
