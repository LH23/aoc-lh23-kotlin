package io.liodev.aoc

import io.liodev.aoc.aoc2023.Day01
import io.liodev.aoc.aoc2023.Day02
import io.liodev.aoc.aoc2023.Day03
import io.liodev.aoc.aoc2023.Day04
import io.liodev.aoc.aoc2023.Day05
import io.liodev.aoc.aoc2023.Day06
import io.liodev.aoc.aoc2023.Day07
import io.liodev.aoc.aoc2023.Day08
import io.liodev.aoc.aoc2023.Day09
import io.liodev.aoc.aoc2023.Day10
import io.liodev.aoc.aoc2023.Day11
import io.liodev.aoc.aoc2023.Day12
import io.liodev.aoc.aoc2023.Day13
import io.liodev.aoc.aoc2023.Day14
import io.liodev.aoc.aoc2023.Day15
import io.liodev.aoc.aoc2023.Day16
import io.liodev.aoc.aoc2023.Day17
import io.liodev.aoc.aoc2023.Day18
import io.liodev.aoc.aoc2023.Day19
import io.liodev.aoc.aoc2023.Day20
import io.liodev.aoc.aoc2023.Day21
import io.liodev.aoc.aoc2023.Day22
import io.liodev.aoc.aoc2023.Day23
import io.liodev.aoc.aoc2023.Day24
import io.liodev.aoc.aoc2023.Day25
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass

class AllDaysTest {
    private fun runTestForDay(
        kDay: KClass<out Day<*>>,
        year: Int = 2023,
    ) {
        val name = kDay.simpleName
        val testInput = readInputAsString("src/input/$year/${name}_test.txt")
        val realInput = readInputAsString("src/input/$year/$name.txt")
        val testDay = kDay.constructors.first().call(testInput)
        val realDay = kDay.constructors.first().call(realInput)
        runDay(testDay, realDay, year = year)
    }

    // 2023
    @Test
    fun aoc2023day01() = runTestForDay(Day01::class)

    @Test
    fun aoc2023day02() = runTestForDay(Day02::class)

    @Test
    fun aoc2023day03() = runTestForDay(Day03::class)

    @Test
    fun aoc2023day04() = runTestForDay(Day04::class)

    @Test
    fun aoc2023day05() = runTestForDay(Day05::class)

    @Test
    fun aoc2023day06() = runTestForDay(Day06::class)

    @Test
    fun aoc2023day07() = runTestForDay(Day07::class)

    @Test
    fun aoc2023day08() = runTestForDay(Day08::class)

    @Test
    fun aoc2023day09() = runTestForDay(Day09::class)

    @Test
    fun aoc2023day10() = runTestForDay(Day10::class)

    @Test
    fun aoc2023day11() = runTestForDay(Day11::class)

    @Test
    fun aoc2023day12() = runTestForDay(Day12::class)

    @Test
    fun aoc2023day13() = runTestForDay(Day13::class)

    @Test
    fun aoc2023day14() = runTestForDay(Day14::class)

    @Test
    fun aoc2023day15() = runTestForDay(Day15::class)

    @Test
    fun aoc2023day16() = runTestForDay(Day16::class)

    @Test
    fun aoc2023day17() = runTestForDay(Day17::class)

    @Test
    fun aoc2023day18() = runTestForDay(Day18::class)

    @Test
    fun aoc2023day19() = runTestForDay(Day19::class)

    @Test
    fun aoc2023day20() = runTestForDay(Day20::class)

    @Test
    fun aoc2023day21() = runTestForDay(Day21::class)

    @Test
    fun aoc2023day22() = runTestForDay(Day22::class)

    @Test
    fun aoc2023day23() = runTestForDay(Day23::class)

    @Test
    fun aoc2023day24() = runTestForDay(Day24::class)

    @Test
    fun aoc2023day25() = runTestForDay(Day25::class)

    // 2022
    @Test
    fun aoc2022day01() = runTestForDay(io.liodev.aoc.aoc2022.Day01::class, 2022)

    @Test
    fun aoc2022day02() = runTestForDay(io.liodev.aoc.aoc2022.Day02::class, 2022)

    @Test
    fun aoc2022day03() = runTestForDay(io.liodev.aoc.aoc2022.Day03::class, 2022)

    @Test
    fun aoc2022day04() = runTestForDay(io.liodev.aoc.aoc2022.Day04::class, 2022)

    @Test
    fun aoc2022day05() = runTestForDay(io.liodev.aoc.aoc2022.Day05::class, 2022)

    @Test
    fun aoc2022day06() = runTestForDay(io.liodev.aoc.aoc2022.Day06::class, 2022)

    @Test
    fun aoc2022day07() = runTestForDay(io.liodev.aoc.aoc2022.Day07::class, 2022)

    @Test
    fun aoc2022day08() = runTestForDay(io.liodev.aoc.aoc2022.Day08::class, 2022)

    @Test
    fun aoc2022day09() = runTestForDay(io.liodev.aoc.aoc2022.Day09::class, 2022)

    @Test
    fun aoc2022day10() = runTestForDay(io.liodev.aoc.aoc2022.Day10::class, 2022)

    @Test
    fun aoc2022day11() = runTestForDay(io.liodev.aoc.aoc2022.Day11::class, 2022)

    @Test
    fun aoc2022day12() = runTestForDay(io.liodev.aoc.aoc2022.Day12::class, 2022)
}
