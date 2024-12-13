package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import kotlin.math.roundToLong

// --- 2024 Day 13: Claw Contraption ---
class Day13(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(480L, 37901, 875318608908, 77407675412647)
    private val machines = input.split("\n\n").map { Machine.from(it) }

    data class Machine(
        val buttonA: Pair<Long, Long>,
        val buttonB: Pair<Long, Long>,
        val prize: Pair<Long, Long>,
    ) {
        fun minTokensToWin(): Long? {
            val (xp, yp) = prize.first.toDouble() to prize.second.toDouble()
            val (xa, ya) = buttonA.first.toDouble() to buttonA.second.toDouble()
            val (xb, yb) = buttonB.first.toDouble() to buttonB.second.toDouble()

            val n = ((yp - xp / xa * ya) / (yb - xb * ya / xa)).roundToLong()
            val m = ((xp - n * xb) / xa).roundToLong()

            val cxp = m * xa + n * xb
            val cyp = m * ya + n * yb
            return if (n > 0 && m > 0 && cxp == xp && cyp == yp) {
                (m * 3 + n)
            } else {
                null
            }
        }

        companion object {
            fun from(it: String): Machine {
                val buttonA = it.lines()[0].substringAfter("Button A:").getPair()
                val buttonB = it.lines()[1].substringAfter("Button B:").getPair()
                val prize = it.lines()[2].substringAfter("Prize:").getPair()
                return Machine(buttonA, buttonB, prize)
            }

            private fun String.getPair() = this.split(",").let { Pair(it[0].drop(3).toLong(), it[1].drop(3).toLong()) }
        }
    }

    override fun solvePart1(): Long = machines.mapNotNull { it.minTokensToWin() }.sum()

    override fun solvePart2(): Long =
        machines
            .mapNotNull {
                it.copy(prize = it.prize + 10000000000000L).minTokensToWin()
            }.sum()

    private operator fun Pair<Long, Long>.plus(x: Long) = Pair(first + x, second + x)
}


fun main() {
    val name = Day13::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day13(testInput), Day13(realInput), year)
}
