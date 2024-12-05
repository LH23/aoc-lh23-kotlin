package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 5: Print Queue ---
class Day05(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(143, 6505, 123, 6897)

    private val parsedInput =
        input.split("\n\n").let {
            OrderingRules.from(it[0]) to it[1].lines().map { updateLine -> Update.from(updateLine) }
        }

    override fun solvePart1(): Int {
        val (orderingRules, updates) = parsedInput
        return updates
            .filter { update -> orderingRules.ordered(update.pageNumbers) }
            .sumOf { it.pageNumbers[it.pageNumbers.size / 2] }
    }

    override fun solvePart2(): Int {
        val (orderingRules, updates) = parsedInput
        return updates
            .filter { update -> !orderingRules.ordered(update.pageNumbers) }
            .sumOf { orderingRules.order(it.pageNumbers)[it.pageNumbers.size / 2] }
    }

    data class Update(
        val pageNumbers: List<Int>,
    ) {
        companion object {
            fun from(updateLine: String): Update {
                val pageNumbers = updateLine.split(",").map { it.toInt() }
                return Update(pageNumbers)
            }
        }
    }

    data class OrderingRules(
        val rules: List<Pair<Int, Int>>,
    ) {
        fun ordered(updatePages: List<Int>): Boolean =
            rules.all { (first, second) ->
                if (second in updatePages) {
                    updatePages.lastIndexOf(first) < updatePages.indexOf(second)
                } else {
                    true
                }
            }

        fun order(pageNumbers: List<Int>): List<Int> {
            val reorderedPages = mutableListOf<Int>()
            for (n in pageNumbers) {
                val putAfter =
                    rules
                        .filter { it.second == n && it.first in reorderedPages }
                        .map { it.first }
                val afterIndex = putAfter.maxOfOrNull { reorderedPages.indexOf(it) } ?: -1
                reorderedPages.add(afterIndex + 1, n)
            }
            return reorderedPages.toList()
        }

        companion object {
            fun from(s: String): OrderingRules {
                val list = s.lines().map { it.split("|").map { it.toInt() } }
                return OrderingRules(list.map { it[0] to it[1] })
            }
        }
    }
}

fun main() {
    val name = Day05::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day05(testInput), Day05(realInput), year)
}
