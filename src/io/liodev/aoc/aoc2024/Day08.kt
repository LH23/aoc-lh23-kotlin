package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.times

// --- 2024 Day 8: Resonant Collinearity ---
class Day08(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(14, 252, 34, 839)

    private val antennasMap = input.split("\n").map { it.toCharArray().toList() }
    private val antennas =
        buildMap {
            (antennasMap.indices * antennasMap[0].indices).forEach { (r, c) ->
                if (antennasMap[r][c] != '.') {
                    val antennaCoords: MutableList<Coord> =
                        getOrPut(antennasMap[r][c]) { mutableListOf() }
                    antennaCoords.add(Coord(r, c))
                }
            }
        }

    override fun solvePart1(): Int =
        generateAntinodes(pairwiseAntinodes = { antenna1, antenna2 ->
            calculateAntinodes(antenna1, antenna2)
        }).size

    override fun solvePart2(): Int =
        generateAntinodes(pairwiseAntinodes = { antenna1, antenna2 ->
            calculateResonantAntinodes(antenna1, antenna2)
        }).size

    private fun generateAntinodes(pairwiseAntinodes: (Coord, Coord) -> List<Coord>): Set<Coord> {
        val antinodes = mutableSetOf<Coord>()
        for (frequency in antennas.keys) {
            if (antennas[frequency]!!.size > 1) {
                val coords = antennas[frequency]!!
                for (i in 0..<coords.lastIndex) {
                    for (j in i + 1..coords.lastIndex) {
                        antinodes.addAll(pairwiseAntinodes(coords[i], coords[j]))
                    }
                }
            }
        }
        return antinodes
    }

    private fun calculateAntinodes(
        antennaA: Coord,
        antennaB: Coord,
    ): List<Coord> =
        listOf(
            antennaA + inverseDistance(antennaA, antennaB),
            antennaB + inverseDistance(antennaB, antennaA),
        ).filter { it.validIndex(antennasMap) }

    private fun calculateResonantAntinodes(
        antennaA: Coord,
        antennaB: Coord,
    ): List<Coord> = resonantAntinodes(antennaA, antennaB) + resonantAntinodes(antennaB, antennaA)

    private fun resonantAntinodes(
        a1: Coord,
        a2: Coord,
    ): List<Coord> {
        val list = mutableListOf<Coord>()
        var a = a1
        while (a.validIndex(antennasMap)) {
            list.add(a)
            a += inverseDistance(a1, a2)
        }
        return list
    }

    private fun inverseDistance(
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
