package models

/**
 * Data holder representing hours, minutes.
 * @param [hours] value of hours within range of 0 to 23 (inclusively)
 * @param [minutes] value of minutes within range of 0 to 59 (inclusively)
 */
class SimpleTime(hours: Int, minutes: Int) {
    val hours: Int
    val minutes: Int

    init {
        this.hours = hours % 24
        this.minutes = minutes % 60
    }

    companion object {
        /**
         * Parse [timeString] into [SimpleTime]
         *
         * @param timeString must follow a pattern "HH:MM", where hours range from "0" to "23" (inclusively)
         * and minutes range from "00" to "59" (inclusively)
         **/
        fun from(timeString: String): SimpleTime {
            val splits = timeString.split(":")
            return SimpleTime(hours = Integer.parseInt(splits[0]), minutes = Integer.parseInt(splits[1]))
        }
    }

    /**
     * Return whether a given [other] [SimpleTime] runs either before or at the same time as this one.
     **/
    fun happensBeforeOrAt(other: SimpleTime): Boolean {
        return this.totalMinutes() <= other.totalMinutes()
    }

    private fun totalMinutes(): Int = hours * 60 + minutes

    /**
     * Serialize the object in "HH:MM" format
     */
    override fun toString(): String {
        val minutesStr = if (minutes == 0) "00" else "$minutes"
        return "$hours:$minutesStr"
    }
}