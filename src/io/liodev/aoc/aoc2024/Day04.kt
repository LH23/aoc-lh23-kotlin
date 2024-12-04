package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.validIndex
import io.liodev.aoc.utils.get

// --- 2024 Day 4: Ceres Search ---
class Day04(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(18, 2571, 9, 1992)

    private val wordSearch = input.split("\n").map { it.toCharArray().toList() }

    override fun solvePart1(): Int {
        var count = 0
        for (i in wordSearch.indices) {
            for (j in wordSearch[0].indices) {
                if (wordSearch.isXmas(Coord(i,j), Coord(i + 1,j + 0), Coord(i + 2,j + 0), Coord(i + 3,j + 0))) count++
                if (wordSearch.isXmas(Coord(i,j), Coord(i - 1,j + 0), Coord(i - 2,j + 0), Coord(i - 3,j + 0))) count++
                if (wordSearch.isXmas(Coord(i,j), Coord(i + 0,j + 1), Coord(i + 0,j + 2), Coord(i + 0,j + 3))) count++
                if (wordSearch.isXmas(Coord(i,j), Coord(i + 0,j - 1), Coord(i + 0,j - 2), Coord(i + 0,j - 3))) count++

                if (wordSearch.isXmas(Coord(i,j), Coord(i + 1,j + 1), Coord(i + 2,j + 2), Coord(i + 3,j + 3))) count++
                if (wordSearch.isXmas(Coord(i,j), Coord(i + 1,j - 1), Coord(i + 2,j - 2), Coord(i + 3,j - 3))) count++
                if (wordSearch.isXmas(Coord(i,j), Coord(i - 1,j + 1), Coord(i - 2,j + 2), Coord(i - 3,j + 3))) count++
                if (wordSearch.isXmas(Coord(i,j), Coord(i - 1,j - 1), Coord(i - 2,j - 2), Coord(i - 3,j - 3))) count++
            }
        }
        return count
    }

    override fun solvePart2(): Int {
        var count = 0
        for (i in wordSearch.indices) {
            for (j in wordSearch[0].indices) {
                if (wordSearch.isXCrossedMas(Coord(i - 1 , j - 1), Coord(i,j), Coord(i + 1, j + 1), Coord(i - 1, j + 1), Coord(i + 1, j - 1))) count++
                if (wordSearch.isXCrossedMas(Coord(i + 1 , j + 1), Coord(i,j), Coord(i - 1, j - 1), Coord(i + 1, j - 1), Coord(i - 1, j + 1))) count++

                if (wordSearch.isXCrossedMas(Coord(i - 1 , j - 1), Coord(i,j), Coord(i + 1, j + 1), Coord(i + 1, j - 1), Coord(i - 1, j + 1))) count++
                if (wordSearch.isXCrossedMas(Coord(i + 1 , j + 1), Coord(i,j), Coord(i - 1, j - 1), Coord(i - 1, j + 1), Coord(i + 1, j - 1))) count++
            }
        }
        return count
    }

    private fun List<List<Char>>.isXmas(
        x: Coord,
        m: Coord,
        a: Coord,
        s: Coord
    ): Boolean = x.validIndex(this) && m.validIndex(this) && a.validIndex(this) && s.validIndex(this) &&
            this[x] == 'X' && this[m] == 'M' && this[a] == 'A' && this[s] == 'S'

    private fun List<List<Char>>.isXCrossedMas(
        m: Coord,
        a: Coord,
        s: Coord,
        m2: Coord,
        s2: Coord
    ): Boolean = (m.validIndex(this) && a.validIndex(this) && s.validIndex(this) && m2.validIndex(this) && s2.validIndex(this) &&
            this[m] == 'M' && this[a] == 'A' && this[s] == 'S' && this[m2] == 'M' && this[s2] == 'S')

}

fun main() {
    val name = Day04::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day04(testInput), Day04(realInput), year)
}
