package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 22: Monkey Market ---
class Day22(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(37990510L, 15335183969, 23, 1696)

    private val initialSecrets = input.lines().map { it.toInt() }

    override fun solvePart1(): Long {
        var sum = 0L
        for (num in initialSecrets) {
            var tmp = num.toLong()
            val repeat = 2000
            repeat(repeat) {
                tmp = nextSecretNum(tmp)
            }
            sum += tmp
        }
        return sum
    }

    override fun solvePart2(): Long {
        val diffs = List(initialSecrets.size) { mutableListOf<Int>() }
        val prices = List(initialSecrets.size) { mutableListOf<Int>() }
        for ((i, num) in initialSecrets.withIndex()) {
            var tmp = num.toLong()
            var oldTmp: Long
            val repeat = 2000
            repeat(repeat) {
                oldTmp = tmp
                tmp = nextSecretNum(tmp)
                diffs[i].add((tmp % 10 - oldTmp % 10).toInt())
                prices[i].add((tmp % 10).toInt())
            }
        }
        return maxBananas(diffs, prices)
    }

    private fun maxBananas(
        diffs: List<MutableList<Int>>,
        prices: List<MutableList<Int>>,
    ): Long {
        var maxBananas = 0
        val diffsPriceMap =
            List(diffs.size) { i ->
                buildMap {
                    for (seq in diffs[i].windowed(4).toSet()) {
                        val index = (0..diffs[i].lastIndex - 3).indexOfFirst { (0..3).all { j -> diffs[i][it + j] == seq[j] } } + 3
                        this[seq] = prices[i][index]
                    }
                }
            }

        val seen = mutableSetOf<List<Int>>()
        for (diff in diffs.take(2)) { // 2 is enough for my input
            for (seq in diff.windowed(4)) {
                if (seq in seen) continue
                val bananas = calculateBananas(seq, diffsPriceMap)
                if (bananas > maxBananas) {
                    maxBananas = bananas
                    //println("new maxBananas: $maxBananas with seq: $seq in diff ${diffs.indexOf(diff)}")
                }
                seen += seq
            }
        }
        return maxBananas.toLong()
    }

    private fun calculateBananas(seq: List<Int>, diffsList: List<Map<List<Int>, Int>>): Int {
        return diffsList.sumOf { it[seq] ?: 0 }
    }

    private fun calculateBananas(
        seq: List<Int>,
        diffsList: List<MutableList<Int>>,
        pricesList: List<MutableList<Int>>,
    ): Int =
        diffsList.zip(pricesList).sumOf { (diff, prices) ->
            val index = (0..diff.lastIndex - 3).indexOfFirst { (0..3).all { j -> diff[it + j] == seq[j] } }
            if (index == -1) 0 else prices[index + 3]
        }

    private fun nextSecretNum(tmp: Long): Long {
        var secret = tmp
        secret = prune(mix(secret * 64, secret))
        secret = prune(mix(secret / 32, secret))
        secret = prune(mix(secret * 2048, secret))
        return secret
    }

    private fun mix(
        a: Long,
        b: Long,
    ): Long = a xor b

    private fun prune(num: Long): Long = (num + 16777216) % 16777216
}

fun main() {
    val name = Day22::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day22(testInput), Day22(realInput), year, printTimings = true, benchmark = false)
}
