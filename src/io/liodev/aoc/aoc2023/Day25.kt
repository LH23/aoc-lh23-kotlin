package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.times

// --- 2023 Day 25: Snowverload ---
class Day25(input: String) : Day<Int> {
    override val expectedValues = listOf(54, 562772, 12_25_2023, 12_25_2023)

    private val graph = constructGraph(input.split("\n").map { it.toConnections() })

    data class Connections(val component: String, val connections: List<String>)

    private fun String.toConnections(): Connections {
        val label = this.substringBefore(':')
        val connections = this.substringAfter(": ").split(' ')
        return Connections(label, connections)
    }

    override fun solvePart1(): Int {
        var visited: Set<String>
        do {
            getVisitedEdgesCount(graph)
                .asSequence()
                .maxBy { it.value }.key.let { (u, v) ->
                    graph[u]!!.remove(v)
                    graph[v]!!.remove(u)
                }
            visited = visitComponentOf(graph.keys.first(), graph)
        } while (visited.size == graph.keys.size)
        return visited.size * (graph.keys.size - visited.size)
    }

    private fun constructGraph(connections: List<Connections>) =
        buildMap<String, MutableList<String>> {
            connections.forEach { conn ->
                this.getOrPut(conn.component) { mutableListOf() }.addAll(conn.connections)
                conn.connections.forEach { comp ->
                    this.getOrPut(comp) { mutableListOf() }.add(conn.component)
                }
            }
        }.toMutableMap()

    private fun getVisitedEdgesCount(graph: MutableMap<String, MutableList<String>>): MutableMap<Pair<String, String>, Int> {
        paths.clear()
        val visitedEdgesCount = mutableMapOf<Pair<String, String>, Int>()
        val v = graph.keys
        (v * v).asSequence().filter { (a, b) -> a != b }.takeRandom(20) { (a, b) ->
            for ((v1, v2) in calculatePath(a, b, graph).zipWithNext()) {
                if (v1 < v2)
                    visitedEdgesCount[v1 to v2] = visitedEdgesCount.getOrPut(v1 to v2) { 1 } + 1
                else
                    visitedEdgesCount[v2 to v1] = visitedEdgesCount.getOrPut(v2 to v1) { 1 } + 1
            }
        }
        return visitedEdgesCount
    }

    private val paths = mutableMapOf<Pair<String, String>, List<String>>()
    private fun calculatePath(a: String, b: String, graph: Map<String, MutableList<String>>) =
        if (paths[a to b] != null) paths[a to b]!!
        else if (paths[b to a] != null) paths[b to a]!!.reversed()
        else {
            val queue = ArrayDeque<Pair<String, List<String>>>()
            queue.add(a to listOf(a))
            val visited = mutableSetOf<String>()
            while (queue.isNotEmpty()) {
                val (e, path) = queue.removeFirst()
                visited.add(e)
                paths[a to e] = path
                if (b in graph[e]!!) {
                    paths[a to b] = path + listOf(b)
                    break
                } else if (paths[e to b] != null) {
                    paths[a to b] = path + (paths[e to b]!!).drop(1)
                    break
                } else {
                    graph[e]!!.filter { it !in visited }.forEach {
                        queue.add(it to path + listOf(it))
                    }
                }
            }
            paths[a to b]!!
        }


    private fun visitComponentOf(label: String, graph: MutableMap<String, MutableList<String>>): Set<String> {
        val queue = ArrayDeque<String>()
        queue.add(label)
        val visited = mutableSetOf<String>()
        while (queue.isNotEmpty()) {
            val e = queue.removeFirst()
            visited.add(e)
            queue.addAll(graph[e]!!.filter { it !in visited })
        }
        return visited
    }

    override fun solvePart2() = 12_25_2023 // MERRY CHRISTMAS!!
}

private fun <E> Sequence<E>.takeRandom(n: Int, function: (E) -> Unit) {
    return repeat(n) { function(this.shuffled().first()) }
}

fun main() {
    val name = Day25::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day25(testInput), Day25(realInput), printTimings = true)
}