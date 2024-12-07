package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 7: Bridge Repair ---
class Day07(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(3749L, 882304362421, 11387, 145149066755184)

    private val calibrationEquations =
        input.lines().map { line ->
            val (result, operands) = line.split(": ")
            result.toLong() to operands.split(" ").map { it.toLong() }
        }

    override fun solvePart1(): Long =
        calibrationEquations
            .filter { equation ->
                val (result, operands) = equation
                solveRecursive(result, operands, listOf('+', '*'))
            }.sumOf { it.first }

    override fun solvePart2(): Long =
        calibrationEquations
            .filter { equation ->
                val (result, operands) = equation
                solveRecursive(result, operands, listOf('+', '*', '|'))
            }.sumOf { it.first }

    private fun solveRecursive(
        result: Long,
        operands: List<Long>,
        operators: List<Char>,
        acum: Long = 0L,
        operator: Char = '+',
    ): Boolean =
        if (acum > result) {
            false
        } else {
            when (operands.size) {
                0 -> acum == result
                1 -> result == calculate(acum, operands[0], operator)
                else -> {
                    val newAcum = calculate(acum, operands[0], operator)
                    operators.any { op ->
                        solveRecursive(result, operands.drop(1), operators, newAcum, op)
                    }
                }
            }
        }

    private fun calculate(
        a: Long,
        b: Long,
        operator: Char,
    ): Long =
        when (operator) {
            '+' -> a + b
            '*' -> a * b
            else -> (a.toString() + b.toString()).toLong()
        }
}

fun main() {
    val name = Day07::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day07(testInput), Day07(realInput), year, printTimings = true)
}
