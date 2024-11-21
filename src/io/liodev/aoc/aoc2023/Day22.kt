package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord3D

// --- 2023 Day 22: Sand Slabs ---
class Day22(input: String) : Day<Int> {
    override val expectedValues = listOf(5, 499, 7, 95059)

    private val bricks = input.split("\n").map { it.toBrick() }
    private fun String.toBrick(): Brick {
        val parsed = this.split('~').map { coord -> coord.split(',').map { it.toInt() } }
        return Brick(
            Coord3D(parsed[0][0], parsed[0][1], parsed[0][2]),
            Coord3D(parsed[1][0], parsed[1][1], parsed[1][2]),
        )
    }

    data class Brick(val a: Coord3D, val b: Coord3D) {
        fun goDown(): Brick {
            return Brick(a.goDown(), b.goDown())
        }

        fun canGoUp(bricks: List<Brick>): Boolean {
            return piecesAbove(bricks).isEmpty()
        }

        fun canGoDown(bricks: List<Brick>): Boolean {
            return this.a.z > 1 && piecesBelow(bricks).isEmpty()
        }

        fun piecesAbove(bricks: List<Brick>): List<Brick> {
            return bricks.filter { other ->
                this.b.z == other.a.z - 1 && this.overlapsXY(other)
            }
        }

        fun piecesBelow(bricks: List<Brick>): List<Brick> {
            return bricks.filter { other ->
                this.a.z == other.b.z + 1 && this.overlapsXY(other)
            }
        }

        private infix fun overlapsXY(other: Brick): Boolean =
            (a.x..b.x overlaps other.a.x..other.b.x &&
                a.y..b.y overlaps other.a.y..other.b.y)

        infix fun overlaps(other: Brick): Boolean =
            (a.x..b.x overlaps other.a.x..other.b.x &&
                a.y..b.y overlaps other.a.y..other.b.y &&
                a.z..b.z overlaps other.a.z..other.b.z)

        fun calculateFalling(piecesAboveBelowMap: Map<Brick, Pair<List<Brick>, List<Brick>>>): Int {
            val falling = mutableSetOf<Brick>(this)
            val toProcess = ArrayDeque<Brick>()
            toProcess.add(this)

            while (toProcess.isNotEmpty()) {
                val currentPiece = toProcess.removeFirst()
                val newOnes = piecesAboveBelowMap[currentPiece]!!.above().filter { brick ->
                    piecesAboveBelowMap[brick]!!.below().all { it in falling }
                }
                falling.addAll(newOnes)
                toProcess.addAll(newOnes)
            }
            return falling.size - 1
        }

        private fun <A, B> Pair<A, B>.above(): A = this.first
        private fun <A, B> Pair<A, B>.below(): B = this.second
    }

    private fun stabilizeDown(bricks: List<Brick>): List<Brick> {
        val result = mutableListOf<Brick>()
        for (b in bricks) {
            var tmp = b
            while (tmp.canGoDown(result)) {
                tmp = tmp.goDown()
            }
            result.add(tmp)
            // DEBUG ISSUE
//            (result * result).map { (a,b) ->
//                if (a != b && a overlaps b) error("Adding $tmp generated a consistency error: $a overlaps $b")
//            }
        }
        return result
    }

    override fun solvePart1(): Int {
        val stable = stabilizeDown(bricks.sortedBy { it.a.z })
        return stable.count { brick ->
            brick.canGoUp(stable) || brick.piecesAbove(stable)
                .all { it.piecesBelow(stable).size > 1 }
        }
    }

    override fun solvePart2(): Int {
        val stable = stabilizeDown(bricks.sortedBy { it.a.z })
        val stableMap = stable.associateWith { it.piecesAbove(stable) to it.piecesBelow(stable) }
        return stable.filter { brick ->
            brick.piecesAbove(stable).any { it.piecesBelow(stable).size == 1 }
        }.sumOf { it.calculateFalling(stableMap) }
    }

}

infix fun IntRange.overlaps(other: IntRange) =
    this.first <= other.last && other.first <= this.last


fun main() {
    val name = Day22::class.simpleName
    val year = 2023
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/${name}.txt")
    runDay(Day22(testInput), Day22(realInput), year, printTimings = true)
}