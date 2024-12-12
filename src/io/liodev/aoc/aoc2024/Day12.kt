package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.times
import io.liodev.aoc.utils.validIndex

// --- 2024 12
class Day12(
    val input: String,
) : Day<Long> {
    override val expectedValues = listOf(1930L, 1477762, 1206, 923480, 692, 236)

    private val gardenPlots = input.lines().map { line -> line.map { it } }
    val regions = calculateRegions(gardenPlots)

    override fun solvePart1(): Long =
        regions.sumOf { region ->
            region.area.toLong() * region.perimeter
        }

    override fun solvePart2(): Long =
        regions.sumOf { region ->
//            println(
//                "Region ${gardenPlots[region.coords.first()]}: ${region.area} * ${region.sides} = ${region.area * region.sides}",
//            )
            region.area.toLong() * region.sides
        }

    private fun calculateRegions(gardenPlots: List<List<Char>>): List<Region> =
        buildList {
            val added = mutableSetOf<Coord>()
            (gardenPlots.indices * gardenPlots[0].indices).forEach { (r, c) ->
                val current = Coord(r, c)
                if (current !in added) {
                    val region = gardenPlots.getRegion(current)
                    this.add(region)
                    added.addAll(region.coords)
                }
            }
        }

    class Region(
        val gardenPlots: List<List<Char>>,
        val coords: Set<Coord>,
    ) {
        val area = coords.size
        private val perimeterCoords =
            coords
                .flatMap { coord ->
                    coord.getCardinalBorder().filter { it !in coords }
                }
        val perimeter = perimeterCoords.size
        val sides = calculateSides(perimeterCoords)

        private fun calculateSides(perimeterCoords: List<Coord>): Int {
            val sidesList = mutableListOf<Set<Coord>>()
            for (perimeterCoord in perimeterCoords.toSet()) {
                val verticalW = verticalSide(perimeterCoord, Dir.West).toSet()
                val verticalE = verticalSide(perimeterCoord, Dir.East).toSet()
                val horizontalN = horizontalSide(perimeterCoord, Dir.North).toSet()
                val horizontalS = horizontalSide(perimeterCoord, Dir.South).toSet()
                if (verticalW.size > 1 && verticalW !in sidesList) sidesList.add(verticalW)
                if (verticalE.size > 1 && verticalE !in sidesList) sidesList.add(verticalE)
                if (horizontalN.size > 1 && horizontalN !in sidesList) sidesList.add(horizontalN)
                if (horizontalS.size > 1 && horizontalS !in sidesList) sidesList.add(horizontalS)
                if (verticalW.size > 1 && verticalW == verticalE && sidesList.count { it == verticalW} == 1) sidesList.add(verticalW)
                if (horizontalN.size > 1 && horizontalN == horizontalS && sidesList.count { it == horizontalN} == 1) sidesList.add(horizontalN)
            }
            //println("LongSides for ${gardenPlots[coords.first()]}: $sidesList, perimeter $perimeter")
            return perimeter - sidesList.sumOf { it.size - 1 }
        }

        private fun verticalSide(coord: Coord, dir: Dir) =
            buildList {
                val queue = ArrayDeque<Coord>()
                queue.add(coord)
                if (coord.move(dir) !in coords) return@buildList

                while (queue.isNotEmpty()) {
                    val current = queue.removeFirst()
                    this.add(current)
                    queue.addAll(
                        current.getVerticalBorder().filter {
                            it !in this && it in perimeterCoords && it.move(dir) in coords
                        },
                    )
                }
            }

        private fun horizontalSide(coord: Coord, dir: Dir) =
            buildList {
                val queue = ArrayDeque<Coord>()
                queue.add(coord)
                if (coord.move(dir) !in coords) return@buildList

                while (queue.isNotEmpty()) {
                    val current = queue.removeFirst()
                    this.add(current)
                    queue.addAll(
                        current.getHorizontalBorder().filter {
                            it !in this && it in perimeterCoords && it.move(dir) in coords
                        },
                    )
                }
            }
    }

    private fun List<List<Char>>.getRegion(initial: Coord): Region {
        val queue = ArrayDeque<Coord>()
        val visited = mutableSetOf<Coord>()
        queue.add(initial)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current !in visited) {
                visited.add(current)
                queue.addAll(
                    current
                        .getCardinalBorder()
                        .filter { this.validIndex(it) && this[it] == this[initial] },
                )
            }
        }
        return Region(gardenPlots, visited)
    }
}

fun main() {
    val name = Day12::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val testInput2 = readInputAsString("src/input/$year/${name}_test2.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day12(testInput), Day12(realInput), year, extraDays = listOf(Day12(testInput2)))
}
