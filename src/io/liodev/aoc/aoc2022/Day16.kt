package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import kotlin.math.max

// --- Day 16 2022: Proboscidea Volcanium ---
class Day16(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(1651L, 1754, 1707, 2474)

    private val flowRate = mutableMapOf<String, Int>()
    private val pipes = constructPipesGraph(input.split("\n"))
    private val pipesMatrix = pipes.toDistanceMatrix()
    private val minDistancesMatrix = pipesMatrix.floydWarshall()
    private val pipeKeys = pipes.keys.toList()

    private fun constructPipesGraph(lines: List<String>) =
        buildMap<String, MutableList<Pair<String, Int>>> {
            lines.forEach {
                val label = it.substring(6, 8)
                val rate = it.substringAfter("rate=").substringBefore(';').toInt()
                flowRate[label] = rate
                val tunnels = it.substringAfter("to valve").substringAfter(" ").split(", ")
                this
                    .getOrPut(label) { mutableListOf() }
                    .addAll(tunnels.map { tunnel -> tunnel to 1 })
            }
            val valves = flowRate.keys.toList()
            for (valve in valves) {
                if (valve != "AA" && flowRate[valve] == 0) {
                    this[valve]!!.forEach { (dest, distance) ->
                        this[dest]!!.removeIf { it.first == valve }
                        this[dest]!!.addOrReplaceAll(dest, distance, this[valve]!!)
                    }
                    this[valve]!!.clear()
                    this.remove(valve)
                    flowRate.remove(valve)
                }
            }
        }

    override fun solvePart1(): Long {
        val aa = pipeKeys.indexOf("AA")
        return calcMaxPressure(minDistancesMatrix, 30, setOf(aa), aa, 0L)
    }

    override fun solvePart2(): Long {
        val aa = pipeKeys.indexOf("AA")
        return pipeKeys.indices
            .toList()
            .filter { it != aa }
            .generateGroupsOfSize(pipeKeys.size / 2, setOf())
            .maxOf { myValves ->
                val elephantValves = pipeKeys.indices.filter { it !in myValves && it != aa }
                val maxPressureMe =
                    calcMaxPressure(
                        minDistancesMatrix,
                        26,
                        setOf(aa),
                        aa,
                        0L,
                        listOf(aa) + myValves,
                    )
                val maxPressureElephant =
                    calcMaxPressure(
                        minDistancesMatrix,
                        26,
                        setOf(aa),
                        aa,
                        0L,
                        listOf(aa) + elephantValves,
                    )

                maxPressureMe + maxPressureElephant
            }
    }

    private fun List<Int>.generateGroupsOfSize(
        size: Int,
        with: Set<Int>,
    ): List<Set<Int>> =
        when {
            with.size == size -> listOf(with)
            this.isEmpty() -> listOf()
            else ->
                this.drop(1).generateGroupsOfSize(size, with + this.first()) +
                    this.drop(1).generateGroupsOfSize(size, with)
        }

    private fun calcMaxPressure(
        minDistancesMatrix: Array<IntArray>,
        time: Int,
        openValves: Set<Int>,
        currentIndex: Int,
        releasedPressure: Long,
        pipes: List<Int> = pipeKeys.indices.toList(),
    ): Long =
        if (openValves.size == pipes.size || time == 0) {
            releasedPressure
        } else {
            minDistancesMatrix[currentIndex]
                .bestCandidates(pipes, time, openValves, pipes.size / 2 + 1)
                .maxOf { candidate ->
                    val newTime = max(time - minDistancesMatrix[currentIndex][candidate] - 1, 0)
                    val newOpenValves = openValves + listOf(candidate)
                    val newReleasedPressure =
                        releasedPressure + flowRate[pipeKeys[candidate]]!! * newTime
                    val result =
                        calcMaxPressure(
                            minDistancesMatrix,
                            newTime,
                            newOpenValves,
                            candidate,
                            newReleasedPressure,
                            pipes,
                        )
                    result
                }
        }

    private fun IntArray.bestCandidates(
        pipes: List<Int>,
        time: Int,
        openValves: Set<Int>,
        takeN: Int = 10,
    ): List<Int> =
        pipes
            .filter { it !in openValves }
            .sortedByDescending { index ->
                val remaining = time - this[index] - 1
                flowRate[pipeKeys[index]]!!.toLong() * remaining
            }.take(takeN)

    private fun MutableList<Pair<String, Int>>.addOrReplaceAll(
        dest: String,
        destDistance: Int,
        list: List<Pair<String, Int>>,
    ) {
        for ((label, distance) in list) {
            if (dest == label) continue
            val currentTunnel = this.firstOrNull { it.first == label }
            if (currentTunnel != null) {
                if (currentTunnel.second > distance + 1) {
                    this.remove(currentTunnel)
                    this.add(label to distance + destDistance)
                }
            } else {
                this.add(label to distance + destDistance)
            }
        }
    }
}

private fun Array<IntArray>.printMatrix() = this.map { it.joinToString(" ") { n -> "$n" } }.joinToString("\n") { line -> line }

typealias Graph<K> = Map<K, List<Pair<K, Int>>>

private fun <K> Graph<K>.toDistanceMatrix(): Array<IntArray> {
    val keys = this.keys.toList()
    val matrix = Array(keys.size) { IntArray(keys.size) { Int.MAX_VALUE } }
    keys.forEachIndexed { i, key ->
        matrix[i][i] = 0
        this[key]!!.forEach { (dest, distance) ->
            matrix[i][keys.indexOf(dest)] = distance
        }
    }
    return matrix
}

fun Array<IntArray>.floydWarshall(): Array<IntArray> {
    val n = this.size
    val dist = this.clone()
    for (k in 0..<n) {
        for (i in 0..<n) {
            for (j in 0..<n) {
                if (dist[i][k] != Int.MAX_VALUE &&
                    dist[k][j] != Int.MAX_VALUE &&
                    dist[i][k] + dist[k][j] < dist[i][j]
                ) {
                    dist[i][j] = dist[i][k] + dist[k][j]
                }
            }
        }
    }
    return dist
}

fun main() {
    val name = Day16::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day16(testInput), Day16(realInput), year, printTimings = true)
}
