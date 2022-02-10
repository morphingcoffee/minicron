import domain.MiniCronSolver
import models.SimpleConfig
import models.SimpleTime
import test.InternalTestRunner

/**
 * Entry-point to the MiniCron program, which determines next execution
 * of a given cron config & an arbitrary current time value.
 *
 * Input argument: arbitrary time in "HH:MM" format, such as "16:10"
 * STDIN: arbitrary number of mini cron configuration lines in "MIN_PATTERN HOUR_PATTERN EXECUTABLE" format,
 * such as "45 * /bin/run_me_hourly"
 *
 * Custom Input argument: "test"
 *  runs internal validity tests. No STDIN required.
 *
 * Author: Evaldas Snukiskis
 */
fun main(args: Array<String>) {
    val inputArg = args[0]

    if ("test" == inputArg) {
        // Special input argument "test" - to run internal tests instead of provided config in STDIN
        InternalTestRunner().runTests()
    } else {
        // Otherwise, expect a time argument in the form of "HH:MM"
        val simulatedTime = SimpleTime.from(inputArg)

        // Get all config input lines from STDIN
        val inputConfigLines = generateSequence(::readLine)
        inputConfigLines.forEach { line ->
            val config = SimpleConfig.from(line)
            val solution = MiniCronSolver().nextRunTime(givenTime = simulatedTime, config = config)
            println(solution)
        }
    }
}


