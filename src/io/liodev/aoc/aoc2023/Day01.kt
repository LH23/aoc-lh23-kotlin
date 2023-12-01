package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

class Day01(input: String): Day<Int> {
    // 12, 38, 15, and 77.
    override val expectedValues = listOf(213, 53651, 281, -1)

    private val parsedInput = parseInput(input)

    private fun parseInput(input: String): List<String> = input.split("\n")
    override fun solvePart1() = parsedInput.map { it.filter { it.isDigit() } }.map { "${it.first()}${it.last()}".toInt() }.sum()

    override fun solvePart2() = parsedInput.map { "${it.firstNum()}${it.lastNum()}".toInt() }.also { it.println() }.sum()
}

private fun String.firstNum(): Char {
    return if (this[0].isDigit()) this[0]
        else when {
            this.startsWith("one") -> '1'
            this.startsWith("two") -> '2'
            this.startsWith("three") -> '3'
            this.startsWith("four") -> '4'
            this.startsWith("five") -> '5'
            this.startsWith("six") -> '6'
            this.startsWith("seven") -> '7'
            this.startsWith("eight") -> '8'
            this.startsWith("nine") -> '9'
            else -> this.substring(1).firstNum()
        }
}

private fun String.lastNum(): Char {
    return if (this[lastIndex].isDigit()) this[lastIndex]
    else when {
        this.endsWith("one") -> '1'
        this.endsWith("two") -> '2'
        this.endsWith("three") -> '3'
        this.endsWith("four") -> '4'
        this.endsWith("five") -> '5'
        this.endsWith("six") -> '6'
        this.endsWith("seven") -> '7'
        this.endsWith("eight") -> '8'
        this.endsWith("nine") -> '9'
        else -> this.substring(0, this.lastIndex).lastNum()
    }
}

private fun String.replaceNums(): String = this
        .replace("one", "one1")
        .replace("two", "two2")
        .replace("three", "three3")
        .replace("four", "four4")
        .replace("five", "five5")
        .replace("six", "six6")
        .replace("seven", "seven7")
        .replace("eight", "eight8")
        .replace("nine", "nine9")

fun main() {
    val name = Day01::class.simpleName
    val testInput= readInputAsString("src/input/2023/${name}_test.txt")
    val realInput= readInputAsString("src/input/2023/${name}.txt")
    runDay(Day01(testInput), Day01(realInput))
}