package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.printMatrix
import io.liodev.aoc.utils.validIndex
import kotlin.math.max
import kotlin.math.min

// --- Day 14 2022: Regolith Reservoir ---
class Day14(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(24, 715, 93, 25248)

    private val scanTraces =
        input
            .split("\n")
            .map {
                it
                    .split(" -> ")
                    .let { it.map { it.split(",").let { (x, y) -> Coord(y.toInt(), x.toInt()) } } }
            }.also { traces ->
                rangeR = traces.flatMap { coord -> coord.map { it.r } }.let { it.min()..it.max() }
                rangeC = traces.flatMap { coord -> coord.map { it.c } }.let { it.min()..it.max() }
            }
    private var rangeR: IntRange
    private var rangeC: IntRange

    override fun solvePart1(): Int {
        val sandMap = scanTraces.buildSandMap()
        var sand = 0
        while (true) {
            val sandPos = throwSand(sandMap)
            if (sandPos.r == -1) break
            sandMap[sandPos.r][sandPos.c] = 'o'
            sand++
        }
        return sand
    }

    override fun solvePart2(): Int {
        val sandMap = scanTraces.buildSandMap(true)
        var sand = 0
        while (true) {
            val sandPos = throwSand(sandMap, rangeR.max())
//            println("Return sandPos = $sandPos")
//            sandMap.printMatrix("")
            if (sandPos.r == -1) break
            sandMap[sandPos.r][sandPos.c] = 'o'
            sand++
        }
        return sand + 1
    }

    private fun throwSand(sandMap: List<List<Char>>, expandSize: Int = 0): Coord {
        val releaseCoord = Coord(0, 500 - rangeC.first + expandSize)
        var sandPos = releaseCoord
        while (true) {
            when {
                !sandPos.validIndex(sandMap) -> return Coord(-1, -1)
                sandPos.fallOne(sandMap) != null -> {
                    sandPos = sandPos.fallOne(sandMap)!!
                }
                sandPos == releaseCoord -> return Coord(-1, -1)
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
            (!sandMap.validIndex(down) || sandMap[down.r][down.c] == '.') -> down
            (!sandMap.validIndex(downLeft) || sandMap[downLeft.r][downLeft.c] == '.') -> downLeft
            (!sandMap.validIndex(downRight) || sandMap[downRight.r][downRight.c] == '.') -> downRight
            else -> null
        }//.also { println("fallOne = $it") }
    }

    private fun List<List<Coord>>.buildSandMap(expanded: Boolean = false): List<MutableList<Char>> =
        buildList {
            repeat(rangeR.last + 1) {
                add(MutableList(rangeC.last - rangeC.first + 1 + if (expanded) rangeR.last * 2 else 0) { '.' })
            }

            if (expanded) {
                add(MutableList(rangeC.last - rangeC.first + 1 + rangeR.last * 2) { '.' })
                add(MutableList(rangeC.last - rangeC.first + 1 + rangeR.last * 2) { '#' })
            }

            this@buildSandMap.forEach { coord ->
                coord.zipWithNext { a, b ->
                    for (r in min(a.r, b.r)..max(a.r, b.r)) {
                        for (c in min(a.c, b.c)..max(a.c, b.c)) {
                            this[r][c - rangeC.first + if (expanded) rangeR.last else 0] = '#'
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
    runDay(Day14(testInput), Day14(realInput), year)
}
