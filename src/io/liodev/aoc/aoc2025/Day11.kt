package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2025 Day 11: Reactor ---
class Day11(
    val input: String,
) : Day<Long> {
    override val expectedValues = listOf(2L, 652, 4, 362956369749210)

    private val serverGraph = input.split("\n").associate {
        val device = it.substringBefore(":")
        val connections = it.substringAfter(": ").split(" ")
        device to connections
    }

    override fun solvePart1(): Long = countPathsWithCache("you", "out")

    override fun solvePart2(): Long = countPathsWithCache("svr", "out", requiredElements = setOf("fft", "dac"))

    data class CacheKey(val start: String, val end: String, val requiredElements: Set<String>)
    var cache: MutableMap<CacheKey, Long> = mutableMapOf()
    
    fun countPathsWithCache(start: String, end: String, requiredElements: Set<String> = setOf()): Long {
        cache = mutableMapOf()
        return countPathsCacheRec(start, end, requiredElements)
    }

    // SLOW AND BAD!
    fun countPathsPartially(): Long {
        // visualizing the graph :D
        val svr = "svr"
        val section1 = setOf("akw", "gcv", "hng")
        val fft = "fft"
        val section2 = setOf("opq", "njx", "fxz", "kmc", "amt")
        val section3 = setOf("ibk", "vic", "tyl", "zle", "hnh")
        val section4 = setOf("hud", "mmm", "xba", "ekg", "fkd")
        val dac = "dac"
        val section5 = setOf("you", "vkz", "iqd", "poa", "xlo")
        val out = "out"

        val pathsSvrFft = countPathsBfs(svr, fft, section2)
        val pathsFftDac = section3.sumOf {
            val paths1 = countPathsBfs( fft, it, section4)
            val paths2 = countPathsBfs( it, dac, section5)
            paths1 * paths2
        }
        val pathsDacOut = countPathsBfs(dac, out, setOf())
        
        return pathsSvrFft * pathsFftDac * pathsDacOut
    }
    
    fun countPathsBfs(start: String, final: String, excludeSet: Set<String> = setOf()): Long {
        val queue = ArrayDeque<List<String>>()
        queue.add(listOf(start))
        var paths = 0L
        while (queue.isNotEmpty()) {
            val path = queue.removeFirst()
            val current = path.last()
            
            if (current in excludeSet) {
                continue
            }
            if (current == final) {
                paths++
                continue
            }
            for (neighbor in serverGraph[current] ?: emptyList()) {
                if (neighbor !in path) {
                    queue.add(path + neighbor)
                }
            }
        }
        return paths
    }

    fun countPathsCacheRec(current: String, final: String, requiredElements: Set<String>): Long {
        val required = requiredElements - current
        val key = CacheKey(current, final, required)
        cache[key]?.let { return it }

        if (current == final) {
            return if (required.isEmpty()) 1L else 0L
        }
    
        val result = serverGraph[current]?.sumOf { neighbor ->
            countPathsCacheRec(neighbor, final, required)
        }?: 0L
        cache[key] = result
        return result
    }
}

fun convertToDot(
    input: String,
    isDirected: Boolean = true,
    highlightNodes: List<String> = emptyList()
): String {
    val sb = StringBuilder()

    val graphType = if (isDirected) "digraph" else "graph"
    val edgeSymbol = if (isDirected) "->" else "--"

    sb.appendLine("$graphType G {")
    sb.appendLine("  node [shape=box style=filled fillcolor=\"#dddddd\"];")
    sb.appendLine("  overlap=false;")
    sb.appendLine("  splines=true;")
    sb.appendLine("")

    if (highlightNodes.isNotEmpty()) {
        sb.appendLine("  // Highlighted Nodes")
        for (node in highlightNodes) {
            sb.appendLine("  $node [style=\"filled,bold\" fillcolor=\"#ff4444\" color=\"#880000\" penwidth=3 fontsize=20 fontcolor=white];")
        }
        sb.appendLine("")
    }

    sb.appendLine("  // Edges")
    input.lineSequence().forEach { line ->
        if (line.isNotBlank()) {
            val parts = line.split(":")
            val source = parts[0].trim()
            if (parts.size > 1) {
                val destinations = parts[1].trim().split(Regex("\\s+"))
                for (dest in destinations) {
                    if (dest.isNotBlank()) {
                        sb.appendLine("  $source $edgeSymbol $dest;")
                    }
                }
            }
        }
    }
    sb.appendLine("}")
    return sb.toString()
}

fun main() {
    val name = Day11::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day11(testInput), Day11(realInput), year, printTimings = true)
}
