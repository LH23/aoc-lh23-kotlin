package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import kotlin.math.sqrt

// 2023 Day06
class Day06(input: String) : Day<Int> {
    override val expectedValues = listOf(288, 781200, 71503, 49240091)

    private val races = input.split("\n").map {
        it.substringAfter(":").trim().split(" ")
    }.map { it.filterNot(String::isEmpty).map(String::toInt) }

    private val racesConcat = races.map { race ->
        race.joinToString("") { it.toString() }
    }.map { it.toLong() }

    override fun solvePart1() = races[0].indices.map {
        val time = races[0][it].toLong()
        val distance = races[1][it].toLong()
        winningOptions(time, distance)
    }.reduce { a, b -> a * b }

    override fun solvePart2(): Int = winningOptions(racesConcat[0], racesConcat[1])

    private fun winningOptions(time: Long, distance: Long): Int {
        val roots = listOf(
            ((-time + sqrt(1.0 * time * time - 4 * distance)) / -2).toInt(),
            ((-time - sqrt(1.0 * time * time - 4 * distance)) / -2 -0.01).toInt()
        )
        return (roots[1] - roots[0])
    }
}

fun main() {
    val name = Day06::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day06(testInput), Day06(realInput), printTimings = true)
}