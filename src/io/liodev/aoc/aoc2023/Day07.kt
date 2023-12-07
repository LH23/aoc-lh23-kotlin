package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2023 Day 7: Camel Cards ---
class Day07(input: String) : Day<Long> {
    override val expectedValues = listOf(6440L, 255048101, 5905, 253718286)

    private val cardsWithBid = input
        .split("\n")
        .map { it.toHandWithBid() }

    private fun String.toHandWithBid(): Pair<Hand, Int> =
        Pair(
            Hand(cards = this.substringBefore(" ").toList()),
            this.substringAfter(" ").toInt()
        )

    data class Hand(val cards: List<Char>, val jokerEnabled: Boolean = false) {

        enum class Type(val rank: Int) {
            FIVE(7),
            FOUR(6),
            FULLHOUSE(5),
            THREE(4),
            TWOPAIRS(3),
            PAIR(2),
            HIGHCARD(1),
        }

        fun type(): Type {
            val cardsToUse = if (jokerEnabled) cards.replaceJ() else cards
            val groupSizes = cardsToUse.groupingBy { it }.eachCount().values.sortedDescending()
            return when {
                groupSizes[0] == 5 -> Type.FIVE
                groupSizes[0] == 4 -> Type.FOUR
                groupSizes[0] == 3 && groupSizes[1] == 2 -> Type.FULLHOUSE
                groupSizes[0] == 3 -> Type.THREE
                groupSizes[0] == 2 && groupSizes[1] == 2 -> Type.TWOPAIRS
                groupSizes[0] == 2 -> Type.PAIR
                else -> Type.HIGHCARD
            }
        }

        private fun List<Char>.replaceJ(): List<Char> {
            val noJ = this.filter { it != 'J' }
            val joker = noJ.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
            return if (joker == null) this
            else this.map { if (it == 'J') joker else it }
        }

        fun values(): String = (0..4).map { i -> this.value(i) }.joinToString("")

        private fun value(i: Int): Char {
            return when (val c = cards[i]) {
                'A' -> 'z'
                'K' -> 'y'
                'Q' -> 'x'
                'J' -> if (jokerEnabled) '0' else 'w'
                'T' -> 'v'
                else -> c
            }
        }

    }

    override fun solvePart1(): Long = cardsWithBid
        .sortedWith(compareBy({ it.first.type().rank }, { it.first.values() }))
        .withIndex().sumOf { (i, pair) ->
            (i + 1) * pair.second.toLong()
        }

    override fun solvePart2(): Long = cardsWithBid
        .map { it.first.copy(jokerEnabled = true) to it.second }
        .sortedWith(compareBy({ it.first.type().rank }, { it.first.values() }))
        .withIndex().sumOf { (i, pair) ->
            (i + 1) * pair.second.toLong()
        }
}

fun main() {
    val name = Day07::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day07(testInput), Day07(realInput), printTimings = true)
}