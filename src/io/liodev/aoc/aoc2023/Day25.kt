package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.times

// --- 2023 Day 25: Snowverload ---
class Day25(input: String) : Day<Int> {
    override val expectedValues = listOf(54, 562772, 12_25_2023, 12_25_2023)

    private val graph = constructGraph(input.split("\n"))

    private fun constructGraph(lines: List<String>) =
        buildMap<String, MutableList<String>> {
            lines.forEach {
                val label = it.substringBefore(':')
                val connections = it.substringAfter(": ").split(' ')

                this.getOrPut(label) { mutableListOf() }.addAll(connections)
                connections.forEach { comp ->
                    this.getOrPut(comp) { mutableListOf() }.add(label)
                }
            }
        }.toMutableMap()

    override fun solvePart1(): Int {
        var visited: Set<String>

        while (true) {
            val cut = mutableListOf<Pair<String, String>>()

            repeat(2) { cut += removeMostVisitedEdge(graph) }
            do {
                cut += removeMostVisitedEdge(graph)
                visited = visitComponentOf(graph.keys.first(), graph)
            } while (visited.size == graph.keys.size)

            minimizeCut(cut, graph)

            if (cut.size == 3) break
            else cut.forEach { (u, v) ->
                graph[u]!!.add(v)
                graph[v]!!.add(u)
            }
        }

        // STOCHASTIC RESULT, THERE IS AN INFINITESIMAL POSSIBILITY OF FAILURE
        return visited.size * (graph.keys.size - visited.size)
    }

    private fun minimizeCut(
        cut: MutableList<Pair<String, String>>,
        graph: MutableMap<String, MutableList<String>>
    ) {
        cut.dropLast(1).forEach { (u, v) ->
            graph[u]!!.add(v)
            graph[v]!!.add(u)
            if (visitComponentOf(graph.keys.first(), graph).size == graph.keys.size) {
                graph[u]!!.remove(v)
                graph[v]!!.remove(u)
            } else {
                cut.remove(u to v)
            }
        }
    }

    private fun removeMostVisitedEdge(
        g: MutableMap<String, MutableList<String>>
    ): Pair<String, String> {
        getVisitedEdgesCount(g)
            .asSequence()
            .maxBy { it.value }.key.let { (u, v) ->
                g[u]!!.remove(v)
                g[v]!!.remove(u)
                return u to v
            }
    }

    private fun getVisitedEdgesCount(graph: MutableMap<String, MutableList<String>>): MutableMap<Pair<String, String>, Int> {
        paths.clear()
        val visitedEdgesCount = mutableMapOf<Pair<String, String>, Int>()
        val v = graph.keys
        (v * v).asSequence().filter { (a, b) -> a != b }.takeRandom(10) { (a, b) ->
            for ((v1, v2) in calculatePath(a, b, graph).zipWithNext()) {
                val ordPair = if (v1 < v2) v1 to v2 else v2 to v1
                visitedEdgesCount[ordPair] = visitedEdgesCount.getOrPut(ordPair) { 1 } + 1
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
            paths[a to b] ?: listOf()
        }


    private fun visitComponentOf(
        label: String,
        graph: MutableMap<String, MutableList<String>>
    ): Set<String> {
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