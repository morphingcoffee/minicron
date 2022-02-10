package models

/**
 * Data holder representing config parameters.
 *
 * @param [minutePattern] is a string representing minute pattern within range of 0 to 59, such as "00", "59", [ALL_VALUES], etc.
 * @param [hourPattern] is a string representing hour pattern within range of 0 to 23, such as "1", "23", [ALL_VALUES], etc.
 * @param executable is the original string path of the executable to be run
 */
data class SimpleConfig(val minutePattern: String, val hourPattern: String, val executable: String) {
    companion object {
        /** [ALL_VALUES] represents a wild card value of a pattern **/
        const val ALL_VALUES = "*"

        /**
         * Parse [configString] into [SimpleConfig]
         *
         * @param configString must follow a pattern "MINUTE_PATTERN HOUR_PATTERN EXECUTABLE" with values satisfying
         * corresponding constraints of [minutePattern], [hourPattern] & [executable]
         **/
        fun from(configString: String): SimpleConfig {
            // Split config line on every space, expect 3 components
            val splits = configString.split(" ")
            val minutePattern = splits[0]
            val hourPattern = splits[1]
            val executable = splits[2]

            return SimpleConfig(minutePattern = minutePattern, hourPattern = hourPattern, executable = executable)
        }
    }
}