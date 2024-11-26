package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.set
import io.liodev.aoc.utils.validIndex
import kotlin.math.max
import kotlin.math.min

// --- Day 14 2022: Regolith Reservoir ---
class Day14(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(24, 715, 93, 25248)

    private val scanTraces =
        input.split("\n").map { trace ->
            trace.split(" -> ").map { it.split(",").let { (x, y) -> Coord(y.toInt(), x.toInt()) } }
        }

    override fun solvePart1(): Int = calculateDroppedSand(scanTraces)

    override fun solvePart2(): Int {
        val rangeR = getRangeRows(scanTraces)
        val rangeC = getRangeColumns(scanTraces)
        val floor =
            listOf(
                listOf(
                    Coord(rangeR.last + 2, rangeC.first - rangeR.last),
                    Coord(rangeR.last + 2, rangeC.last + rangeR.last),
                ),
            )
        return calculateDroppedSand(scanTraces + floor) + 1
    }

    private fun getRangeRows(traces: List<List<Coord>>): IntRange =
        traces.flatMap { trace -> trace.map { it.r } }.let { it.min()..it.max() }

    private fun getRangeColumns(traces: List<List<Coord>>): IntRange =
        traces.flatMap { trace -> trace.map { it.c } }.let { it.min()..it.max() }

    private fun calculateDroppedSand(traces: List<List<Coord>>): Int {
        val rangeR = getRangeRows(traces)
        val rangeC = getRangeColumns(traces)
        val sandMap = traces.buildSandMap(rangeR, rangeC)

        var sand = 0
        while (true) {
            val sandPosition = dropSand(rangeC, sandMap)
            if (sandPosition.r == -1) break
            sandMap[sandPosition] = 'o'
            sand++
        }
        return sand
    }

    private fun dropSand(
        rangeC: IntRange,
        sandMap: List<List<Char>>,
    ): Coord {
        val releaseCoord = Coord(0, 500 - rangeC.first)
        var sandPos = releaseCoord
        while (true) {
            return when {
                !sandPos.validIndex(sandMap) -> Coord(-1, -1)
                sandPos.fallOne(sandMap) != null -> {
                    sandPos = sandPos.fallOne(sandMap)!!
                    continue
                }

                sandPos == releaseCoord -> Coord(-1, -1)
                else -> break
            }
        }
        return sandPos
    }

    private fun Coord.fallOne(sandMap: List<List<Char>>): Coord? {
        val down = this.goDown()
        val downLeft = this.goDown().goLeft()
        val downRight = this.goDown().goRight()
        return when {
            (!sandMap.validIndex(down) || sandMap[down] == '.') -> down
            (!sandMap.validIndex(downLeft) || sandMap[downLeft] == '.') -> downLeft
            (!sandMap.validIndex(downRight) || sandMap[downRight] == '.') -> downRight
            else -> null
        }
    }

    private fun List<List<Coord>>.buildSandMap(
        rangeR: IntRange,
        rangeC: IntRange,
    ): List<MutableList<Char>> =
        buildList {
            repeat(rangeR.last + 1) {
                add(MutableList(rangeC.last - rangeC.first + 1) { '.' })
            }

            this@buildSandMap.forEach { coord ->
                coord.zipWithNext { a, b ->
                    for (r in min(a.r, b.r)..max(a.r, b.r)) {
                        for (c in min(a.c, b.c)..max(a.c, b.c)) {
                            this[r][c - rangeC.first] = '#'
                        }
                    }
                }
            }
        }
}

fun main() {
    val name = Day14::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day14(testInput), Day14(realInput), year, printTimings = true)
}
