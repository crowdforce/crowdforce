package space.crowdforce.exception

class UnauthorizedAccessException(override val message: String = "Unauthorized") : OperationException(message)