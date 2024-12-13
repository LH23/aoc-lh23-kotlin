package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.times
import io.liodev.aoc.utils.validIndex

// --- 2024 Day 12: Garden Groups ---
class Day12(
    val input: String,
) : Day<Long> {
    override val expectedValues = listOf(1930L, 1477762, 1206, 923480, 692, 236)

    private val gardenPlots = input.lines().map { line -> line.map { it } }
    private val regions =
        buildList {
            val added = mutableSetOf<Coord>()
            (gardenPlots.indices * gardenPlots[0].indices).forEach { (r, c) ->
                val current = Coord(r, c)
                if (current !in added) {
                    val region = gardenPlots.getRegion(current)
                    this.add(region)
                    added.addAll(region.regionCoords)
                }
            }
        }

    override fun solvePart1(): Long =
        regions.sumOf { region ->
            region.area.toLong() * region.perimeter
        }

    override fun solvePart2(): Long =
        regions.sumOf { region ->
            region.area.toLong() * region.sides
        }

    class Region(
        val regionCoords: Set<Coord>,
    ) {
        val area = regionCoords.size
        private val perimeterCoords =
            regionCoords
                .flatMap { coord ->
                    coord.getCardinalBorder().filter { it !in regionCoords }
                }
        val perimeter = perimeterCoords.size
        val sides = calculateSides(perimeterCoords)

        private fun calculateSides(perimeterCoords: List<Coord>): Int {
            val sidesSet = mutableSetOf<Pair<Set<Coord>, Dir>>()
            for (perimeterCoord in perimeterCoords.toSet()) {
                sidesSet.addAll(
                    listOf(
                        verticalSide(perimeterCoord, Dir.West) to Dir.West,
                        verticalSide(perimeterCoord, Dir.East) to Dir.East,
                        horizontalSide(perimeterCoord, Dir.North) to Dir.North,
                        horizontalSide(perimeterCoord, Dir.South) to Dir.South,
                    ).filter { it.first.size > 1 },
                )
            }
            return perimeter - sidesSet.sumOf { it.first.size - 1 }
        }

        private fun verticalSide(
            coord: Coord,
            dir: Dir,
        ) = getSide(coord, dir, Coord::getVerticalBorder)

        private fun horizontalSide(
            coord: Coord,
            dir: Dir,
        ) = getSide(coord, dir, Coord::getHorizontalBorder)

        private fun getSide(
            coord: Coord,
            dir: Dir,
            coordsToCheck: (Coord) -> List<Coord>,
        ) = buildList {
            val queue = ArrayDeque<Coord>()
            queue.add(coord)
            if (coord.move(dir) !in regionCoords) return@buildList

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                this.add(current)
                queue.addAll(
                    coordsToCheck(current).filter {
                        it !in this && it in perimeterCoords && it.move(dir) in regionCoords
                    },
                )
            }
        }.toSet()
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
        return Region(visited)
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
