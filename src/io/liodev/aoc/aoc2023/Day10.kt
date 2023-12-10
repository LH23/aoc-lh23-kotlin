package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.validIndex

// 2023 Day10
class Day10(input: String) : Day<Int> {
    override val expectedValues = listOf(70, 7173, 8, 291)

    private val pipes = input.split("\n").map { it.toList() }

    override fun solvePart1(): Int = (createPipeLoop().size + 1) / 2

    data class PipePath(val valid: String, val dir: Coord, val pipe: Char, val next: Coord)

    override fun solvePart2(): Int {
        var inside = 0
        val pipeLoop = createPipeLoop()
        val pipeMatrix = MutableList(pipes.size) { MutableList(pipes[0].size) { '.' } }
        pipeLoop.forEach { pipeMatrix[it] = pipes[it] }
        //pipeMatrix.print()
        pipeMatrix[pipeLoop[0]] = replaceS(pipeLoop[0], pipeLoop[1], pipeLoop.last())
        pipeMatrix.indices.forEach { i ->
            pipeMatrix[0].indices.forEach { j ->
                if (pipeMatrix[i][j] == '.' && pipeMatrix.inside(i, j, pipeLoop)) {
                    pipeMatrix[i][j] == 'I'
                    inside++
                }
            }
        }
        //pipeMatrix.print()
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

    private fun List<List<Char>>.inside(i: Int, j: Int, pipeLoop: List<Coord>): Boolean {
        return (j + 1..this.lastIndex)
            .filter { nj -> Coord(i, nj) in pipeLoop }
            .map { nj -> this[i][nj] }
            .filter { it != '-' }
            .joinToString("") { it.toString() }
            .replace("F7", "") // ignore border
            .replace("LJ", "") // ignore border
            .replace("L7", "|") // count L7 pipe as one
            .replace("FJ", "|") // count FJ pipe as one
            .length % 2 == 1
    }

    private fun createPipeLoop(): List<Coord> = buildList {
        pipes.indices.forEach { i ->
            pipes[0].indices.forEach { j ->
                if (pipes[i][j] == 'S') add(Coord(i, j))
            }
        }
        while (true) {
            val nextPipes = calculateNext(this)
            if (nextPipes.isEmpty()) break
            val (dir, next) = nextPipes
            if (dir !in this) add(dir)
            if (next !in this) add(next)
        }
    }

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

private fun <E> MutableList<MutableList<E>>.print() {
    this.forEach { row ->
        println(row.joinToString("") { it.toString() })
    }
}

private operator fun MutableList<MutableList<Char>>.set(coord: Coord, value: Char) {
    this[coord.r][coord.c] = value
}

private operator fun List<List<Char>>.get(coord: Coord): Char {
    return this[coord.r][coord.c]
}

fun main() {
    val name = Day10::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test2.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day10(testInput), Day10(realInput), printTimings = true)
}