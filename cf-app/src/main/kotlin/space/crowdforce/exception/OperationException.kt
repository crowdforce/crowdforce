package space.crowdforce.exception

import java.lang.RuntimeException

open class OperationException(override val message: String) : RuntimeException(message)