package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.validIndex
import java.util.PriorityQueue

// --- 2023 Day 17: Clumsy Crucible ---
class Day17(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(102, 1044, 94, 1227, 59, 71)

    private val heatLossMap = input.split("\n").map { row -> row.map { it.digitToInt() } }

    override fun solvePart1() =
        findOptimalPath(
            Coord(0, 0),
            Coord(heatLossMap.lastIndex, heatLossMap[0].lastIndex),
            excludeDirectionIf = { it.currentDir == it.parent?.currentDir && it.stepsInDir > 3 },
        ).sumOf { heatLossMap[it] }

    override fun solvePart2() =
        findOptimalPath(
            Coord(0, 0),
            Coord(heatLossMap.lastIndex, heatLossMap[0].lastIndex),
            excludeDirectionIf = {
                (it.currentDir == it.parent?.currentDir && it.stepsInDir > 10) ||
                    (it.currentDir != it.parent?.currentDir && it.parent?.stepsInDir!! < 4)
            },
            endCondition = { it.stepsInDir >= 4 },
        ).sumOf { heatLossMap[it] }

    private fun findOptimalPath(
        origin: Coord,
        end: Coord,
        excludeDirectionIf: (Node) -> Boolean,
        endCondition: (Node) -> Boolean = { true },
    ): List<Coord> {
        val openPri = PriorityQueue<Node> { a, b -> a.f - b.f }
        val openMap = HashMap<Node, Int>()
        val closedSet = HashSet<Node>()

        val nodeStart = Node(origin, 1, 1, null)
        openPri.offer(nodeStart)

        while (openPri.isNotEmpty()) {
            val current = openPri.poll()
            closedSet.add(current)

            if (current.position == end && endCondition(current)) {
                return constructPath(current)
            }

            for ((i, n) in current.position.getCardinalBorder().withIndex()) {
                if (!heatLossMap.validIndex(n) || n == current.parent?.position) continue

                val steps = if (current.currentDir == i) current.stepsInDir + 1 else 1
                val node = Node(n, i, steps, current)
                node.g = current.g + heatLossMap[node.position]
                node.h = 0 // abs(node.position.r - end.r) + abs(node.position.c - end.c)
                node.f = node.g + node.h

                if (closedSet.contains(node)) continue
                if (openMap.getOrElse(node) { Int.MAX_VALUE } <= node.g) continue
                if (excludeDirectionIf(node)) continue

                openPri.offer(node)
                openMap[node] = node.g
            }
        }
        return listOf()
    }

    private fun constructPath(node: Node): List<Coord> {
        val path = mutableListOf<Coord>()
        var next = node
        while (next.parent != null) {
            path.add(next.position)
            next = next.parent!!
        }
        // heatLossMap.printPathInMatrix(path, 0)
        return path.reversed()
    }

    private data class Node(
        val position: Coord,
        val currentDir: Int,
        val stepsInDir: Int,
        val parent: Node?,
    ) {
        var f = 0
        var g = 0
        var h = 0

        override fun toString() = "($position $currentDir $stepsInDir)"

        override fun equals(other: Any?): Boolean = other is Node && this.toString() == other.toString()

        override fun hashCode(): Int = this.toString().hashCode()
    }
}

fun main() {
    val name = Day17::class.simpleName
    val year = 2023
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val testInput2 = readInputAsString("src/input/$year/${name}_test2.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(
        Day17(testInput),
        Day17(realInput),
        year,
        extraDays = listOf(Day17(testInput2)),
        printTimings = true,
    )
}
