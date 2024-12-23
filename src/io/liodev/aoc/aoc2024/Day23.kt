package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 23: LAN Party ---
class Day23(
    input: String,
) : Day<String> {
    override val expectedValues = listOf("7", "1368", "co,de,ka,ta", "dd,ig,il,im,kb,kr,pe,ti,tv,vr,we,xu,zi")

    private val networkPairs = input.lines().map { line -> line.split("-").let { it[0] to it[1] } }
    private val networkGraph =
        buildMap<String, MutableSet<String>> {
            networkPairs.forEach { (a, b) ->
                getOrPut(a) { mutableSetOf() }.add(b)
                getOrPut(b) { mutableSetOf() }.add(a)
            }
        }

    override fun solvePart1(): String {
        val lan3 = mutableSetOf<Set<String>>()
        val tNodes = networkGraph.keys.filter { it.startsWith("t") }
        for (tNode in tNodes) {
            for (node in networkGraph[tNode]!!.filter { it != tNode }) {
                for (node2 in networkGraph[node]!!) {
                    if (tNode in networkGraph[node2]!!) {
                        lan3.add(setOf(tNode, node, node2))
                    }
                }
            }
        }
        return lan3.size.toString()
    }

    override fun solvePart2(): String = findBiggestConnectedSet().sorted().joinToString(",")

    private fun findBiggestConnectedSet(): Set<String> = networkGraph.largestClique()

}

private fun Map<String, Set<String>>.largestClique(): Set<String> {
    return this.runBronKerbosch(this.keys)
}

//algorithm BronKerbosch1(R, P, X) is
//    if P and X are both empty then
//        report R as a maximal clique
//    for each vertex v in P do
//        BronKerbosch1(R ⋃ {v}, P ⋂ N(v), X ⋂ N(v))
//        P := P \ {v}
//        X := X ⋃ {v}
fun Map<String, Set<String>>.runBronKerbosch(
    p: Set<String>,
    r: Set<String> = emptySet(),
    x: Set<String> = emptySet(),
    candidates: MutableSet<Set<String>> = mutableSetOf()
): Set<String> {
    if (p.isEmpty() && x.isEmpty() && r !in candidates) {
        if (r.size > (candidates.maxOfOrNull { it.size } ?: 0)) {
            candidates.add(r)
        }
    } else {
        val mostNeighbors = (p + x).maxByOrNull { this[it]!!.size }
        for (v in if (mostNeighbors != null) p.minus(this[mostNeighbors]!!) else p) {
            runBronKerbosch(p.intersect(this[v]!!), r + v, x.intersect(this[v]!!), candidates)
        }
    }
    return candidates.maxBy { it.size }
}

fun main() {
    val name = Day23::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day23(testInput), Day23(realInput), year, printTimings = true)
}
