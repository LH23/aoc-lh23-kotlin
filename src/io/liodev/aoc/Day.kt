package io.liodev.aoc

import kotlin.time.measureTime

interface Day<T> {
    val expectedValues: List<T>
    fun solvePart1(): T
    fun solvePart2(): T
}

fun runDay(
    dayTest: Day<*>,
    dayReal: Day<*>,
    printTimings: Boolean = false,
    skipTests: List<Boolean> = listOf(false, false, false, false)
) {
    val christmasSeparator = "❆⋆꙳•✩⋆☃⋆✩•꙳⋆❆"
    println("\n$christmasSeparator⋆❆⋆꙳ ${dayTest.javaClass.simpleName} ꙳⋆❆⋆${christmasSeparator}")

    val elapsedPart1 = measureTime {
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
    println("$christmasSeparator⋆$christmasSeparator⋆$christmasSeparator")

    val elapsedPart2 = measureTime {
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
    println("$christmasSeparator⋆$christmasSeparator⋆$christmasSeparator")

}
