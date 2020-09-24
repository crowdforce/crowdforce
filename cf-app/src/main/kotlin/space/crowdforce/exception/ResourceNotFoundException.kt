package space.crowdforce.exception

class ResourceNotFoundException(override val message: String = "Resource not found") : OperationException(message)