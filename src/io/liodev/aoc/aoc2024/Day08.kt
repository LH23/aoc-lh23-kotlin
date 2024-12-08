package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import kotlin.time.times

// --- 2024 Day 8: Resonant Collinearity ---
class Day08(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(14, 252, 34, 839)

    private val antennasMap = input.split("\n").map { it.toCharArray().toList() }
    private val antennas =
        buildList {
            mutableListOf<Pair<Char, Coord>>()
            for (r in antennasMap.indices) {
                for (c in antennasMap[0].indices) {
                    if (antennasMap[r][c] != '.') this.add(antennasMap[r][c] to Coord(r, c))
                }
            }
        }.groupBy { it.first }.mapValues { (_, v) -> v.map { it.second } }

    override fun solvePart1(): Int {
        val antinodes = mutableSetOf<Coord>()
        for (frequency in antennas.keys) {
            if (antennas[frequency]!!.size > 1) {
                val coords = antennas[frequency]!!
                for (i in 0..<coords.lastIndex) {
                    for (j in i + 1..coords.lastIndex) {
                        val antinode1 = coords[i] + invDistance(coords[i], coords[j])
                        val antinode2 = coords[j] + invDistance(coords[j], coords[i])
                        println("For pair ${coords[i]} and ${coords[j]}, antinodes are $antinode1 and $antinode2")
                        antinodes.addAll(
                            listOf(antinode1, antinode2).filter {
                                it.validIndex(antennasMap)
                            },
                        )
                    }
                }
            }
        }
        return antinodes.size
    }

    override fun solvePart2(): Int {
        val antinodes = mutableSetOf<Coord>()
        for (frequency in antennas.keys) {
            if (antennas[frequency]!!.size > 1) {
                val coords = antennas[frequency]!!
                for (i in 0..<coords.lastIndex) {
                    for (j in i + 1..coords.lastIndex) {
                        var antinode1 = coords[i]
                        while (antinode1.validIndex(antennasMap)) {
                            antinodes.add(antinode1)
                            antinode1 += invDistance(coords[i], coords[j])
                        }
                        var antinode2 = coords[j]
                        while (antinode2.validIndex(antennasMap)) {
                            antinodes.add(antinode2)
                            antinode2 += invDistance(coords[j], coords[i])
                        }
                    }
                }
            }
        }
        return antinodes.size
    }

    private fun invDistance(
        a: Coord,
        b: Coord,
    ): Coord = Coord((b.r - a.r) * -1, (b.c - a.c) * -1)
}

fun main() {
    val name = Day08::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day08(testInput), Day08(realInput), year)
}
