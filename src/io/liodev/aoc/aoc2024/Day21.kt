package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.findFirstOrNull
import kotlin.math.abs

// --- 2024 Day 21: Keypad Conundrum ---
class Day21(
    val input: String,
) : Day<Long> {
    override val expectedValues = listOf(126384L, 231564, 154115708116294, 281212077733592)

    private val codes = input.lines()
    private var cacheMap = mutableMapOf<Pair<String, Int>, Long>()

    override fun solvePart1(): Long = codes.sumOf { code -> moveRobot(code, 2) * code.dropLast(1).toInt() }

    override fun solvePart2(): Long = codes.sumOf { code -> moveRobot(code, 25) * code.dropLast(1).toInt() }

    private fun moveRobot(
        code: String,
        depth: Int,
    ): Long =
        "A$code".zipWithNext().sumOf { (curr, dest) ->
            val options = movesKeypad(curr, dest)
            options.minOf { o -> moveRobotDpadRec(o, depth, 1) }
        }

    private fun moveRobotDpadRec(
        code: String,
        depth: Int,
        level: Int,
    ): Long =
        cacheMap.getOrPut(Pair(code, (depth - level))) {
            "A$code".zipWithNext().sumOf { (curr, dest) ->
                val options = movesDpad(curr, dest)
                if (depth == level) {
                    options.minOf { it.length.toLong() }
                } else {
                    options.minOf { o -> moveRobotDpadRec(o, depth, level + 1) }
                }
            }
        }

    private val keypad =
        listOf(
            listOf('7', '8', '9'),
            listOf('4', '5', '6'),
            listOf('1', '2', '3'),
            listOf(' ', '0', 'A'),
        )

    private fun movesKeypad(
        current: Char,
        dest: Char,
    ): List<String> {
        return if (current == dest) {
            listOf("A")
        } else {
            val currCoord = keypad.findFirstOrNull(current)!!
            val destCoord = keypad.findFirstOrNull(dest)!!
            val diffCol = currCoord.c - destCoord.c
            val horizontal = (if (diffCol < 0) ">" else "<").repeat(abs(diffCol))
            val diffRow = currCoord.r - destCoord.r
            val vertical = (if (diffRow < 0) "v" else "^").repeat(abs(diffRow))

            return when {
                currCoord.r == 3 && destCoord.c == 0 -> listOf(vertical + horizontal + "A")
                currCoord.c == 0 && destCoord.r == 3 -> listOf(horizontal + vertical + "A")
                currCoord.c == destCoord.c -> listOf(vertical + "A")
                vertical.isEmpty() || horizontal.isEmpty() -> listOf(vertical + horizontal + "A")
                else -> listOf(horizontal + vertical + "A", vertical + horizontal + "A")
            }
        }
    }

    private val dpad =
        listOf(
            listOf(' ', '^', 'A'),
            listOf('<', 'v', '>'),
        )

    private fun movesDpad(
        current: Char,
        dest: Char,
    ): List<String> {
        return if (current == dest) {
            listOf("A")
        } else {
            val currCoord = dpad.findFirstOrNull(current)!!
            val destCoord = dpad.findFirstOrNull(dest)!!
            val diffCol = currCoord.c - destCoord.c
            val horizontal = (if (diffCol < 0) ">" else "<").repeat(abs(diffCol))
            val diffRow = currCoord.r - destCoord.r
            val vertical = (if (diffRow < 0) "v" else "^").repeat(abs(diffRow))

            return when {
                currCoord.c == 0 && destCoord.r == 0 -> listOf(horizontal + vertical + "A")
                currCoord.r == 0 && destCoord.c == 0 -> listOf(vertical + horizontal + "A")
                vertical.isEmpty() || horizontal.isEmpty() -> listOf(vertical + horizontal + "A")
                else -> listOf(horizontal + vertical + "A", vertical + horizontal + "A")
            }
        }
    }
}

fun main() {
    val name = Day21::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day21(testInput), Day21(realInput), year)
}
