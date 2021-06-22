package space.crowdforce.service.mapper

import org.springframework.stereotype.Component
import space.crowdforce.controllers.model.GoalUI
import space.crowdforce.controllers.model.TrackableItemEventPrototypeUI
import space.crowdforce.controllers.model.TrackableItemUI
import space.crowdforce.domain.Goal
import space.crowdforce.domain.item.TrackableItem
import space.crowdforce.domain.item.TrackableItemEventPrototype

@Component
class MapperService {
    fun map(goal: Goal): GoalUI = GoalUI(goal.id, goal.name, goal.description, goal.progressBar, goal.creationTime)
    fun map(trackableItem: TrackableItem): TrackableItemUI = TrackableItemUI(
        id = trackableItem.id,
        activityId = trackableItem.activityId,
        name = trackableItem.name
    )

    fun map(eventPrototype: TrackableItemEventPrototype): TrackableItemEventPrototypeUI = TrackableItemEventPrototypeUI(
        eventPrototype.id,
        eventPrototype.message,
        eventPrototype.startDate,
        eventPrototype.recurring
    )
}
