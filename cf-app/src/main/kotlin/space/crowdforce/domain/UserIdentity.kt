package space.crowdforce.domain

/**
 * Identity key for user identification by user identity.
 * Each user may have multiple identities. The Identity key is used to find a user by id which is provided by authentication provider.
 */
data class UserIdentityKey(
    /**
     * Identity.
     */
    val identity: UserIdentity,

    /**
     * Id which is provided by external authentication provider
     */
    val identityId: String
)

/**
 * Possible user identities.
 */
enum class UserIdentity(val identityType: String) {
    /**
     * Anonymous identity.
     */
    ANON("anon"),

    /**
     * Telegram identity for authentication by Telegram messenger
     */
    TG("tg");

    fun identityKey(identityId: String): UserIdentityKey = UserIdentityKey(this, identityId)

    companion object {
        fun fromName(identityType: String): UserIdentity {
            return values().find { it.identityType == identityType }
                ?: throw IllegalArgumentException("'$identityType' is not valid user identity type")
        }
    }
}
