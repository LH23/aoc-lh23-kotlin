package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import kotlinx.coroutines.*
import io.liodev.aoc.utils.lcm

// --- 2023 Day 8: Haunted Wasteland ---
class Day08(input: String) : Day<Long> {
    override val expectedValues = listOf(6L, 19667, 6, 19185263738117)

    private val document = input.split("\n\n").toDocument()

    data class Document(val steps: String, val inst: Map<String, Pair<String, String>>)

    private fun List<String>.toDocument(): Document {
        val instructions = this[1]
            .split('\n')
            .map { it.split(" = (", ", ", ")") }
            .associate { (it[0] to Pair(it[1], it[2])) }
        return Document(this[0], instructions)
    }

    override fun solvePart1(): Long {
        return calculateSteps(listOf("AAA")) { dest -> dest == "ZZZ" }
    }

    override fun solvePart2(): Long {
        return calculateSteps(document.inst.keys.filter { it.endsWith("A") }) { dest ->
            dest.endsWith("Z")
        }
    }

    private fun calculateSteps(origins: List<String>, ends: (String) -> Boolean): Long {
        val dests = origins.toMutableList()
        val set = mutableSetOf<Long>()

        for (i in dests.indices) {
            var pointerI = 0
            var stepsI = 0L
            var dst = dests[i]
            while (true) {
                val dir = document.steps[pointerI]
                dst = if (dir == 'L') document.inst[dst]!!.first else document.inst[dst]!!.second
                stepsI++
                if (ends(dst)) {
                    set.add(stepsI)
                    break
                }
                pointerI = (pointerI + 1) % document.steps.length
            }
        }
        return lcm(set.toList())
    }
}

fun main() {
    val name = Day08::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day08(testInput), Day08(realInput), printTimings = true)
}