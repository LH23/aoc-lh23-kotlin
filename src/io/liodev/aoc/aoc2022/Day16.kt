package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import kotlin.math.max
import kotlin.math.min

// --- Day 16 2022: Proboscidea Volcanium ---
class Day16(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(1651L, 1754, 1707, 2474)

    private val flowRate = mutableMapOf<String, Int>()
    private val pipes = constructPipesGraph(input.split("\n"))
    private val pipeKeys = pipes.keys.toList()
    private val cacheMaxPressure: MutableMap<Set<Int>, Long> = mutableMapOf()

    private fun constructPipesGraph(lines: List<String>) =
        buildMap<String, MutableList<Pair<String, Int>>> {
            lines.forEach {
                // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
                val label = it.substring(6, 8)
                val rate = it.substringAfter("rate=").substringBefore(';').toInt()
                flowRate.set(label, rate)
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
        cacheMaxPressure.clear()
        val matrix = pipes.toDistanceMatrix()
        val minDistancesMatrix = matrix.floydWarshall()
        // println("pipes $pipes")
        // println("minDistances:\n${minDistancesMatrix.printMatrix()}")
        val aa = pipeKeys.indexOf("AA")
        return calcMaxPressure(minDistancesMatrix, 30, setOf(aa), aa, 0L)
    }

    override fun solvePart2(): Long {
        cacheMaxPressure.clear()
        val matrix = pipes.toDistanceMatrix()
        val minDistancesMatrix = matrix.floydWarshall()
        // println("pipes $pipes")
        // println("minDistances:\n${minDistancesMatrix.printMatrix()}")
        val aa = pipeKeys.indexOf("AA")
        return calcMaxPressureDouble(
            minDistancesMatrix,
            26,
            setOf(aa),
            0,
            aa,
            0,
            aa,
            0L,
        )
    }

    private fun calcMaxPressure(
        minDistancesMatrix: Array<IntArray>,
        time: Int,
        openValves: Set<Int>,
        currentIndex: Int,
        releasedPressure: Long,
    ): Long =
        if (openValves.size == flowRate.size || time == 0) {
            // if (releasedPressure > 1700) println("final pressure $releasedPressure, ${openValves.map { pipeKeys[it] }} time $time s")
            releasedPressure
//        } else if (cacheMaxPressure.getOrDefault(openValves, Long.MIN_VALUE) > releasedPressure) {
//            println("trim $openValves: ${cacheMaxPressure.getOrDefault(openValves, Long.MIN_VALUE)} > $releasedPressure")
//            0// trim recursion
        } else {
            pipeKeys.indices
                .filter { it !in openValves }
                .sortedByDescending { index ->
                    flowRate[pipeKeys[index]]!! *
                        max(
                            time - minDistancesMatrix[currentIndex][index] - 1,
                            0,
                        )
                }.take(10)
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
                        )
                    // println("New open valves $newOpenValves candidate $candidate pressure: $releasedPressure + ${flowRate[pipeKeys[candidate]]!!} * $newTime = $result",)
//                    if (cacheMaxPressure.getOrPut(newOpenValves, defaultValue = { Long.MIN_VALUE }) <= result) {
//                        cacheMaxPressure[newOpenValves] = result
//                    }
                    result
                }
        }

    var maxPressure = 0L

    private fun calcMaxPressureDouble(
        minDistancesMatrix: Array<IntArray>,
        time: Int,
        openValves: Set<Int>,
        distanceToDest1: Int,
        currentDest1: Int,
        distanceToDest2: Int,
        currentDest2: Int,
        releasedPressure: Long,
        debugList: List<Any> = emptyList(),
    ): Long {
        if (openValves.size == flowRate.size || time == 0) {
//            if (releasedPressure > 1800) {
//                println("final pressure $releasedPressure, ${openValves.map { pipeKeys[it] }} time $time s, debugList $debugList")
//            }
            if (releasedPressure > 2300 && releasedPressure > maxPressure) {
                println("new maxPresure $releasedPressure debugList $debugList")
                maxPressure = releasedPressure
            }
            return releasedPressure
        }

        val candidatesPairs =
            pipeKeys.indices
                .filter { it !in openValves }
                .unorderedPairs(distanceToDest1, currentDest1, distanceToDest2, currentDest2)

        if (candidatesPairs.isEmpty()) return releasedPressure

        return candidatesPairs
            .maxOf { (candidate1, candidate2) ->
                val distCandidate1 =
                    if (distanceToDest1 > 0) distanceToDest1 else minDistancesMatrix[currentDest1][candidate1] + 1
                val distCandidate2 =
                    if (distanceToDest2 > 0) distanceToDest2 else minDistancesMatrix[currentDest2][candidate2] + 1
                val travelTime = min(distCandidate1, distCandidate2)
                val newTime = max(time - travelTime, 0)

//                println(
//                    "me going to ${pipeKeys[candidate1]} (in ${distCandidate1}s), elephant going to ${pipeKeys[candidate2]} (in ${distCandidate2}s), travelTime $travelTime, newTime $newTime",
//                )
                val opened = mutableSetOf<Int>()
                val pressure1 =
                    if (distCandidate1 == travelTime && candidate1 !in openValves) {
                        // println("ME Opening ${pipeKeys[candidate1]} at ${26 - newTime} (opened already ${openValves.map { pipeKeys[it] }})")
                        opened.add(candidate1)
                        flowRate[pipeKeys[candidate1]]!!.toLong() * newTime
                    } else {
                        0
                    }
                val pressure2 =
                    if (distCandidate2 == travelTime && candidate2 !in (openValves + opened)) {
                        // println("ELEPHANT Opening ${pipeKeys[candidate2]} at ${26 - newTime} (opened already ${openValves.map { pipeKeys[it] }})")
                        opened.add(candidate2)
                        flowRate[pipeKeys[candidate2]]!!.toLong() * newTime
                    } else {
                        0
                    }

                val result =
                    calcMaxPressureDouble(
                        minDistancesMatrix,
                        newTime,
                        openValves + opened,
                        distCandidate1 - travelTime,
                        candidate1,
                        distCandidate2 - travelTime,
                        candidate2,
                        releasedPressure + pressure1 + pressure2,
                        debugList + "Opened: ${opened.map { pipeKeys[it] }} (at ${newTime}s) (p1 $pressure1 p2 $pressure2))",
                    )

                // println("open valves ${openValves + listOf(candidate)} candidate $candidate pressure: $releasedPressure + ${flowRate[pipeKeys[candidate]]!!} * $newTime = $result")
                result
            }
    }

    private fun List<Int>.unorderedPairs(
        distanceToDest1: Int,
        dest1: Int,
        distanceToDest2: Int,
        dest2: Int,
    ): List<Pair<Int, Int>> =
        when {
            distanceToDest1 != 0 -> this.map { dest1 to it }
            distanceToDest2 != 0 -> this.map { it to dest2 }
            else -> generateAllPairs(this).toList()
        }

    private fun generateAllPairs(valves: List<Int>): Set<Pair<Int, Int>> {
        val pairs = mutableSetOf<Pair<Int, Int>>()
        for (valve in valves) {
            for (valve2 in valves) {
                if (valve != valve2) {
                    pairs.add(valve to valve2)
                }
            }
        }
        return pairs
    }

//    private fun IntArray.calculateMaxPressure(
//        openValves: Set<Int>,
//    ): List<Int> = pipeKeys.indices
//            .filter { it !in openValves }
//            .sortedByDescending {index ->
//                val remaining = time - this[index] - 1
//                flowRate[pipeKeys[index]]!!.toLong() * remaining
//            }.take(3)

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

private fun Array<IntArray>.printMatrix() = this.map { it.joinToString(" ") { it.toString() } }.joinToString("\n") { it }

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
