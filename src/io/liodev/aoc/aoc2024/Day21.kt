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
        return codes.sumOf { code -> moveRobotKeypadRec(code, 2) * code.dropLast(1).toInt() }
    }

    override fun solvePart2(): Long {
        cacheMap.clear()
        return codes.sumOf { code -> moveRobotKeypadRec(code, 25) * code.dropLast(1).toInt() }
    }

    private fun moveRobotKeypadRec(
        code: String,
        depth: Int,
    ): Long {
        var prev = 'A'
        return code.sumOf { c ->
            val options = movesKeypad(prev, c)
            prev = c
            options.minOf { o ->
                moveRobotDpadRec(o, depth, 1)
            }
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
                    options.minOf { o ->
                        moveRobotDpadRec(o, depth, level + 1)
                    }
                }
            }
        }

    // UNUSED
    private fun moveRobotKeypadLength(
        code: String,
        n: Int,
    ): Long {
        var currentPos = 'A'
        var length = 0L
        for (c in code) {
            val options = movesKeypad(currentPos, c)
            val optionsSize = mutableListOf<Int>()
            for (o in options) {
                var codes = listOf(o)
                repeat(n) {
                    codes = listOf(moveRobotDpad(codes))
                }
                optionsSize.add(codes[0].length)
            }
            length += optionsSize.min()
            currentPos = c
        }
        return length
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
        return when {
            (currentPos == dest) -> listOf("A")
            (rowKpad(currentPos) == rowKpad(dest)) ->
                listOf(
                    moveHorizontallyKpad(
                        currentPos,
                        dest,
                    ) + "A",
                )

            else -> {
                val times = abs(rowKpad(dest) - rowKpad(currentPos))
                val horizontal = moveHorizontallyKpad(currentPos, dest)
                val vertical =
                    if (rowKpad(currentPos) < rowKpad(dest)) {
                        "v".repeat(times)
                    } else {
                        "^".repeat(times)
                    }
                return if (rowKpad(currentPos) == 3 && colKpad(dest) == 0) {
                    listOf(vertical + horizontal + "A")
                } else if (colKpad(currentPos) == 0 && rowKpad(dest) == 3) {
                    listOf(horizontal + vertical + "A")
                } else if (colKpad(currentPos) == colKpad(dest)) {
                    listOf(vertical + "A")
                } else {
                    listOf(horizontal + vertical + "A", vertical + horizontal + "A")
                }
            }
        }
    }

    private fun moveHorizontallyKpad(
        currentPos: Char,
        dest: Char,
    ): String {
        val times = abs(colKpad(dest) - colKpad(currentPos))
        return if (colKpad(currentPos) < colKpad(dest)) {
            ">".repeat(times)
        } else {
            "<".repeat(times)
        }
    }

    private fun moveRobotDpad(combinations: List<String>): String {
        val result = StringBuilder()
        for (combination in combinations) {
            var currentPos = 'A'
            for (c in combination) {
                val options = movesDpad(currentPos, c)
                result.append(options.minByOrNull { moveRobotDpadCalc(moveRobotDpadList(listOf(it))) })
                currentPos = c
            }
        }
        return result.toString()
    }

    private fun moveRobotDpadList(combinations: List<String>): List<String> {
        val total = mutableListOf<String>()
        var min = Int.MAX_VALUE
        for (combination in combinations) {
            var results = listOf("")
            var currentPos = 'A'
            for (c in combination) {
                val options = movesDpad(currentPos, c)
                results = results.flatMap { result -> options.map { option -> result + option } }
                currentPos = c
            }
            min = results.minOf { it.length }.coerceAtMost(min)
            total.addAll(results.filter { it.length == min })
        }
        return total.filter { it.length == min }
    }

    private fun moveRobotDpadCalc(combinations: List<String>): Int {
        var min = Int.MAX_VALUE
        for (combination in combinations) {
            var results = listOf("")
            var currentPos = 'A'
            for (c in combination) {
                val options = movesDpad(currentPos, c)
                results = results.flatMap { result -> options.map { option -> result + option } }
                currentPos = c
            }
            min = results.minOf { it.length }.coerceAtMost(min)
        }
        return min
    }

    private fun movesDpad(
        currentPos: Char,
        dest: Char,
    ): List<String> {
        return when {
            (currentPos == dest) -> listOf("A")
            (rowDpad(currentPos) == rowDpad(dest)) ->
                listOf(
                    moveHorizontallyDpad(
                        currentPos,
                        dest,
                    ) + "A",
                )

            else -> {
                val horizontal = moveHorizontallyDpad(currentPos, dest)
                val vertical =
                    if (rowDpad(currentPos) < rowDpad(dest)) {
                        "v"
                    } else {
                        "^"
                    }
                if (colDpad(currentPos) == 0 && rowDpad(dest) == 0) {
                    return listOf(horizontal + vertical + "A")
                } else if (rowDpad(currentPos) == 0 && colDpad(dest) == 0) {
                    return listOf(vertical + horizontal + "A")
                } else if (colDpad(currentPos) == colDpad(dest)) {
                    return listOf(vertical + "A")
                } else {
                    return listOf(horizontal + vertical + "A", vertical + horizontal + "A")
                }
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

    private fun moveHorizontallyDpad(
        currentPos: Char,
        dest: Char,
    ): String {
        val times = abs(colDpad(dest) - colDpad(currentPos))
        return if (colDpad(currentPos) < colDpad(dest)) {
            ">".repeat(times)
        } else {
            "<".repeat(times)
        }
    }
}

inline fun <R> List<List<Char>>.mapIndexed2NotNull(transform: (i: Int, j: Int, c: Char) -> R?): List<R> =
    buildList {
        forEachIndexed2 { i, j, c ->
            transform(i, j, c)?.let { add(it) }
        }
    }

inline fun List<List<Char>>.forEachIndexed2(action: (i: Int, j: Int, c: Char) -> Unit) {
    for (i in indices) {
        val b = get(i)
        for (j in b.indices) {
            action(i, j, b[j])
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
