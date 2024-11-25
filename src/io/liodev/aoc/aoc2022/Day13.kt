package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import java.util.Stack

// --- Day 13 2022: Distress Signal ---
class Day13(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(13, 5557, 140, 22425)

    private val pairs = input.split("\n\n").map { it.toPairOfPackets() }
    private val fullList = input.lines().filter { it.isNotEmpty() }.map { Packet.fromString(it) }

    override fun solvePart1() = pairs.mapIndexed { i, pair -> if (rightOrder(pair)) (i + 1) else 0 }.sum()

    override fun solvePart2(): Int {
        val divider2 = Packet.fromString("[[2]]")
        val divider6 = Packet.fromString("[[6]]")
        val sorted = (fullList + listOf(divider2, divider6)).sorted()
        return (sorted.indexOf(divider2) + 1) * (sorted.indexOf(divider6) + 1)
    }

    private fun rightOrder(pair: Pair<Packet, Packet>) = pair.first < pair.second

    private fun String.toPairOfPackets() =
        split("\n").let {
            Packet.fromString(it[0]) to Packet.fromString(it[1])
        }

    sealed class Packet : Comparable<Packet> {
        data class Item(
            val number: Int,
        ) : Packet() {
            override fun compareTo(other: Packet) =
                if (other is Item) {
                    number.compareTo(other.number)
                } else {
                    asList().compareTo(other)
                }

            fun asList() = ListItems(listOf(Item(number)))

            override fun toString() = number.toString()
        }

        data class ListItems(
            val items: List<Packet>,
        ) : Packet() {
            override fun compareTo(other: Packet): Int =
                when (other) {
                    is ListItems ->
                        this.items
                            .zip(other.items)
                            .map { (a, b) -> a.compareTo(b) }
                            .firstOrNull { it != 0 } ?: items.size.compareTo(other.items.size)

                    is Item -> this.compareTo(other.asList())
                }

            override fun toString() = items.toString()
        }

        companion object {
            fun fromString(string: String): Packet =
                if (string.isNumeric()) {
                    Item(string.toInt())
                } else {
                    val items = mutableListOf<Packet>()
                    val stack = Stack<Int>()
                    var prev = 1
                    for (i in 1..<string.lastIndex) {
                        when (string[i]) {
                            '[' -> stack.push(i)
                            ']' -> stack.pop()
                            ',' ->
                                if (stack.isEmpty()) {
                                    items.add(fromString(string.substring(prev, i)))
                                    prev = i + 1
                                }
                        }
                    }
                    if (prev < string.lastIndex) {
                        items.add(fromString(string.substring(prev, string.lastIndex)))
                    }
                    ListItems(items)
                }

            private fun String.isNumeric(): Boolean = toIntOrNull() != null
        }
    }
}

fun main() {
    val name = Day13::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day13(testInput), Day13(realInput), year)
}
