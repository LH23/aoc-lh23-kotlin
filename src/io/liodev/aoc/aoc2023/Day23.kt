package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.printPathInMatrix
import io.liodev.aoc.utils.times

// --- Day 2023: A Long Walk ---
class Day23(input: String) : Day<Int> {
    override val expectedValues = listOf(94, 2254, 154, 6394)

    private val maze = input.split("\n").map { it.toList() }

    private fun findLongestPathSize(
        origin: Coord,
        end: Coord,
        graph: Map<Coord, List<Coord>>,
    ): Int {
        return findLongestTopo(origin, end, graph, topologicalSort(graph))
    }

    private val closedSetRec = HashSet<Coord>()
    private fun findLongestRec(
        a: Coord,
        b: Coord,
        graph: Map<Coord, List<Pair<Coord, Int>>>,
    ): Int {
        return if (a == b) return 0
        else {
            closedSetRec.add(a)
            var distanceA = Int.MIN_VALUE
            for ((next, weight) in graph[a]!!) {
                if (next in closedSetRec) continue
                else distanceA = distanceA.coerceAtLeast(findLongestRec(next, b, graph) + weight)
            }
            closedSetRec.remove(a)
            distanceA
        }
    }

    private fun findLongestTopo(
        a: Coord,
        b: Coord,
        graph: Map<Coord, List<Coord>>,
        toposort: MutableList<Coord>
    ): Int {
        val closedSet = HashSet<Coord>()

        val distance: MutableMap<Coord, Pair<Int, Coord?>> =
            toposort.associateWith { Int.MIN_VALUE to null }.toMutableMap()
        distance[toposort.find { it == a }!!] = 0 to null

        while (toposort.isNotEmpty()) {
            val current = toposort.removeFirst()
            if (distance[current]!!.first != Int.MIN_VALUE) {
                for (next in graph[current] ?: emptyList()) {
                    if (distance[next]!!.first < distance[current]!!.first + 1 && next !in closedSet) {
                        distance[next] = distance[current]!!.first + 1 to current
                    }
                }
            }
            closedSet.add(current)
        }
        return distance[b]!!.first
    }

    // DEBUG
    private fun constructPath(map: MutableMap<Coord, Pair<Int, Coord?>>, end: Coord): List<Coord> {
        val path = mutableListOf<Coord>()
        var next = map[end]!!.second
        while (next != null) {
            path.add(next)
            next = map[next]!!.second
        }
        maze.printPathInMatrix(path, empty = null, fill = 'O')
        return path.reversed()
    }

    override fun solvePart1(): Int {
        return findLongestPathSize(Coord(0, 1), Coord(maze.lastIndex, maze[0].lastIndex - 1),
            maze.constructGraph(
                excludeDirectionIf = { node ->
                    (!node.position.validIndex(maze)) || when (maze[node.position]) {
                        '#' -> true
                        '>' -> node.position.cardinalBorderDirs[node.currentDir] != Dir.East
                        '<' -> node.position.cardinalBorderDirs[node.currentDir] != Dir.West
                        'v' -> node.position.cardinalBorderDirs[node.currentDir] != Dir.South
                        '^' -> node.position.cardinalBorderDirs[node.currentDir] != Dir.North
                        else -> false
                    }
                }
            )
        )
    }

    override fun solvePart2(): Int {
        return findLongestRec(Coord(0, 1), Coord(maze.lastIndex, maze[0].lastIndex - 1),
            simplifyGraph(
                maze.constructGraph(
                    excludeDirectionIf = { node ->
                        !node.position.validIndex(maze) || maze[node.position] == '#'
                    }
                )
            )
        )
    }

    private fun simplifyGraph(graph: Map<Coord, List<Coord>>): Map<Coord, MutableList<Pair<Coord, Int>>> =
        buildMap {
            for ((coord, edges) in graph) {
                if (edges.size != 2) for (nextCoord in edges) {
                    var current = coord
                    var next = nextCoord
                    var weight = 1
                    while (true) {
                        val nextEdge = (graph[next]?.toSet() ?: emptySet()) - setOf(current)
                        if (nextEdge.size != 1) break
                        current = next
                        next = nextEdge.single()
                        weight++
                    }
                    getOrPut(coord) { mutableListOf() }.add(next to weight)
                }
            }
        }

    private fun List<List<Char>>.constructGraph(
        excludeDirectionIf: (Node) -> Boolean
    ) =
        (this.indices * this[0].indices)
            .filter { (r, c) ->
                this[r][c] != '#'
            }.map { (r, c) -> Coord(r, c) }
            .associateWith { coord ->
                coord.getCardinalBorder().withIndex().filter { (i, n) ->
                    val node = Node(n, i)
                    !excludeDirectionIf(node)
                }.map { it.value }
            }

    private data class Node(val position: Coord, val currentDir: Int)

    private val visited = mutableSetOf<Coord>()
    private val topologicalOrder = mutableListOf<Coord>()
    private fun topologicalSort(graph: Map<Coord, List<Coord>>): MutableList<Coord> {
        for (node in graph.keys) {
            if (node !in visited) {
                graph.dfs(node)
            }
        }
        return topologicalOrder.asReversed().toMutableList()
    }

    private fun Map<Coord, List<Coord>>.dfs(node: Coord) {
        visited.add(node)
        for (neighbour in this[node] ?: emptyList()) {
            if (neighbour !in visited) {
                dfs(neighbour)
            }
        }
        topologicalOrder.add(node)
    }
}

fun main() {
    val name = Day23::class.simpleName
    val year = 2023
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/${name}.txt")
    runDay(Day23(testInput), Day23(realInput), year)
}