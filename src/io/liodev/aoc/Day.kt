package io.liodev.aoc

interface Day<T> {
    val expectedValues: List<T>
    fun solvePart1(): T
    fun solvePart2(): T
}

fun runDay(dayTest: Day<*>, dayReal: Day<*>) {
    println("\n")

    val resultTest1 = dayTest.solvePart1()
    checkResult("Test Part1", resultTest1, dayTest.expectedValues[0])
    val result1 = dayReal.solvePart1()
    checkResult("Real Part1", result1, dayReal.expectedValues[1])

    val resultTest2 = dayTest.solvePart2()
    checkResult("Test Part2", resultTest2, dayTest.expectedValues[2])
    val result2 = dayReal.solvePart2()
    checkResult("Real Part2", result2, dayReal.expectedValues[3])

}
