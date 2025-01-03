package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 5: Print Queue ---
class Day05(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(143, 6505, 123, 6897)

    private val orderingRules = OrderingRules.from(input.split("\n\n")[0])
    private val updates =
        input
            .split("\n\n")[1]
            .lines()
            .map { line -> line.split(",").map { page -> page.toInt() } }

    override fun solvePart1(): Int =
        updates
            .filter { orderingRules.ordered(it) }
            .sumOf { pages -> pages[pages.size / 2] }

    override fun solvePart2(): Int =
        updates
            .filter { !orderingRules.ordered(it) }
            .sumOf { pages -> orderingRules.sort(pages)[pages.size / 2] }

    data class OrderingRules(
        val rules: Set<Pair<Int, Int>>,
    ) {
        fun ordered(pages: List<Int>): Boolean =
            pages.zipWithNext().all { (firstPage, secondPage) ->
                compare(firstPage, secondPage) <= 0
            }

        // slower version
        fun order(pageNumbers: List<Int>): List<Int> {
            val reorderedPages = mutableListOf<Int>()
            for (page in pageNumbers) {
                val putAfterIndex =
                    rules
                        .filter { it.second == page && it.first in reorderedPages }
                        .map { it.first }
                        .maxOfOrNull { reorderedPages.indexOf(it) } ?: -1
                reorderedPages.add(putAfterIndex + 1, page)
            }
            return reorderedPages.toList()
        }

        fun sort(pageNumbers: List<Int>): List<Int> = pageNumbers.sortedWith { a, b -> compare(a, b) }

        private fun compare(p1: Int, p2: Int): Int =
            when {
                p1 to p2 in rules -> -1
                p2 to p1 in rules -> 1
                else -> 0
            }

        companion object {
            fun from(s: String): OrderingRules {
                val list = s.lines().map { it.split("|").map { it.toInt() } }
                return OrderingRules(list.map { it[0] to it[1] }.toSet())
            }
        }
    }
}

fun main() {
    val name = Day05::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day05(testInput), Day05(realInput), year, printTimings = true)
}
