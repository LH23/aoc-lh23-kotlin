package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- Day 10 2022: Cathode-Ray Tube ---
class Day10(
    input: String,
) : Day<Int> {
    private val part2TestExpected =
        arrayOf(
            "##..##..##..##..##..##..##..##..##..##..",
            "###...###...###...###...###...###...###.",
            "####....####....####....####....####....",
            "#####.....#####.....#####.....#####.....",
            "######......######......######......####",
            "#######.......#######.......#######.....",
        )
    private val part2RealExpected =
        arrayOf(
            "####.###..#..#.###..#..#.####..##..#..#.",
            "#....#..#.#..#.#..#.#..#....#.#..#.#..#.",
            "###..###..#..#.#..#.####...#..#....####.",
            "#....#..#.#..#.###..#..#..#...#....#..#.",
            "#....#..#.#..#.#.#..#..#.#....#..#.#..#.",
            "#....###...##..#..#.#..#.####..##..#..#.",
        )

    override val expectedValues = listOf(13140, 13720, 0, 0)

    private val regXbyCycle =
        input
            .split("\n")
            .flatMap {
                when (it) {
                    "noop" -> listOf(0)
                    else -> listOf(0, it.substringAfter(' ').toInt())
                }
            }.fold(listOf(1)) { acc, x ->
                acc + (acc.last() + x)
            }

    override fun solvePart1() = listOf(20, 60, 100, 140, 180, 220).sumOf { it * regXbyCycle[it - 1] }

    override fun solvePart2(): Int {
        val crt = Array(6) { CharArray(40) { '.' } }
        for (i in 0..<regXbyCycle.size - 1) {
            if ((i % 40) in regXbyCycle[i] - 1..regXbyCycle[i] + 1) {
                crt[(i / 40)][(i % 40)] = '#'
            }
        }
        //printCrt(crt)
        val expected = if (regXbyCycle[3] == 16) part2TestExpected else part2RealExpected
        return crt.zip(expected).count { it.first.joinToString("") != it.second }
    }

    private fun printCrt(crt: Array<CharArray>) {
        println(crt.joinToString("") { it.joinToString("") + '\n' })
    }
}

fun main() {
    val name = Day10::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day10(testInput), Day10(realInput), year, printTimings = true)
}
