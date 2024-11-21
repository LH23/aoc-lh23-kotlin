package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2023 Day 15: Lens Library ---
class Day15(input: String): Day<Int> {

    override val expectedValues = listOf(1320, 512283, 145, 215827)

    private val initSequence = input.split(",")

    override fun solvePart1(): Int {
        return initSequence.sumOf { hash(it) }
    }

    private fun hash(s: String): Int {
        var current = 0
        for (c in s) {
            current += c.code
            current = current * 17 % 256
        }
        return current
    }

    override fun solvePart2() : Int {
        val boxes = HashMap<Int, MutableList<Pair<String, Int>>>()
        for (step in initSequence) {
            val label = step.split('-', '=')[0]
            val key = hash(label)
            val box = boxes.getOrPut(key) { mutableListOf() }
            if (step.contains('-')) {
                box.removeIf { it.first == label }
            } else {
                val focalLength = step.substringAfter('=').toInt()
                val labeled = box.find { it.first == label }
                if (labeled == null) box.add(label to focalLength)
                else box[box.indexOf(labeled)] = label to focalLength
            }
        }
        return boxes.keys.sumOf { num ->
            (num + 1) * boxes[num]!!.mapIndexed { i, (_, focalLength) ->
                (i + 1) * focalLength
            }.sum()
        }
    }
}

fun main() {
    val name = Day15::class.simpleName
    val year = 2023
    val testInput= readInputAsString("src/input/$year/${name}_test.txt")
    val realInput= readInputAsString("src/input/$year/${name}.txt")
    runDay(Day15(testInput), Day15(realInput), year)
}