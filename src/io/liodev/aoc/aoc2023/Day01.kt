package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

class Day01(input: String) : Day<Int> {
    override val expectedValues = listOf(213, 53651, 281, 53894)

    private val calibrationInputs = input.split("\n")
    override fun solvePart1() = calibrationInputs
        .map { input -> input.filter { it.isDigit() } }
        .sumOf { "${it.first()}${it.last()}".toInt() }

    override fun solvePart2() = calibrationInputs
        .sumOf { input -> "${input.firstNum()}${input.lastNum()}".toInt() }
}

private val nums = listOf(
    "one" to '1', "1" to '1',
    "two" to '2', "2" to '2',
    "three" to '3', "3" to '3',
    "four" to '4', "4" to '4',
    "five" to '5', "5" to '5',
    "six" to '6', "6" to '6',
    "seven" to '7', "7" to '7',
    "eight" to '8', "8" to '8',
    "nine" to '9', "9" to '9'
).toMap()

private fun String.firstNum() = nums[this.findAnyOf(nums.keys)!!.second]

private fun String.lastNum() = nums[this.findLastAnyOf(nums.keys)!!.second]

fun main() {
    val name = Day01::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day01(testInput), Day01(realInput))
}