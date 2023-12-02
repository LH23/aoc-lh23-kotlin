package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2023 Day 2: Cube Conundrum ---
class Day02(input: String) : Day<Int> {
    override val expectedValues = listOf(8, 2541, 2286, 66016)

    private val games = input.split('\n').map { it.toGame() }

    override fun solvePart1() = games.filter { game ->
        game.grabs.all { it.valid(12, 13, 14) }
    }.sumOf { it.id }

    override fun solvePart2(): Int = games.map { game ->
        game.calculateMinGrab()
    }.sumOf { it.red * it.green * it.blue }
}

data class Game(val id: Int, val grabs: List<Grab>) {
    fun calculateMinGrab() = Grab(
        grabs.maxBy { it.red }.red,
        grabs.maxBy { it.green }.green,
        grabs.maxBy { it.blue }.blue
    )
}

private fun String.toGame() = Game(
    id = substringBefore(':').split(' ')[1].toInt(),
    grabs = substringAfter(':').trim().split(';').map { it.toGrab() }
)

data class Grab(val red: Int, val green: Int, val blue: Int) {
    fun valid(totalRed: Int, totalGreen: Int, totalBlue: Int): Boolean {
        return red <= totalRed && green <= totalGreen && blue <= totalBlue
    }
}

private fun String.toGrab(): Grab {
    val cubes = split(',').map { it.trim() }
    var red = 0
    var green = 0
    var blue = 0
    for (color in cubes) {
        when {
            color.contains("red") -> red = color.split(' ')[0].toInt()
            color.contains("green") -> green = color.split(' ')[0].toInt()
            color.contains("blue") -> blue = color.split(' ')[0].toInt()
        }
    }
    return Grab(red, green, blue)
}

fun main() {
    val name = Day02::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day02(testInput), Day02(realInput))
}