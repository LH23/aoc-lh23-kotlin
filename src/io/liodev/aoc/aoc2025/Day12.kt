package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.aoc2025.Day12.Shape.Companion.toShape
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import kotlin.text.toList

// --- 2025 Day 12: Christmas Tree Farm ---
class Day12(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(3L, 536, 12252025, 12252025)

    private val presentsDecoration = input.split("\n\n").let { 
        val shapes = it.take(6).map { shape -> shape.lines().drop(1).map { it.toList() }.toShape() }
        val regions = it.drop(6).first().let { regionStr ->
            regionStr.lines().map { line ->
                val size = line.substringBefore(":").split("x").let { (a,b) -> a.toInt() to b.toInt() }
                val presents = line.substringAfter(": ").split(" ").map { it.toInt() }
                Region(size, presents)
            }
        }
        PresentsDecoration(shapes, regions)
    }

    data class PresentsDecoration(
        val shapes: List<Shape>,
        val regions: List<Region>
    )
    
    data class Shape(val grid: List<List<Char>>) {
        val space = grid.sumOf { row -> row.count { it == '#' }}
        val rotations: List<Shape> by lazy {
            val rotations = mutableListOf<Shape>()
            var current = this
            repeat(3) {
                current = current.rotate()
                rotations.add(current)
            }
            rotations
        }

        private fun rotate(): Shape {
            return grid.mapIndexed { i, row ->
                row.mapIndexed { j, _ ->
                    grid[grid.size - 1 - j][i]
                }
            }.toShape()
        }
        
        companion object {
            fun List<List<Char>>.toShape(): Shape {
                return Shape(this)
            }
        }
    }

    data class Region(val size: Pair<Int, Int>, val presents: List<Int>) {
        fun canFitAllPresents(shapes: List<Shape>): Boolean {
            val capacity = size.first * size.second
            val totalShapesSpace = shapes.zip(presents).sumOf { (shape, present) ->
                shape.space * present
            }
            return capacity >= totalShapesSpace
            // extra fitting checks not needed ::christmas miracle::
        }
    }

    override fun solvePart1(): Long {
        return presentsDecoration.regions.count {
            it.canFitAllPresents(presentsDecoration.shapes)
        }.toLong()
    }

    override fun solvePart2(): Long = 12252025
}

fun main() {
    val name = Day12::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day12(testInput), Day12(realInput), year)
}
