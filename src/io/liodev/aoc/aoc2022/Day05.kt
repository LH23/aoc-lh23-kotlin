package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import java.util.Stack

class Day05(private val input: String) : Day<String> {
    override val expectedValues = listOf("CMZ", "CWMTGHBDW", "MCD", "SSCGWJCRB")

    private val instructions = parseInstuctions(input)
    private fun parseInstuctions(input: String) = input
        .substring(input.indexOf("move "))
        .split('\n').map { inst ->
            val regex = Regex("""move (\d+) from (\d+) to (\d+)""")
            val (i, f, t) = regex.find(inst)!!.destructured
            Instruction(i.toInt(), f.toInt(), t.toInt())
        }

    private fun parseStacks(input: String) = ShipStacks(
        input
            .substring(0, input.indexOf("\n 1 "))
            .split('\n')
            .map { it.toRow() }
            .createStacks()
    )

    class ShipStacks(val stacks: List<Stack<Char>>) {
        fun moveWithCrateMover9000(inst: Instruction) {
            repeat(inst.num) {
                stacks[inst.to - 1].push(stacks[inst.from - 1].pop())
            }
        }

        fun moveWithCrateMover9001(inst: Instruction) {
            val crates = stacks[inst.from - 1].takeLast(inst.num)
            stacks[inst.to - 1].addAll(crates)
            repeat(inst.num) { stacks[inst.from - 1].removeLast() }
        }
    }

    class Instruction(val num: Int, val from: Int, val to: Int)

    override fun solvePart1(): String {
        val stacks = parseStacks(input)
        instructions.forEach { stacks.moveWithCrateMover9000(it) }
        return stacks.stacks.map { it.pop() }.joinToString("")
    }

    override fun solvePart2(): String {
        val stacks = parseStacks(input)
        instructions.forEach { stacks.moveWithCrateMover9001(it) }
        return stacks.stacks.map { it.pop() }.joinToString("")
    }
}

private fun List<List<Char>>.createStacks(): List<Stack<Char>> {
    return List<Stack<Char>>(this[0].size) { Stack() }.also { stacks ->
        for (i in 0..this[0].lastIndex) {
            for (j in lastIndex downTo 0) {
                if (this[j][i] != '-') stacks[i].push(this[j][i])
            }
        }
    }
}

private fun String.toRow(): List<Char> {
    return this
        .replace("    ", "[-]")
        .replace("] [", "")
        .replace("]", "")
        .replace("[", "")
        .map { it }
}


fun main() {
    val name = Day05::class.simpleName
    val testInput = readInputAsString("src/2022/input/${name}_test.txt")
    val realInput = readInputAsString("src/2022/input/${name}.txt")
    runDay(Day05(testInput), Day05(realInput))
}