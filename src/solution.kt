/**
 * Program reading lines from STDIN and determining next cron job run time.
 *
 * [runInternalTests] can be used to run internal tests used during dev
 *
 * Author: Evaldas Snukiskis
 */
fun main() {
    // Get a sequence of config input lines from STDIN. Read 1 line for simulated time, rest for configs
    val timeSplits = readLine()?.split(":")
    val simulatedTime =
        SimpleTime(hours = Integer.parseInt(timeSplits?.get(0)), minutes = Integer.parseInt(timeSplits?.get(1)))
    val rawInputLines = generateSequence(::readLine).toList()

    // Disabled: used for TDD while writing up the solution
    // runInternalTests()

    val lines: List<String> = rawInputLines.toList()

    lines.forEach { line ->
        val solution = solveFor(givenTime = simulatedTime, config = SimpleConfig.from(line))
        println(solution)
    }
}

fun solveFor(givenTime: SimpleTime, config: SimpleConfig): String {
    val cronTime: SimpleTime
    val todayOrTomorrow: String

    if (config.hourPattern != SimpleConfig.ALL_VALUES && config.minutePattern != SimpleConfig.ALL_VALUES) {
        /** Run daily block **/
        val configHour = Integer.parseInt(config.hourPattern)
        val configMinute = Integer.parseInt(config.minutePattern)
        cronTime = SimpleTime(hours = configHour, minutes = configMinute)
        todayOrTomorrow = if (givenTime.happensBefore(cronTime)) "today" else "tomorrow"
    } else if (config.hourPattern == SimpleConfig.ALL_VALUES && config.minutePattern != SimpleConfig.ALL_VALUES) {
        /** Run hourly block **/
        val configMinute = Integer.parseInt(config.minutePattern)
        // Special case - '>=' so that cron can still run "at the same minute" (as provided in sample test case)
        cronTime = if (configMinute >= givenTime.minutes) {
            SimpleTime(hours = givenTime.hours, minutes = configMinute)
        } else {
            SimpleTime(hours = givenTime.hours + 1, minutes = configMinute)
        }
        todayOrTomorrow = if (givenTime.happensBefore(cronTime)) "today" else "tomorrow"
    } else if (config.hourPattern != SimpleConfig.ALL_VALUES && config.minutePattern == SimpleConfig.ALL_VALUES) {
        /** Run 60 times block (every minute on a given hour). **/
        val configHour = Integer.parseInt(config.hourPattern)
        // Choose the current minute when hour matches. Otherwise, cron will run on 0th minute
        cronTime = if (givenTime.hours == configHour) {
            SimpleTime(hours = configHour, minutes = givenTime.minutes)
        } else {
            SimpleTime(hours = configHour, minutes = 0)
        }
        todayOrTomorrow = if (givenTime.happensBefore(cronTime)) "today" else "tomorrow"
    } else {
        /** Run every minute block. Will always yield current time **/
        cronTime = givenTime
        todayOrTomorrow = "today"
    }

    return "$cronTime $todayOrTomorrow - ${config.executable}"
}


data class SimpleConfig(val minutePattern: String, val hourPattern: String, val executable: String) {
    companion object {
        const val ALL_VALUES = "*"
        fun from(s: String): SimpleConfig {
            // Split config line on every space, expect 3 components
            val splits = s.split(" ")
            val minutePattern = splits[0]
            val hourPattern = splits[1]
            val executable = splits[2]

            return SimpleConfig(minutePattern = minutePattern, hourPattern = hourPattern, executable = executable)
        }
    }
}

/**
 * Data holder representing hours, minutes.
 * Ensure hours between 0 and 23, minutes between 0 and 59
 */
class SimpleTime(hours: Int, minutes: Int) {
    val hours: Int
    val minutes: Int

    init {
        this.hours = hours % 24
        this.minutes = minutes % 60
    }

    /** e.g.
     * SimpleTime 16:10 happens before SimpleTime 20:00
     * SimpleTime 16:10 does not happen before SimpleTime 15:00
     * SimpleTime 16:10 does not happen before SimpleTime 16:05
     *
     * Special case: 16:10 HAPPENS BEFORE 16:10
     * (so that cron can still run at the same minute as provided in one of test cases)
     * **/
    fun happensBefore(other: SimpleTime): Boolean {
        return this.totalMinutes() <= other.totalMinutes()
    }

    private fun totalMinutes(): Int = hours * 60 + minutes

    override fun toString(): String {
        val minutesStr = if (minutes == 0) "00" else "$minutes"
        return "$hours:$minutesStr"
    }
}

/** dev tests (given + additional made up cases) **/
fun runInternalTests() {

    // region SimpleTime tests

    if (!SimpleTime(16, 0).happensBefore(SimpleTime(16, 10))) {
        throw Exception("SimpleTime happensBefore issue")
    }

    if (SimpleTime(20, 0).happensBefore(SimpleTime(16, 10))) {
        throw Exception("SimpleTime happensBefore issue")
    }

    // endregion

    // region Solution tests

    val fakeTime = SimpleTime(hours = 16, minutes = 10)

    var solution: String = solveFor(fakeTime, SimpleConfig.from("30 1 /bin/run_me_daily"))
    if (solution != "1:30 tomorrow - /bin/run_me_daily") {
        throw Exception("run_me_daily: got '${solution}'")
    }

    solution = solveFor(fakeTime, SimpleConfig.from("30 16 /bin/run_me_daily"))
    if (solution != "16:30 today - /bin/run_me_daily") {
        throw Exception("run_me_daily: got '${solution}'")
    }

    solution = solveFor(fakeTime, SimpleConfig.from("45 * /bin/run_me_hourly"))
    if (solution != "16:45 today - /bin/run_me_hourly") {
        throw Exception("Failed run_me_hourly: got '$solution'")
    }

    solution = solveFor(fakeTime, SimpleConfig.from("10 * /bin/run_me_hourly"))
    if (solution != "16:10 today - /bin/run_me_hourly") {
        throw Exception("Failed run_me_hourly: got '$solution'")
    }

    solution = solveFor(fakeTime, SimpleConfig.from("* * /bin/run_me_every_minute"))
    if (solution != "16:10 today - /bin/run_me_every_minute"
    ) {
        throw Exception("Failed run_me_every_minute: got '$solution'")
    }

    solution = solveFor(
        fakeTime, SimpleConfig.from("* 19 /bin/run_me_sixty_times")
    )
    if (solution != "19:00 today - /bin/run_me_sixty_times"
    ) {
        throw Exception("Failed run_me_sixty_times: got '$solution'")
    }

    solution = solveFor(
        fakeTime, SimpleConfig.from("* 16 /bin/run_me_sixty_times")
    )
    if (solution != "16:10 today - /bin/run_me_sixty_times"
    ) {
        throw Exception("Failed run_me_sixty_times: got '$solution'")
    }

    solution = solveFor(
        fakeTime, SimpleConfig.from("* 15 /bin/run_me_sixty_times")
    )
    if (solution != "15:00 tomorrow - /bin/run_me_sixty_times"
    ) {
        throw Exception("Failed run_me_sixty_times: got '$solution'")
    }

    // endregion

    println("tests passed")
}