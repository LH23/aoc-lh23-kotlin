package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.set
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.printMatrix
import io.liodev.aoc.utils.printPathInMatrix
import io.liodev.aoc.utils.validIndex
import java.util.PriorityQueue
import kotlin.math.abs

// --- 2023 Day 17: Clumsy Crucible ---
class Day17(input: String) : Day<Int> {
    override val expectedValues = listOf(102, 1044, 94, 1227, 59, 71)

    private val heatLossMap = input.split("\n").map { row -> row.map { it.digitToInt() } }

    override fun solvePart1() = findOptimalPath(
        Coord(0, 0),
        Coord(heatLossMap.lastIndex, heatLossMap[0].lastIndex),
        checkDirCondition = { it.currentDir == it.parent?.currentDir && it.stepsInDir > 3 }
    ).sumOf { heatLossMap[it] }

    override fun solvePart2() = findOptimalPath(
        Coord(0, 0),
        Coord(heatLossMap.lastIndex, heatLossMap[0].lastIndex),
        checkDirCondition = {
            (it.currentDir == it.parent?.currentDir && it.stepsInDir == 10) ||
                    (it.currentDir != it.parent?.currentDir && it.stepsInDir >= 4)
        }
    ).sumOf { heatLossMap[it] }

    private fun findOptimalPath(
        origin: Coord,
        end: Coord,
        checkDirCondition: (Node) -> Boolean
    ): List<Coord> {
        val openPri = PriorityQueue<Node>() { a, b -> a.f - b.f }
        val openMap = HashMap<Node, Int>()
        val closedSet = HashSet<Node>()

        val nodeStart = Node(origin, 0, 0, null)
        openPri.offer(nodeStart)

        while (openPri.isNotEmpty()) {
            val current = openPri.poll()
            closedSet.add(current)

            if (current.position == end) {
                val path = mutableListOf<Coord>()
                var next = current
                while (next.parent != null) {
                    path.add(next.position)
                    next = next.parent!!
                }
                //heatLossMap.printPathInMatrix(path, 0)
                return path.reversed()
            }

            for ((i, n) in current.position.getCardinalBorder().withIndex()) {
                if (!heatLossMap.validIndex(n) || n == current.parent?.position) continue

                val node =
                    Node(n, i, if (current.currentDir == i) current.stepsInDir + 1 else 1, current)
                node.g = current.g + heatLossMap[node.position]
                node.h =
                    abs(node.position.r - end.r) + abs(node.position.c - end.c) // manhattan distance
                node.f = node.g + node.h

                if (closedSet.contains(node)) continue

                if (checkDirCondition(node)) continue

                val cp = openMap[node]
                //println("CP of $node = $cp <= ${node.g}?")
                if (cp != null && cp <= node.g) {
                    //println("Discarded $n by saved g: $cp <= ${node.g}")
                    continue
                }

                openPri.offer(node)
                //println("Added $node, size ${openPri.size}")
                openMap[node] = node.g
            }
        }
        return listOf()
    }
}

data class Node(val position: Coord, val currentDir: Int, val stepsInDir: Int, val parent: Node?) {
    var f = 0
    var g = 0
    var h = 0
    override fun toString() =
        "(${position} $stepsInDir $currentDir)"

    override fun equals(other: Any?): Boolean = other is Node && this.toString() == other.toString()
    override fun hashCode(): Int = this.toString().hashCode()
}

fun main() {
    val name = Day17::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val testInput2 = readInputAsString("src/input/2023/${name}_test2.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(
        Day17(testInput),
        Day17(realInput),
        extraDays = listOf(Day17(testInput2)),
        printTimings = true
    )
}