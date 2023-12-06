package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

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
        val time = races[0][it]
        val distance = races[1][it]
        winningOptions(time.toLong(), distance.toLong())
    }.reduce { a, b -> a * b }

    private fun winningOptions(time: Long, distance: Long): Int {
        var wins = 0
        for (speed in 1..<time) {
            val traveled = speed * (time - speed)
            if (traveled > distance) wins++
        }
        return wins
    }

    override fun solvePart2(): Int = winningOptions(racesConcat[0], racesConcat[1])
}

fun main() {
    val name = Day06::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day06(testInput), Day06(realInput))
}