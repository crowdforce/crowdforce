package space.crowdforce.domain.item

enum class Period(
    val days: Long
) {
    NON_RECURRING(0),
    DAILY(1),
    WEEKLY(7),
    TWO_WEEK(14);
}