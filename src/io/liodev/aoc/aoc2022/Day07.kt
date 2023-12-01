package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2022 Day 7: No Space Left On Device ---
class Day07(input: String) : Day<Long> {
    // TEST P1 these directories are a and e; the sum of their total sizes is 95437 (94853 + 584)
    override val expectedValues = listOf(95437L, 1447046, 24933642, 578710) // 40572957

    private val consoleLines = input
        .split('\n')
        .filter { it != "$ ls" && !it.startsWith("dir ") }

    private fun createDirTree(consoleLines: List<String>): Map<String, Long> {
        var currentPath = ""
        val dirs = mutableMapOf<String, Long>()
        for (line in consoleLines) {
            when {
                line.startsWith("$ cd ..") -> {
                    currentPath = currentPath.substring(0, currentPath.lastIndexOf('/'))
                }

                line.startsWith("$ cd ") -> {
                    currentPath = "$currentPath/${line.removePrefix("$ cd ")}"
                        .replace("//", "/")
                    dirs[currentPath] = 0
                }

                else -> {
                    val size = line.split(' ')[0].toLong()
                    dirs[currentPath] = dirs[currentPath]!! + size
                }
            }
        }
        dirs.keys.sortedByDescending { it.length }.forEach { path ->
            dirs[path] = dirs[path]!! + getSubdirSize(dirs, path)
        }
        return dirs
    }

    private fun getSubdirSize(dirs: MutableMap<String, Long>, path: String): Long =
        dirs.keys.filter { path.isSubdir(it) }.sumOf { dirs[it]!!.toLong() }

    override fun solvePart1() = createDirTree(consoleLines)
        .filter { (_, size) -> size <= 100_000 }
        .values
        .sum()

    override fun solvePart2(): Long {
        val dirs = createDirTree(consoleLines)
        val requiredSpace = 30_000_000 - (70_000_000L - dirs["/"]!!)
        return dirs.values.sorted().first { it > requiredSpace }
    }

}

private fun String.isSubdir(other: String) =
    (this == "/" && other != "/" && other.count { it == '/' } == 1) ||
        (other.startsWith("$this/") && (other.count { it == '/' } == this.count { it == '/' } + 1))

fun main() {
    val name = Day07::class.simpleName
    val testInput = readInputAsString("src/input/2022/${name}_test.txt")
    val realInput = readInputAsString("src/input/2022/${name}.txt")
    runDay(Day07(testInput), Day07(realInput))
}