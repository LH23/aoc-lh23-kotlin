package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2022 Day 8: Treetop Tree House ---
class Day08(input: String) : Day<Int> {
    override val expectedValues = listOf(21, 1779, 8, 172224)

    private val treesHeightMap = input.split("\n")
        .map { row -> row.map { it - '0' } }

    override fun solvePart1(): Int {
        val a = treesHeightMap
        val n = a.size
        val nli = a.lastIndex
        val m = a[0].size
        val mli = a[0].lastIndex

        val visibleTrees = List(n) { i ->
            MutableList(m) { j -> i == 0 || i == nli || j == 0 || j == mli }
        }

        var maxH: Int
        for (i in 1..<nli) {
            maxH = a[i][0]
            for (j in 1..<mli) maxH = visibleTrees.maxVisible(i, j, maxH)
            maxH = a[i][mli]
            for (j in (1..<mli).reversed()) maxH = visibleTrees.maxVisible(i, j, maxH)
        }
        for (j in 1..<mli) {
            maxH = a[0][j]
            for (i in 1..<nli) maxH = visibleTrees.maxVisible(i, j, maxH)
            maxH = a[nli][j]
            for (i in (1..<nli).reversed()) maxH = visibleTrees.maxVisible(i, j, maxH)
        }
        return visibleTrees.sumOf { it.count { isVisible -> isVisible } }
    }

    private fun List<MutableList<Boolean>>.maxVisible(i: Int, j: Int, maxH: Int): Int {
        if (treesHeightMap[i][j] > maxH) {
            this[i][j] = true
            return treesHeightMap[i][j]
        }
        return maxH
    }

    override fun solvePart2(): Int {
        var maxScenicScore = 0
        for (i in 1..<treesHeightMap.lastIndex) {
            for (j in 1..<treesHeightMap[0].lastIndex) {
                maxScenicScore = maxScenicScore.coerceAtLeast(scenicScore(i, j))
            }
        }
        return maxScenicScore
    }

    private fun scenicScore(i: Int, j: Int): Int {
        val a = treesHeightMap
        val nli = a.lastIndex
        val mli = a[0].lastIndex
        return ((i - 1 downTo 1).takeWhile { a[it][j] < a[i][j] }.count() + 1) *
                ((i + 1..<nli).takeWhile { a[it][j] < a[i][j] }.count() + 1) *
                ((j - 1 downTo 1).takeWhile { a[i][it] < a[i][j] }.count() + 1) *
                ((j + 1..<mli).takeWhile { a[i][it] < a[i][j] }.count() + 1)
    }
}

fun main() {
    val name = Day08::class.simpleName
    val testInput = readInputAsString("src/input/2022/${name}_test.txt")
    val realInput = readInputAsString("src/input/2022/${name}.txt")
    runDay(Day08(testInput), Day08(realInput))
}