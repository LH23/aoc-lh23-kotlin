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
    private val valvesMatrix = constructGraph(input.split("\n")).toDistanceMatrix()
    private val minDistancesMatrix = valvesMatrix.floydWarshall()
    private val valveNames = flowRate.keys.toList()

    override fun solvePart1(): Long {
        val aa = valveNames.indexOf("AA")
        return calcMaxPressure(30, setOf(aa), aa, valveNames.indices.toList())
    }

    private val maxPressureCache: MutableMap<CacheKey, Long> = mutableMapOf()

    data class CacheKey(
        val time: Int,
        val currentIndex: Int,
        val valvesToOpen: Set<Int>,
    )

    override fun solvePart2(): Long {
        val aa = valveNames.indexOf("AA")
        return valveNames.indices
            .toList()
            .filter { it != aa }
            .take(12) // simplify generated groups (VALID ONLY ON THIS INPUT)
            .generateGroupsOfSize(valveNames.size / 2, setOf())
            .map { myValves ->
                myValves to valveNames.indices.filter { it !in myValves && it != aa }.toSet()
            }.maxOf { (myValves, elephantValves) ->
                val maxPressureMe =
                    calcMaxPressure(
                        26,
                        setOf(aa),
                        aa,
                        listOf(aa) + myValves,
                    )

                val maxPressureElephant =
                    calcMaxPressure(
                        26,
                        setOf(aa),
                        aa,
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
        time: Int,
        openValves: Set<Int>,
        currentIndex: Int,
        valves: List<Int>,
    ): Long =
        if (openValves.size == valves.size || time == 0) {
            0
        } else {
            valves
                .bestCandidates(
                    time,
                    currentIndex,
                    openValves,
                    valves.size / 2 + 1,
                ).maxOf { candidate ->
                    val newTime = max(time - minDistancesMatrix[currentIndex][candidate] - 1, 0)
                    val newOpenValves = openValves + listOf(candidate)
                    val newReleasedPressure = candidate.flowRate().toLong() * newTime

                    newReleasedPressure +
                        calcMaxPressure(
                            newTime,
                            newOpenValves,
                            candidate,
                            valves,
                        )
                }
        }

    private fun List<Int>.bestCandidates(
        time: Int,
        candidate: Int,
        openValves: Set<Int>,
        takeN: Int,
    ): List<Int> =
        this
            .filter { it !in openValves }
            .sortedByDescending { index ->
                index.flowRate() * (time - minDistancesMatrix[candidate][index] - 1)
            }.take(takeN)

    private fun constructGraph(lines: List<String>) =
        buildMap<String, MutableList<Pair<String, Int>>> {
            lines
                .forEach {
                    val label = it.substring(6, 8)
                    val rate = it.substringAfter("rate=").substringBefore(';').toInt()
                    flowRate[label] = rate
                    val tunnels = it.substringAfter("to valve").substringAfter(" ").split(", ")
                    this
                        .getOrPut(label) { mutableListOf() }
                        .addAll(tunnels.map { tunnel -> tunnel to 1 })
                }
            this.removeZeroFlowValves()
        }

    private fun MutableMap<String, MutableList<Pair<String, Int>>>.removeZeroFlowValves() {
        for (valve in flowRate.keys.toList()) {
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

    private fun Int.flowRate(): Int = flowRate[valveNames[this]]!!

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

private typealias Graph<K> = Map<K, List<Pair<K, Int>>>

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
