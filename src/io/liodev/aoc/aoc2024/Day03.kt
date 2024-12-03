package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

class Day03(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(322L, 184576302, 96, 118173507)

    private val corruptedMemory = input.split("\n")

    override fun solvePart1(): Long =
        corruptedMemory.sumOf { line ->
            val regex = """mul\((\d\d?\d?,\d\d?\d?)\)""".toRegex()
            val matches = regex.findAll(line)
            matches.sumOf { match ->
                match.multiply()
            }
        }

    override fun solvePart2(): Long =
        corruptedMemory.joinToString ("").let { program ->
            val cleanLine = program.processDoDontInstructions()
            val regex = """mul\((\d\d?\d?,\d\d?\d?)\)""".toRegex()
            val matches = regex.findAll(cleanLine)
            matches.sumOf { match ->
                //println("match: ${match.value}")
                match.multiply()
            }
        }
}

private fun MatchResult.multiply(): Long =
    value
        .substringAfter("(")
        .substringBefore(")")
        .split(',')
        .map { it.toLong() }
        .reduce { a, b -> a * b }

private fun String.processDoDontInstructions(): String {
    var next = 0
    var result = ""
    while (next > -1) {
        val nextDont = this.indexOf("don't()", next)
        if (nextDont == -1) {
            result += this.substring(next)
            break
        }
        result += this.substring(next, nextDont + 7)
        next = this.indexOf("do()", nextDont)
    }
    //println("processDoDontInstructions: $result")
    return result
}

fun main() {
    val name = Day03::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day03(testInput), Day03(realInput), year)
}
