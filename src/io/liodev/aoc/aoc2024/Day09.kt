package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 9: Disk Fragmenter ---
class Day09(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(1928L, 6259790630969, 2858, 6289564433984)

    private val diskMap =
        buildList {
            input.mapIndexed { i, s ->
                val size = s - '0'
                if (i % 2 == 0) {
                    add(MemCell.Data(i / 2, size))
                } else {
                    add(MemCell.Space(size))
                }
            }
        }

    sealed class MemCell {
        data class Data(
            val index: Int,
            val size: Int,
        ) : MemCell()

        data class Space(
            val size: Int,
        ) : MemCell()
    }

    override fun solvePart1(): Long {
        val defragmented = mutableListOf<MemCell>()
        var i = 0
        var j = diskMap.lastIndex
        var copyFrom: MemCell.Data = MemCell.Data(-1, 0)
        while (i < j) {
            when (val cell = diskMap[i]) {
                is MemCell.Data -> defragmented.add(cell)
                is MemCell.Space -> {
                    var freeSpace = cell.size
                    while (freeSpace > 0) {
                        if (copyFrom.size == 0) {
                            if (diskMap[j] is MemCell.Space) {
                                j--
                                continue
                            }
                            copyFrom = diskMap[j] as MemCell.Data
                        } else {
                            if (copyFrom.size <= freeSpace) {
                                defragmented.add(copyFrom)
                                freeSpace -= copyFrom.size
                                copyFrom = copyFrom.copy(size = 0)
                                j--
                            } else {
                                defragmented.add(copyFrom.copy(size = freeSpace))
                                copyFrom = copyFrom.copy(size = copyFrom.size - freeSpace)
                                freeSpace = 0
                            }
                        }
                    }
                }
            }
            i++
        }
        if (copyFrom.size > 0) defragmented.add(copyFrom)

        // checkMemSize(defragmented)
        return calculateChecksum(defragmented)
    }

    override fun solvePart2(): Long {
        val defragmented = diskMap.toMutableList()
        for (j in diskMap.lastIndex downTo 1) {
            when (val cell = defragmented[j]) {
                is MemCell.Space -> continue
                is MemCell.Data -> {
                    var i = 1
                    while (i < j) {
                        when (val spaceCell = defragmented[i]) {
                            is MemCell.Data -> i++
                            is MemCell.Space -> {
                                if (spaceCell.size >= cell.size) {
                                    defragmented.removeAt(i)
                                    defragmented.add(i, cell)
                                    defragmented.removeAt(j)
                                    defragmented.add(j, MemCell.Space(cell.size))
                                    if (spaceCell.size - cell.size > 0) {
                                        defragmented.add(
                                            i + 1,
                                            MemCell.Space(spaceCell.size - cell.size),
                                        )
                                    }
                                    break
                                }
                                i++
                            }
                        }
                    }
                }
            }
        }
        // checkMemSize(defragmented)
        return calculateChecksum(defragmented)
    }

    private fun checkMemSize(defragmented: List<MemCell>) {
        val totalMem = diskMap.sumOf { if (it is MemCell.Data) it.size.toLong() else 0L }
        val fileDefragMem = defragmented.sumOf { if (it is MemCell.Data) it.size.toLong() else 0L }
        check(totalMem == fileDefragMem)
    }

    private fun calculateChecksum(cells: List<MemCell>): Long {
        var globalIndex = 0
        return cells.sumOf {
            if (it is MemCell.Space) {
                globalIndex += it.size
                return@sumOf 0L
            }
            val data = it as MemCell.Data
            var sum = 0L
            repeat(data.size) {
                sum += data.index * globalIndex
                globalIndex++
            }
            sum
        }
    }

    private fun printMem(cells: List<MemCell>) {
        for (m in cells) {
            if (m is MemCell.Data) print("${m.index},".repeat(m.size))
            if (m is MemCell.Space) print(".".repeat(m.size))
        }
        println()
    }
}

fun main() {
    val name = Day09::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day09(testInput), Day09(realInput), year)
}
