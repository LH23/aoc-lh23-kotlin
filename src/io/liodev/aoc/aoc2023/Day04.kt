package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import kotlin.math.pow

// --- 2023 Day 4: Scratchcards ---
class Day04(input: String) : Day<Int> {
    override val expectedValues = listOf(13, 20667, 30, 5833065)

    private val cards = input.split("\n").map { it.toCard() }

    override fun solvePart1() = cards.sumOf { card ->
        (2.0.pow(card.winnerNumbers - 1)).toInt()
    }

    override fun solvePart2(): Int = cards.withIndex().let { cardsIndexed ->
        val numCards = MutableList(cards.size) { 1 }
        cardsIndexed.sumOf { (i, card) ->
            repeat(card.winnerNumbers) { offset ->
                numCards[i + 1 + offset] += numCards[i]
            }
            numCards[i]
        }
    }

    fun solvePart2Fold(): Int = cards.reversed().fold(emptyList<Int>()) { acc, card ->
        val sum = 1 + (0 until card.winnerNumbers).sumOf { acc[it] }
        listOf(sum) + acc
    }.sum()

    data class Card(val winnerNumbers: Int)

    private fun String.toCard(): Card {
        val (ws, ns) = this.substringAfter(':').split("|")
        val winners = ws.split(' ').filter { it != "" }.map { it.toInt() }
        val nums = ns.split(' ').filter { it != "" }.map { it.toInt() }
        return Card(nums.count { it in winners })
    }
}


fun main() {
    val name = Day04::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day04(testInput), Day04(realInput))
}