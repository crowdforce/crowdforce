package space.crowdforce.service.tg

import org.springframework.stereotype.Component

@Component
class CommandContext {
    private val map: MutableMap<Long, UserContext> = mutableMapOf()

    fun getOrCreate(userId: Long): UserContext = map.computeIfAbsent(userId) { k -> UserContext() }
}

class UserContext {
    private val values: MutableMap<String, String> = mutableMapOf();
    private var lastTextId: String? = null
    var lastCommand: String? = null

    fun applyContext(inputData: Map<String, String>) {
        values.putAll(inputData)
    }

    fun put(key: Argument, value: String) {
        put(key.argName, value)
    }

    fun put(key: String, value: String) {
        values.put(key, value)
    }

    fun putText(textId: String, value: String) {
        values.put(textId, value)
        lastTextId = textId
    }

    fun pollTextId(): String? {
        val text = lastTextId

        lastTextId = null;

        return text
    }

    fun command(command: String) {
        lastCommand = command
    }

    fun value(arg: Argument): String? {
        return arg.rawFrom(values)
    }

    fun valueByLink(arg: Argument): String? {
        return arg.rawFrom(values)?.let { values[it] }
    }

    fun invalidate(){
        values.clear()
        lastTextId = null
        lastCommand = null
    }

    override fun toString(): String {
        return "UserContext(values=$values, lastTextId=$lastTextId, lastCommand=$lastCommand)"
    }
}
