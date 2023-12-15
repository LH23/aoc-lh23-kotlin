package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// 2023 Day15
class Day15(input: String): Day<Int> {
    //After "rn=1":
    //Box 0: [rn 1]
    //
    //After "cm-":
    //Box 0: [rn 1]
    //
    //After "qp=3":
    //Box 0: [rn 1]
    //Box 1: [qp 3]
    //
    //After "cm=2":
    //Box 0: [rn 1] [cm 2]
    //Box 1: [qp 3]
    //
    //After "qp-":
    //Box 0: [rn 1] [cm 2]
    //
    //After "pc=4":
    //Box 0: [rn 1] [cm 2]
    //Box 3: [pc 4]
    //
    //After "ot=9":
    //Box 0: [rn 1] [cm 2]
    //Box 3: [pc 4] [ot 9]
    //
    //After "ab=5":
    //Box 0: [rn 1] [cm 2]
    //Box 3: [pc 4] [ot 9] [ab 5]
    //
    //After "pc-":
    //Box 0: [rn 1] [cm 2]
    //Box 3: [ot 9] [ab 5]
    //
    //After "pc=6":
    //Box 0: [rn 1] [cm 2]
    //Box 3: [ot 9] [ab 5] [pc 6]
    //
    //After "ot=7":
    //Box 0: [rn 1] [cm 2]
    //Box 3: [ot 7] [ab 5] [pc 6]


    //rn: 1 (box 0) * 1 (first slot) * 1 (focal length) = 1
    //cm: 1 (box 0) * 2 (second slot) * 2 (focal length) = 4
    //ot: 4 (box 3) * 1 (first slot) * 7 (focal length) = 28
    //ab: 4 (box 3) * 2 (second slot) * 5 (focal length) = 40
    //pc: 4 (box 3) * 3 (third slot) * 6 (focal length) = 72

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
    val testInput= readInputAsString("src/input/2023/${name}_test.txt")
    val realInput= readInputAsString("src/input/2023/${name}.txt")
    runDay(Day15(testInput), Day15(realInput))
}