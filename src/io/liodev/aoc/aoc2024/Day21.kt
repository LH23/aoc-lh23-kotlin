package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import kotlin.math.abs

// --- 2024 Day 21: Keypad Conundrum ---
class Day21(
    val input: String,
) : Day<Long> {
    override val expectedValues = listOf(126384L, 231564, 154115708116294, 281212077733592)

    private val codes = input.lines()
    private var cacheMap = mutableMapOf<Pair<String, Int>, Long>()

    override fun solvePart1(): Long {
        cacheMap.clear()
        return codes.sumOf { code -> moveRobot(code, 2) * code.dropLast(1).toInt() }
    }

    override fun solvePart2(): Long {
        cacheMap.clear()
        return codes.sumOf { code -> moveRobot(code, 25) * code.dropLast(1).toInt() }
    }

    private fun moveRobot(
        code: String,
        depth: Int,
    ): Long {
        var prev = 'A'
        return code.sumOf { c ->
            val options = movesKeypad(prev, c)
            prev = c
            options.minOf { o -> moveRobotDpadRec(o, depth, 1) }
        }
    }

    private fun moveRobotDpadRec(
        code: String,
        depth: Int,
        level: Int,
    ): Long =
        if (depth + 1 == level) {
            code.length.toLong()
        } else {
            cacheMap.getOrPut(code to level) {
                var prev = 'A'
                code.sumOf { c ->
                    val options = movesDpad(prev, c)
                    prev = c
                    options.minOf { o -> moveRobotDpadRec(o, depth, level + 1) }
                }
            }
        }

    private fun rowKpad(c: Char): Int =
        when (c) {
            '7', '8', '9' -> 0
            '4', '5', '6' -> 1
            '1', '2', '3' -> 2
            '0', 'A' -> 3
            else -> throw IllegalArgumentException("Invalid KPAD character: $c")
        }

    private fun colKpad(c: Char) =
        when (c) {
            '7', '4', '1' -> 0
            '8', '5', '2', '0' -> 1
            '9', '6', '3', 'A' -> 2
            else -> throw IllegalArgumentException("Invalid KPAD character: $c")
        }

    private fun movesKeypad(
        currentPos: Char,
        dest: Char,
    ): List<String> {
        return if (currentPos == dest) {
            listOf("A")
        } else {
            val diffCol = colKpad(currentPos) - colKpad(dest)
            val horizontal = (if (diffCol < 0) ">" else "<").repeat(abs(diffCol))
            val diffRow = rowKpad(currentPos) - rowKpad(dest)
            val vertical = (if (diffRow < 0) "v" else "^").repeat(abs(diffRow))

            return when {
                rowKpad(currentPos) == 3 && colKpad(dest) == 0 -> listOf(vertical + horizontal + "A")
                colKpad(currentPos) == 0 && rowKpad(dest) == 3 -> listOf(horizontal + vertical + "A")
                colKpad(currentPos) == colKpad(dest) -> listOf(vertical + "A")
                vertical.isEmpty() || horizontal.isEmpty() -> listOf(vertical + horizontal + "A")
                else -> listOf(horizontal + vertical + "A", vertical + horizontal + "A")
            }
        }
    }

    private fun rowDpad(c: Char): Int =
        when (c) {
            '^', 'A' -> 0
            '<', 'v', '>' -> 1
            else -> throw IllegalArgumentException("Invalid DPAD character: $c")
        }

    private fun colDpad(c: Char) =
        when (c) {
            '<' -> 0
            'v', '^' -> 1
            '>', 'A' -> 2
            else -> throw IllegalArgumentException("Invalid DPAD character: $c")
        }

    private fun movesDpad(
        currentPos: Char,
        dest: Char,
    ): List<String> {
        return if (currentPos == dest) {
            listOf("A")
        } else {
            val diffCol = colDpad(currentPos) - colDpad(dest)
            val horizontal = (if (diffCol < 0) ">" else "<").repeat(abs(diffCol))
            val diffRow = rowDpad(currentPos) - rowDpad(dest)
            val vertical = (if (diffRow < 0) "v" else "^").repeat(abs(diffRow))

            return when {
                colDpad(currentPos) == 0 && rowDpad(dest) == 0 -> listOf(horizontal + vertical + "A")
                rowDpad(currentPos) == 0 && colDpad(dest) == 0 -> listOf(vertical + horizontal + "A")
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
