package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- Day 11 2022: Monkey in the Middle ---
class Day11(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(10605L, 56350, 2713310158, 13954061248)

    private val monkeys = input.split("\n\n").map { Monkey.fromString(it) }
    private val lcm = monkeys.map { it.divisible }.reduce { acc, i -> acc * i }

    override fun solvePart1() = calculateMonkeyBusiness(20, 3)

    override fun solvePart2() = calculateMonkeyBusiness(10000)

    private fun calculateMonkeyBusiness(
        rounds: Int,
        reduceWorryLevelBy: Int = 1,
    ): Long {
        val activity = IntArray(monkeys.size) { 0 }
        val monkeysItems = Array(monkeys.size) { monkeys[it].items.toMutableList() }

        repeat(rounds) {
            monkeys.forEachIndexed { i, monkey ->
                activity[i] += monkeysItems[i].size
                while (monkeysItems[i].isNotEmpty()) {
                    val item = monkeysItems[i].removeFirst()
                    val worryLevel = monkey.inspect(item % lcm) / reduceWorryLevelBy

                    val destination =
                        if (worryLevel % monkey.divisible == 0L) monkey.ifTrue else monkey.ifFalse
                    monkeysItems[destination].addLast(worryLevel)
                }
            }
        }
        return activity.sortedDescending().take(2).let { it[0].toLong() * it[1] }
    }

    data class Monkey(
        val items: List<Long>,
        val inspect: (Long) -> Long,
        val divisible: Long,
        val ifTrue: Int,
        val ifFalse: Int,
    ) {
        companion object {
            internal fun fromString(monkeyString: String) =
                monkeyString.lines().let {
                    Monkey(
                        it[1].substringAfter(": ").split(", ").map { item -> item.toLong() },
                        it[2].substringAfter("new = ").let {
                            { old: Long ->
                                val (s1, op, s2) = it.split(' ')
                                val n1 = if (s1 == "old") old else s1.toLong()
                                val n2 = if (s2 == "old") old else s2.toLong()
                                when (op) {
                                    "+" -> n1 + n2
                                    "*" -> n1 * n2
                                    else -> throw IllegalArgumentException("Invalid op: $op")
                                }
                            }
                        },
                        it[3].substringAfter("by ").toLong(),
                        it[4].substringAfter("monkey ").toInt(),
                        it[5].substringAfter("monkey ").toInt(),
                    )
                }
        }
    }
}

fun main() {
    val name = Day11::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day11(testInput), Day11(realInput), year, printTimings = true)
}
