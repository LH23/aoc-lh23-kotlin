package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 22: Monkey Market ---
class Day22(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(37990510L, 15335183969, 23, 1696)

    private val initialSecrets = input.lines().map { it.toLong() }

    override fun solvePart1(): Long = initialSecrets.sumOf { it.secretNumsSequence().drop(2000).first() }

    override fun solvePart2(): Long {
        val pricesMap = mutableMapOf<List<Int>, Int>()

        for (num in initialSecrets) {
            val secrets = num.secretNumsSequence().take(2000)
            val diffs = (listOf(num) + secrets).zipWithNext().map { (a, b) -> (b % 10 - a % 10).toInt() }
            val prices = secrets.map { (it % 10).toInt() }.toList()
            val seen = mutableSetOf<List<Int>>()

            diffs.windowed(4).forEachIndexed { i, seq ->
                if (seq !in seen) {
                    pricesMap[seq] = pricesMap.getOrDefault(seq, 0) + prices[i + 3]
                    seen += seq
                }
            }
        }
        return pricesMap.maxOf { it.value }.toLong()
    }

    private fun Long.secretNumsSequence() =
        generateSequence(this) {
            nextSecretNum(it)
        }

    private fun nextSecretNum(tmp: Long): Long {
        var secret = tmp
        secret = prune(mix(secret * 64, secret))
        secret = prune(mix(secret / 32, secret))
        secret = prune(mix(secret * 2048, secret))
        return secret
    }

    private fun mix(a: Long, b: Long): Long = a xor b

    private fun prune(num: Long): Long = num % 16777216
}


fun main() {
    val name = Day22::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day22(testInput), Day22(realInput), year, printTimings = true)
}
