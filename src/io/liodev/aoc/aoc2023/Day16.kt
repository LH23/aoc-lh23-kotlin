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
        MutableList(layout[0].size) { Visited(0, 0, 0, 0) }
    }

    override fun solvePart1(): Int {
        return calculateVisited(Coord(0,0) to Dir.East)
    }

    override fun solvePart2() : Int {
        var max = 0
        for (i in layout.indices){
            max = max.coerceAtLeast(calculateVisited(Coord(i, 0) to Dir.East))
            max = max.coerceAtLeast(calculateVisited(Coord(i, layout.lastIndex) to Dir.West))
            max = max.coerceAtLeast(calculateVisited(Coord(0, i) to Dir.South))
            max = max.coerceAtLeast(calculateVisited(Coord(layout.lastIndex, i) to Dir.North))
        }
        return max
    }

    private fun calculateVisited(origin: Pair<Coord, Dir>): Int {
        (visited.indices * visited[0].indices).forEach {(i,j) ->
            visited[i][j] = Visited(0, 0, 0, 0)
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
                if (dir in listOf(Dir.North, Dir.South)) listOf(coord.move(Dir.East) to Dir.East, coord.move(Dir.West) to Dir.West)
                else listOf(coord.move(dir) to dir)
            '|' ->
                if (dir in listOf(Dir.West, Dir.East)) listOf(coord.move(Dir.North) to Dir.North, coord.move(Dir.South) to Dir.South)
                else listOf(coord.move(dir) to dir)
            '/' -> when(dir) {
                Dir.North -> listOf(coord.move(Dir.East) to Dir.East)
                Dir.West -> listOf(coord.move(Dir.South) to Dir.South)
                Dir.South -> listOf(coord.move(Dir.West) to Dir.West)
                Dir.East -> listOf(coord.move(Dir.North) to Dir.North)
            }
            '\\' -> when(dir) {
                Dir.North -> listOf(coord.move(Dir.West) to Dir.West)
                Dir.West -> listOf(coord.move(Dir.North) to Dir.North)
                Dir.South -> listOf(coord.move(Dir.East) to Dir.East)
                Dir.East -> listOf(coord.move(Dir.South) to Dir.South)
            }
            else -> error("Invalid element")
        }
    }

    data class Visited(val n: Int, val w: Int, val s: Int, val e: Int) {
        fun add(dir: Dir) = when (dir) {
            Dir.North -> this.copy(n = n + 1)
            Dir.West -> this.copy(w = w + 1)
            Dir.South -> this.copy(s = s + 1)
            Dir.East -> this.copy(e = e + 1)
        }

        val visited = n > 0 || w > 0 || s > 0 || e > 0

        fun alreadyVisited(dir: Dir): Boolean = when (dir) {
            Dir.North -> n > 0
            Dir.West -> w > 0
            Dir.South -> s > 0
            Dir.East -> e > 0
        }
    }
}

fun main() {
    val name = Day16::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day16(testInput), Day16(realInput))
}