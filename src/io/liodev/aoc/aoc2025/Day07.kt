package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.set
import io.liodev.aoc.utils.findFirstOrNull

// --- 2025 Day 7: Laboratories ---
class Day07(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(21L, 1658, 40, 53916299384254)

    private val tachyonManifold = input.split("\n").map { it.toList() }

    override fun solvePart1(): Long {
        val diagram = tachyonManifold.map { it.toMutableList() }
        val start = tachyonManifold.findFirstOrNull('S')
        return emitTachyonRay(start!!.goDown(), diagram)
    }

    fun emitTachyonRay(newCoord: Coord, diagram: List<MutableList<Char>>): Long {
        return if (!newCoord.validIndex(diagram)) 0
        else when (diagram[newCoord]) {
            '|' -> 0 // already handled
            '.' -> {
                diagram[newCoord] = '|'
                emitTachyonRay(newCoord.goDown(), diagram)
            }
            '^' -> {
                1 + emitTachyonRay(newCoord.goLeft(), diagram) + emitTachyonRay(
                    newCoord.goRight(),
                    diagram
                )
            }
            else -> error("Invalid character ${diagram[newCoord]} at $newCoord")
        }
    }
    
    override fun solvePart2(): Long {
        val diagram = tachyonManifold.map { it.toMutableList() }
        val start = tachyonManifold.findFirstOrNull('S')
        return countTimelines(start!!.goDown(), diagram)
    }

    val cache: MutableMap<Coord, Long> = mutableMapOf()
    
    fun countTimelines(newCoord: Coord, diagram: List<MutableList<Char>>): Long {
        return if (!newCoord.validIndex(diagram)) 1
        else when (diagram[newCoord]) {
            '|' -> cache[newCoord]!!
            '.' -> {
                diagram[newCoord] = '|'
                val count = countTimelines(newCoord.goDown(), diagram)
                cache[newCoord] = count
                count
            }
            '^' -> {
                countTimelines(newCoord.goLeft(), diagram) + countTimelines(
                    newCoord.goRight(),
                    diagram
                )
            }
            else -> error("Invalid character ${diagram[newCoord]} at $newCoord")
        }
    }
}

fun main() {
    val name = Day07::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day07(testInput), Day07(realInput), year, printTimings = true)
}
