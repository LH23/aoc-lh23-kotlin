package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.set
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.times
import io.liodev.aoc.utils.validIndex

// --- 2023 Day 16: The Floor Will Be Lava ---
class Day16(input: String) : Day<Int> {
    override val expectedValues = listOf(46, 6740, 51, 7041)

    private val layout = input.split("\n").map { it.toList() }
    private val visited: List<MutableList<Visited>> = List(layout.size) {
        MutableList(layout[0].size) { Visited() }
    }

    override fun solvePart1(): Int = calculateVisited(Coord(0, 0) to Dir.East)

    override fun solvePart2(): Int = layout.indices.maxOf {
        listOf(
            calculateVisited(Coord(it, 0) to Dir.East),
            calculateVisited(Coord(it, layout.lastIndex) to Dir.West),
            calculateVisited(Coord(0, it) to Dir.South),
            calculateVisited(Coord(layout.lastIndex, it) to Dir.North)
        ).max()
    }

    private fun calculateVisited(origin: Pair<Coord, Dir>): Int {
        (visited.indices * visited[0].indices).forEach { (i, j) ->
            visited[i][j] = Visited()
        }
        val toVisit = ArrayDeque<Pair<Coord, Dir>>()
        toVisit.addLast(origin)
        while (toVisit.isNotEmpty()) {
            val nextOnes = visit(toVisit.removeFirst())
            toVisit.addAll(nextOnes)
        }
        return visited.sumOf { row -> row.count { it.visited } }
    }

    private fun visit(cell: Pair<Coord, Dir>): List<Pair<Coord, Dir>> {
        val (coord, dir) = cell
        if (!visited.validIndex(coord) || visited[coord].alreadyVisited(dir)) {
            return listOf()
        }
        visited[coord] = visited[coord].add(dir)
        return when (layout[coord]) {
            '.' -> listOf(coord.move(dir) to dir)
            '-' ->
                if (dir in listOf(Dir.North, Dir.South)) listOf(
                    coord.move(Dir.East) to Dir.East,
                    coord.move(Dir.West) to Dir.West
                )
                else listOf(coord.move(dir) to dir)

            '|' ->
                if (dir in listOf(Dir.West, Dir.East)) listOf(
                    coord.move(Dir.North) to Dir.North,
                    coord.move(Dir.South) to Dir.South
                )
                else listOf(coord.move(dir) to dir)

            '/' -> when (dir) {
                Dir.North -> listOf(coord.move(Dir.East) to Dir.East)
                Dir.West -> listOf(coord.move(Dir.South) to Dir.South)
                Dir.South -> listOf(coord.move(Dir.West) to Dir.West)
                Dir.East -> listOf(coord.move(Dir.North) to Dir.North)
            }

            '\\' -> when (dir) {
                Dir.North -> listOf(coord.move(Dir.West) to Dir.West)
                Dir.West -> listOf(coord.move(Dir.North) to Dir.North)
                Dir.South -> listOf(coord.move(Dir.East) to Dir.East)
                Dir.East -> listOf(coord.move(Dir.South) to Dir.South)
            }

            else -> error("Invalid element")
        }
    }

    data class Visited(val n: Boolean, val w: Boolean, val s: Boolean, val e: Boolean) {
        constructor() : this(false, false, false, false)

        fun add(dir: Dir) = when (dir) {
            Dir.North -> this.copy(n = true)
            Dir.West -> this.copy(w = true)
            Dir.South -> this.copy(s = true)
            Dir.East -> this.copy(e = true)
        }

        val visited = n || w || s || e

        fun alreadyVisited(dir: Dir): Boolean = when (dir) {
            Dir.North -> n
            Dir.West -> w
            Dir.South -> s
            Dir.East -> e
        }
    }
}

fun main() {
    val name = Day16::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day16(testInput), Day16(realInput), printTimings = true)
}