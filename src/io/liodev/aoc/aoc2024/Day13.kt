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
            val solutions = mutableListOf<Pair<Int, Int>>()

            for (n in 0..1000) {
                for (m in 0..1000) {
                    if (buttonA.first * n + buttonB.first * m == prize.first && buttonA.second * n + buttonB.second * m == prize.second) {
                        solutions += Pair(n, m)
                        break
                    }
                }
            }
            if (solutions.isEmpty()) return null
            return solutions.minOf { it.first * 3L + it.second }
        }

        fun minTokensToWinCorrectedPrize(): Long? {
            val (correctXp, correctYp) = prize.first + 10000000000000.0 to prize.second + 10000000000000.0
            val (xa, ya) = buttonA.first.toDouble() to buttonA.second.toDouble()
            val (xb, yb) = buttonB.first.toDouble() to buttonB.second.toDouble()
            val solutions = mutableListOf<Pair<Long, Long>>()

            val d = (yb - xb * ya / xa)
            val n = ((correctYp - correctXp / xa * ya) / d).roundToLong()
            val m = ((correctXp - n * xb) / xa).roundToLong()

            val xp = m * xa + n * xb
            val yp = m * ya + n * yb
            if (n > 0 && m > 0 && xp == correctXp && yp == correctYp) solutions += Pair(m, n)

            if (solutions.isEmpty()) return null
            return solutions.minOf { it.first * 3L + it.second }
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

    override fun solvePart2(): Long = machines.mapNotNull { it.minTokensToWinCorrectedPrize() }.sum()
}

fun main() {
    val name = Day13::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day13(testInput), Day13(realInput), year)
}
