package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.set
import io.liodev.aoc.utils.times
import io.liodev.aoc.utils.validIndex

// --- 2023 Day 10: Pipe Maze ---
class Day10(input: String) : Day<Int> {
    override val expectedValues = listOf(70, 7173, 8, 291)

    private val pipes = input.split("\n").map { it.toList() }
    private val pipeLoop = createPipeLoop()

    override fun solvePart1(): Int = (pipeLoop.size + 1) / 2

    private fun createPipeLoop(): List<Coord> = buildList {
        add(Coord((pipes.indices * pipes[0].indices).first() { (i,j) -> pipes[i][j] == 'S' }))
        while (true) {
            val nextPipes = calculateNext(this)
            if (nextPipes.isEmpty()) break
            val (dir, next) = nextPipes
            if (dir !in this) add(dir)
            if (next !in this) add(next)
        }
    }

    data class PipePath(val valid: String, val dir: Coord, val pipe: Char, val next: Coord)

    private fun calculateNext(currPipe: List<Coord>): List<Coord> {
        val curr = currPipe.last()
        NEIGHBORS.forEach { pipePath ->
            if (pipes.validIndex(curr + pipePath.dir) &&
                pipes[curr + pipePath.dir] == pipePath.pipe &&
                pipes[curr] in pipePath.valid &&
                curr + pipePath.dir !in currPipe
            ) {
                return listOf(curr + pipePath.dir, curr + pipePath.next)
            }
        }
        return listOf()
    }

    override fun solvePart2(): Int {
        val pipeMatrix = MutableList(pipes.size) { MutableList(pipes[0].size) { '.' } }
        pipeLoop.forEach { pipeMatrix[it] = pipes[it] }
        pipeMatrix[pipeLoop[0]] = replaceS(pipeLoop[0], pipeLoop[1], pipeLoop.last())

        return pipeMatrix.sumOf { row -> numInsides(row) }
    }

    private fun numInsides(row: MutableList<Char>): Int {
        var isInside = false
        var inside = 0
        val filtered = row.filter { it != '-' }
        for (i in 0..<filtered.lastIndex) {
            if (filtered[i] == '.' && isInside) inside++
            if (filtered[i] == '|' ||
                filtered[i].toString() + filtered[i + 1] == "L7" ||
                filtered[i].toString() + filtered[i + 1] == "FJ"
            ) {
                isInside = !isInside
            }
        }
        return inside
    }

    private fun replaceS(s: Coord, next: Coord, last: Coord): Char {
        return when {
            next.r == last.r -> '-'
            next.c == last.c -> '|'
            s + Coord(0, 1) == next && s + Coord(-1, 0) == last -> 'L'
            s + Coord(0, 1) == last && s + Coord(-1, 0) == next -> 'L'
            s + Coord(0, -1) == next && s + Coord(1, 0) == last -> '7'
            s + Coord(0, -1) == last && s + Coord(1, 0) == next -> '7'
            s + Coord(0, 1) == next && s + Coord(1, 0) == last -> 'F'
            s + Coord(0, 1) == last && s + Coord(1, 0) == next -> 'F'
            s + Coord(0, -1) == next && s + Coord(-1, 0) == last -> 'J'
            s + Coord(0, -1) == last && s + Coord(-1, 0) == next -> 'J'
            else -> error("invalid S")
        }
    }

    companion object {
        val NEIGHBORS = listOf(
            PipePath("S-7J", Coord(0, -1), '-', Coord(0, -1)),
            PipePath("S-7J", Coord(0, -1), 'L', Coord(-1, -1)),
            PipePath("S-7J", Coord(0, -1), 'F', Coord(1, -1)),
            PipePath("S-LF", Coord(0, 1), '-', Coord(0, 1)),
            PipePath("S-LF", Coord(0, 1), 'J', Coord(-1, 1)),
            PipePath("S-LF", Coord(0, 1), '7', Coord(1, 1)),
            PipePath("S|LJ", Coord(-1, 0), '|', Coord(-1, 0)),
            PipePath("S|LJ", Coord(-1, 0), '7', Coord(-1, -1)),
            PipePath("S|LJ", Coord(-1, 0), 'F', Coord(-1, 1)),
            PipePath("S|7F", Coord(1, 0), '|', Coord(1, 0)),
            PipePath("S|7F", Coord(1, 0), 'J', Coord(1, -1)),
            PipePath("S|7F", Coord(1, 0), 'L', Coord(1, 1)),
        )
    }
}


fun main() {
    val name = Day10::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test2.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day10(testInput), Day10(realInput), printTimings = true)
}