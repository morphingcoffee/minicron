package domain

import models.SimpleConfig
import models.SimpleTime

class MiniCronSolver {
    /**
     * The solution identifies multiple different scenarios based on the config pattern & handles them separately.
     * Nearest cron time is identified in each scenario, and a helper method [SimpleTime.happensBeforeOrAt] determines
     * whether cron time has already past today (implying tomorrow's execution).
     *
     * @return string in "HH:MM DAY - path/to/executable" format,
     * where "HH:MM" is the next cron time and "DAY" is 'today' or 'tomorrow'
     */
    fun nextRunTime(givenTime: SimpleTime, config: SimpleConfig): String {
        val cronTime: SimpleTime
        val todayOrTomorrow: String

        if (config.hourPattern != SimpleConfig.ALL_VALUES && config.minutePattern != SimpleConfig.ALL_VALUES) {
            /** Run daily block **/
            val configHour = Integer.parseInt(config.hourPattern)
            val configMinute = Integer.parseInt(config.minutePattern)
            cronTime = SimpleTime(hours = configHour, minutes = configMinute)
            todayOrTomorrow = if (givenTime.happensBeforeOrAt(cronTime)) "today" else "tomorrow"
        } else if (config.hourPattern == SimpleConfig.ALL_VALUES && config.minutePattern != SimpleConfig.ALL_VALUES) {
            /** Run hourly block **/
            val configMinute = Integer.parseInt(config.minutePattern)
            cronTime = if (configMinute >= givenTime.minutes) {
                // Special case - '>=' so that cron can still run "at the same minute"
                SimpleTime(hours = givenTime.hours, minutes = configMinute)
            } else {
                // Run the next hour
                SimpleTime(hours = (givenTime.hours + 1) % 24, minutes = configMinute)
            }
            todayOrTomorrow = if (givenTime.happensBeforeOrAt(cronTime)) "today" else "tomorrow"
        } else if (config.hourPattern != SimpleConfig.ALL_VALUES && config.minutePattern == SimpleConfig.ALL_VALUES) {
            /** Run 60 times block (every minute on a given hour). **/
            val configHour = Integer.parseInt(config.hourPattern)
            // Choose the current minute when hour matches. Otherwise, cron will run on 0th minute
            cronTime = if (givenTime.hours == configHour) {
                SimpleTime(hours = configHour, minutes = givenTime.minutes)
            } else {
                SimpleTime(hours = configHour, minutes = 0)
            }
            todayOrTomorrow = if (givenTime.happensBeforeOrAt(cronTime)) "today" else "tomorrow"
        } else {
            /** Run every minute block. Will always yield the current time **/
            cronTime = givenTime
            todayOrTomorrow = "today"
        }

        return "$cronTime $todayOrTomorrow - ${config.executable}"
    }
}