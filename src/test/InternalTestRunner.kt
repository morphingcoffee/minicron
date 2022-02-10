package test

import domain.MiniCronSolver
import models.SimpleConfig
import models.SimpleTime

/**
 * During initial TDD implementation the (non-refactored version of the) test code was used to verify validity of the solution.
 * Consists of provided test cases + additional made-up cases which may be relevant to the implementation.
 *
 * [InternalTestRunner.runTests] can be triggered to get test pass/fail status printed to STDOUT.
 */
class InternalTestRunner {

    private val solver = MiniCronSolver()

    fun runTests() {
        runSimpleTimeTests()
        runSolverTests()
    }

    private fun runSimpleTimeTests() {
        if (!SimpleTime(16, 0).happensBeforeOrAt(SimpleTime(16, 10))) {
            throw Exception("models.SimpleTime happensBefore issue")
        }

        if (SimpleTime(20, 0).happensBeforeOrAt(SimpleTime(16, 10))) {
            throw Exception("models.SimpleTime happensBefore issue")
        }

        println("SimpleTime tests passed")
    }

    private fun runSolverTests() {
        // Daily pattern tests
        var solution: String =
            solver.solveFor(SimpleTime(hours = 16, minutes = 10), SimpleConfig.from("30 1 /bin/run_me_daily"))
        validateEquals(solution, "1:30 tomorrow - /bin/run_me_daily")

        solution = solver.solveFor(SimpleTime(hours = 16, minutes = 10), SimpleConfig.from("30 16 /bin/run_me_daily"))
        validateEquals(solution, "16:30 today - /bin/run_me_daily")

        solution = solver.solveFor(
            SimpleTime(hours = 0, minutes = 0), SimpleConfig.from("0 0 /bin/daily_midnight")
        )
        validateEquals(solution, "0:00 today - /bin/daily_midnight")

        // Hourly pattern tests
        solution = solver.solveFor(SimpleTime(hours = 16, minutes = 10), SimpleConfig.from("45 * /bin/run_me_hourly"))
        validateEquals(solution, "16:45 today - /bin/run_me_hourly")

        solution = solver.solveFor(SimpleTime(hours = 16, minutes = 10), SimpleConfig.from("10 * /bin/run_me_hourly"))
        validateEquals(solution, "16:10 today - /bin/run_me_hourly")

        solution =
            solver.solveFor(SimpleTime(hours = 23, minutes = 1), SimpleConfig.from("0 * /bin/hourly_at_zeroth_minute"))
        validateEquals(solution, "0:00 tomorrow - /bin/hourly_at_zeroth_minute")

        // Once a minute pattern tests
        solution =
            solver.solveFor(SimpleTime(hours = 16, minutes = 10), SimpleConfig.from("* * /bin/run_me_every_minute"))
        validateEquals(solution, "16:10 today - /bin/run_me_every_minute")

        // 60 times pattern tests
        solution = solver.solveFor(
            SimpleTime(hours = 16, minutes = 10), SimpleConfig.from("* 19 /bin/run_me_sixty_times")
        )
        validateEquals(solution, "19:00 today - /bin/run_me_sixty_times")

        solution = solver.solveFor(
            SimpleTime(hours = 16, minutes = 10), SimpleConfig.from("* 16 /bin/run_me_sixty_times")
        )
        validateEquals(solution, "16:10 today - /bin/run_me_sixty_times")

        solution = solver.solveFor(
            SimpleTime(hours = 16, minutes = 10), SimpleConfig.from("* 15 /bin/run_me_sixty_times")
        )
        validateEquals(solution, "15:00 tomorrow - /bin/run_me_sixty_times")

        solution = solver.solveFor(
            SimpleTime(hours = 0, minutes = 0), SimpleConfig.from("* * /bin/sixty_times_midnight")
        )
        validateEquals(solution, "0:00 today - /bin/sixty_times_midnight")

        println("Solver tests passed")
    }

    private fun validateEquals(actual: String, expected: String) {
        if (actual != expected) {
            throw Exception("Assertion failed: got `${actual}` but expected `$expected`")
        }
    }
}