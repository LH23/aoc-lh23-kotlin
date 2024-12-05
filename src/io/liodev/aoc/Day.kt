package io.liodev.aoc

import kotlin.time.measureTime

interface Day<T> {
    /** Values expected for each test, in this order: [[Test Part1, Real Part1, Test Part2, Real Part2]] */
    val expectedValues: List<T>

    /** Solve part 1 of the day */
    fun solvePart1(): T

    /** Solve part 2 of the day */
    fun solvePart2(): T
}

/**
 * Runs the solutions for a specific day's Advent of Code puzzle
 *
 * This method executes the `solvePart1` and `solvePart2` methods of the provided `Day` instances and
 * compares the results against expected values
 *
 * @param dayTest The `Day` instance containing the solution for the sample/test data.
 * @param dayReal The `Day` instance containing the solution for the real input data.
 * @param year The year of this Advent of Code puzzle.
 * @param extraDays An optional list of additional `Day` instances to run.
 * @param printTimings If `true`, prints the execution time for each part.
 * @param skipTests A list of booleans indicating which tests to skip.
 */
fun runDay(
    dayTest: Day<*>,
    dayReal: Day<*>,
    year: Int,
    extraDays: List<Day<*>> = listOf(),
    printTimings: Boolean = false,
    skipTests: List<Boolean> = listOf(false, false, false, false),
) {
    val christmasSeparator = "❆⋆꙳•✩⋆☃⋆✩•꙳⋆❆"
    val longChristmasSeparator = "$christmasSeparator⋆$christmasSeparator⋆$christmasSeparator"

    println("\n$christmasSeparator⋆꙳ ${dayTest.javaClass.simpleName} $year ꙳⋆$christmasSeparator")

    processExtraDays(extraDays, printTimings, longChristmasSeparator)

    val elapsedPart1 =
        measureTime {
            if (!skipTests[0]) {
                val resultTest1 = dayTest.solvePart1()
                checkResult("Test Part1", resultTest1, dayTest.expectedValues[0])
            }
            if (!skipTests[1]) {
                val result1 = dayReal.solvePart1()
                checkResult("Real Part1", result1, dayReal.expectedValues[1])
            }
        }
    if (printTimings) println(" Elapsed Part1: $elapsedPart1")
    println(longChristmasSeparator)

    val elapsedPart2 =
        measureTime {
            if (!skipTests[2]) {
                val resultTest2 = dayTest.solvePart2()
                checkResult("Test Part2", resultTest2, dayTest.expectedValues[2])
            }
            if (!skipTests[3]) {
                val result2 = dayReal.solvePart2()
                checkResult("Real Part2", result2, dayReal.expectedValues[3])
            }
        }
    if (printTimings) println(" Elapsed Part2: $elapsedPart2")
    println(longChristmasSeparator)

}

fun processExtraDays(
    extraDays: List<Day<*>>,
    printTimings: Boolean,
    longChristmasSeparator: String,
) {
    for ((i, day) in extraDays.withIndex()) {
        val elapsed =
            measureTime {
                val result = day.solvePart1()
                checkResult("ExtraDay$i Part1", result, day.expectedValues[4 + i * 2])
            }
        if (printTimings) println(" Elapsed Part1 ExtraDay$i: $elapsed")
        val elapsed2 =
            measureTime {
                val result = day.solvePart2()
                checkResult("ExtraDay$i Part2", result, day.expectedValues[4 + i * 2 + 1])
            }
        if (printTimings) println(" Elapsed Part2 ExtraDay$i: $elapsed2")
        println(longChristmasSeparator)
    }
}
