package io.liodev.aoc

import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads full content from the given input txt file.
 */
fun readInputAsString(filename: String): String {
    try {
        val path = Path(filename)
        return path.readText()
    } catch (e: IOException) {
        throw IllegalArgumentException("File $filename not found")
    }
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() =
    BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
        .toString(16)
        .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun checkResult(
    testName: String,
    actual: Any?,
    expected: Any?,
) {
    if (actual == expected) {
        println("✅ $testName Correct, $actual == $expected")
    } else {
        error("❌ $testName WRONG, $actual != $expected")
    }
}

/**
 * Converts the common AoC graph representation to DOT format for visualization
 * aaa: bbb ccc ddd
 * ...
 */
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

