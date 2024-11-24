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

    private val pairs = input.split("\n\n").map { it.split("\n").toPairOfPackets() }
    private val fullList = input.lines().filter { it.isNotEmpty() }.map { Packet.fromString(it) }

    override fun solvePart1() = pairs.mapIndexed { i, pair -> if (rightOrder(pair)) (i + 1) else 0 }.sum()

    private fun rightOrder(pair: Pair<Packet, Packet>): Boolean {
        val left = pair.first
        val right = pair.second
//        println("Left: $left")
//        println("Right: $right")
        return left < right
    }

    override fun solvePart2(): Int {
        val decoder2 = Packet.fromString("[[2]]")
        val decoder6 = Packet.fromString("[[6]]")
        val sorted = (fullList + listOf(decoder2, decoder6)).sorted()
        return (sorted.indexOf(decoder2) + 1) * (sorted.indexOf(decoder6) + 1)
    }

    private fun List<String>.toPairOfPackets(): Pair<Packet, Packet> = Packet.fromString(this[0]) to Packet.fromString(this[1])

    sealed class Packet : Comparable<Packet> {
        data class Item(
            val number: Int,
        ) : Packet() {
            override fun compareTo(other: Packet) =
                if (other is Item) {
                    number.compareTo(other.number)
                } else {
                    ListItems(listOf(Item(number))).compareTo(other)
                }

            override fun toString() = number.toString()
        }

        data class ListItems(
            val items: List<Packet>,
        ) : Packet() {
            override fun compareTo(other: Packet): Int {
                return if (other is ListItems) {
                    for (i in items.indices) {
                        return when {
                            i >= other.items.size || items[i] > other.items[i] -> 1
                            items[i] < other.items[i] -> -1
                            else -> continue
                        }
                    }
                    return if (other.items.size > items.size) -1 else 0
                } else {
                    -1 * other.compareTo(this)
                }
            }

            override fun toString() = "[" + items.joinToString { it.toString() } + "]"
        }

        override fun toString(): String =
            when (this) {
                is Item -> number.toString()
                is ListItems -> items.joinToString(",") { it.toString() }
            }

        companion object {
            fun fromString(string: String): Packet =
                if (string.isNumeric()) {
                    // println("fromString $string to Item")
                    Item(string.toInt())
                } else {
                    // println("fromString $string to ListItems")
                    val items = mutableListOf<Packet>()
                    val stack = Stack<Int>()
                    var prev = 1
                    for (i in 1..<string.lastIndex) {
                        val char = string[i]
                        when (char) {
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
