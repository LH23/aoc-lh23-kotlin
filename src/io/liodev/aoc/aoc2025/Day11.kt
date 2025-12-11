package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2025 Day 11: Reactor ---
class Day11(
    val input: String,
) : Day<Long> {
    override val expectedValues = listOf(0L, 652, 0, 362956369749210)

    private val serverGraph = input.split("\n").associate {
        val device = it.substringBefore(":")
        val connections = it.substringAfter(": ").split(" ")
        device to connections
    }

    override fun solvePart1(): Long {
        val start = "you"
        val end = "out"
        val paths = bfs(start, end, setOf())
        return paths.size.toLong()
    }

    override fun solvePart2(): Long {
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

        val pathsSvrFft = bfs(svr, fft, section2).size
        val pathsFftDac = section3.sumOf {
            val paths1 = bfs( fft, it, section4)
            val paths2 = bfs( it, dac, section5 + "hud") // we hate the hud
            paths1.size * paths2.size
        }
        val pathsDacOut = bfs(dac, out, setOf()).size
        
        return pathsSvrFft.toLong() * pathsFftDac * pathsDacOut
    }

    fun bfs(start: String, final: String, excludeSet: Set<String>): List<List<String>> {
        val result = mutableListOf<List<String>>()
        val queue = ArrayDeque<List<String>>()
        queue.add(listOf(start))

        while (queue.isNotEmpty()) {
            val path = queue.removeFirst()
            val current = path.last()
            
            if (current in excludeSet) {
                continue
            }
            if (current == final) {
                result.add(path)
            }
            for (neighbor in serverGraph[current] ?: emptyList()) {
                if (neighbor !in path) {
                    queue.add(path + neighbor)
                }
            }
        }
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
    runDay(Day11(testInput), Day11(realInput), year)
}
