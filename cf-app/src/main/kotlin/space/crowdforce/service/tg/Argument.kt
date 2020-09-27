package space.crowdforce.service.tg

enum class Argument(val argName: String) {
    OWNED_PROJECT_ID("ownedProjectId"),
    GOAL_NAME("goalName") {
        override fun from(map: Map<String, String>): String {
            return map[rawFrom(map)!!]!!
        }
    },
    GOAL_DESCRIPTION("goalDescription"){
        override fun from(map: Map<String, String>): String {
            return map[rawFrom(map)!!]!!
        }
    };

    fun rawFrom(map: Map<String, String>): String? {
        return map.get(argName)
    }

    open fun from(map: Map<String, String>): String {
        return map.get(argName)!!
    }
}
