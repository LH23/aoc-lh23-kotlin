package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import java.lang.Error

// --- 2022 Day 7: No Space Left On Device ---
class Day07(input: String) : Day<Long> {
    override val expectedValues = listOf(95437L, 1447046, 24933642, 578710)

    private val consoleLines = input
        .split('\n')
        .filterNot { it.isLs() || it.isDir() }

    private fun createDirTree(consoleLines: List<String>): Map<String, Long> {
        val dirTree = DirTree()
        for (line in consoleLines) {
            when {
                line.isCdUp() -> dirTree.goUp()
                line.isCd() -> dirTree.cdDir(line.removePrefix("$ cd "))
                line.isFile() -> dirTree.addFileSize(line.split(' ')[0].toLong())
                else -> error("Invalid command $line")
            }
        }
        dirTree.acumSubdirSizes()
        return dirTree.dirs
    }

    class DirTree(
        private var currentPath: String = "",
        val dirs: MutableMap<String, Long> = mutableMapOf()
    ) {
        fun goUp() {
            currentPath = currentPath.substring(0, currentPath.lastIndexOf('/'))
        }

        fun cdDir(dir: String) {
            currentPath = "$currentPath/$dir".replace("//", "/")
            dirs[currentPath] = 0
        }

        fun addFileSize(size: Long) {
            dirs[currentPath] = dirs[currentPath]!! + size
        }

        fun acumSubdirSizes() {
            dirs.keys.sortedByDescending { it.length }.forEach { path ->
                dirs[path] = dirs[path]!! + getSubdirSize(dirs, path)
            }
        }

        private fun getSubdirSize(dirs: MutableMap<String, Long>, path: String): Long =
            dirs.keys.filter { path.isSubdir(it) }.sumOf { dirs[it]!!.toLong() }

    }

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

private fun String.isLs() = this == "$ ls"
private fun String.isDir() = startsWith("dir ")
private fun String.isCd() = startsWith("$ cd ")
private fun String.isCdUp() = startsWith("$ cd ..")
private fun String.isFile() = split(' ')[0].all { it.isDigit() }

private fun String.isSubdir(other: String) =
    (this == "/" && other != "/" && other.slashes() == 1) ||
        (other.startsWith("$this/") && (other.slashes() == this.slashes() + 1))

private fun String.slashes(): Int = count { it == '/' }

fun main() {
    val name = Day07::class.simpleName
    val testInput = readInputAsString("src/input/2022/${name}_test.txt")
    val realInput = readInputAsString("src/input/2022/${name}.txt")
    runDay(Day07(testInput), Day07(realInput))
}