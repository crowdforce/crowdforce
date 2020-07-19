package space.crowdforce.service.mapper

import org.springframework.stereotype.Component
import space.crowdforce.controllers.model.GoalUI
import space.crowdforce.domain.Goal

@Component
class MapperService {
    fun map(goal: Goal): GoalUI = GoalUI(goal.id, goal.name, goal.description, goal.progressBar, goal.creationTime)
}
