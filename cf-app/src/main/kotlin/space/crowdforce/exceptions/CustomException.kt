package space.crowdforce.exceptions

import java.lang.RuntimeException

class InvalidVerificationException(reason: String): RuntimeException(reason)
