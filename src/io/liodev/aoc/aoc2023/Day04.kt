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

    override fun solvePart2(): Int {
        var acum = 0
        val numCards = MutableList(cards.size) { 1 }
        for ((i, card) in cards.withIndex()) {
            acum += numCards[i]
            repeat(card.winnerNumbers) { offset ->
                numCards[i + 1 + offset] += numCards[i]
            }
        }
        return acum
    }

    data class Card(val winnerNumbers: Int)

    private fun String.toCard(): Card {
        val (ws, ns) = this.substringAfter(':').split("|")
        val w = ws.split(" ").filter { it != "" }.map { it.toInt() }
        val n = ns.split(" ").filter { it != "" }.map { it.toInt() }
        return Card(n.count { it in w })
    }
}


fun main() {
    val name = Day04::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day04(testInput), Day04(realInput))
}