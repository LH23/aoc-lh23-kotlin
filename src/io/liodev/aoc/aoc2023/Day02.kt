package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

class Day02(input: String): Day<Int> {
    // only 12 red cubes, 13 green cubes, and 14 blue cubes
    override val expectedValues = listOf(8, 2541, 2286, 66016)

    private val games = parseInput(input)
    private fun parseInput(input: String): List<Game> = input.split("\n").map {
        it.toGame()
    }

    override fun solvePart1() = games.filter { game ->
            game.grabs.all { grab -> grab.valid(12,13,14) }
    }.sumOf { it.id }

    // powers> 48, 12, 1560, 630, and 36, sumed> 2286
    override fun solvePart2(): Int {
        return games.map { game -> game.calculateMinGrab() }.sumOf {
            it.reduce { a, e -> a * e }
        }

    }
}

data class Game (val id: Int, val grabs: List<Grab>) {
    fun calculateMinGrab(): List<Int> {
        return listOf(
            grabs.maxBy { it.red }.red,
            grabs.maxBy { it.green }.green,
            grabs.maxBy { it.blue }.blue)
    }
}

private fun String.toGame() = Game(
    id = this.substringBefore(':').split(' ')[1].toInt(),
    grabs = this.substringAfter(':').trim().split(';').map { it.toGrab() }
)

data class Grab(val red: Int, val green: Int, val blue: Int) {
    fun valid(totalRed: Int, totalGreen: Int, totalBlue: Int): Boolean {
        return red <= totalRed && green <= totalGreen && blue <= totalBlue
    }
}

private fun String.toGrab(): Grab {
    val cubes = this.split(',').map { it.trim() }
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
    val testInput= readInputAsString("src/input/2023/${name}_test.txt")
    val realInput= readInputAsString("src/input/2023/${name}.txt")
    runDay(Day02(testInput), Day02(realInput))
}