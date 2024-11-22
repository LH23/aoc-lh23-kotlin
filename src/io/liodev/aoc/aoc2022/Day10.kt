package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- Day 10 2022: Cathode-Ray Tube ---
class Day10(
    input: String,
) : Day<Int> {
    private val part2TestExpected =
        listOf(
            "##..##..##..##..##..##..##..##..##..##..",
            "###...###...###...###...###...###...###.",
            "####....####....####....####....####....",
            "#####.....#####.....#####.....#####.....",
            "######......######......######......####",
            "#######.......#######.......#######.....",
        )
    private val part2RealExpected =
        listOf(
            "####.###..#..#.###..#..#.####..##..#..#.",
            "#....#..#.#..#.#..#.#..#....#.#..#.#..#.",
            "###..###..#..#.#..#.####...#..#....####.",
            "#....#..#.#..#.###..#..#..#...#....#..#.",
            "#....#..#.#..#.#.#..#..#.#....#..#.#..#.",
            "#....###...##..#..#.#..#.####..##..#..#.",
        )

    override val expectedValues = listOf(13140, 13720, 0, 0)

    private val program =
        input.split("\n").flatMap {
            when (it) {
                "noop" -> listOf(0)
                else -> listOf(0, it.substringAfter(' ').toInt())
            }
        }

    override fun solvePart1(): Int {
        var regX = 1
        var signal = 0
        for (i in 1..<program.size) {
            if ((i + 20) % 40 == 0) {
                //println("i: $i, regX: $regX")
                signal += i * regX
            }
            regX += program[i - 1]
        }
        return signal
    }

    override fun solvePart2(): Int {
        val crt = List(6) { MutableList(40) { '.' } }
        var regX = 1
        for (i in 1..<program.size) {
            if (((i - 1) % 40) in regX - 1..regX + 1) {
                crt[(i - 1) / 40][(i - 1) % 40] = '#'
            }
            regX += program[i - 1]
        }
        printCrt(crt)
        val expected = if (program[1] == 15) part2TestExpected else part2RealExpected
        return crtDiff(crt.map { it.joinToString("") }, expected)
    }

    private fun crtDiff(
        crt: List<String>,
        expected: List<String>,
    ): Int = crt.zip(expected).count { it.first != it.second }

    private fun printCrt(crt: List<List<Char>>) {
        println(crt.joinToString("") { it.joinToString("") + '\n' })
    }
}

fun main() {
    val name = Day10::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day10(testInput), Day10(realInput), year)
}
