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
                            var cellJ = diskMap[j]
                            while (cellJ !is MemCell.Data) {
                                cellJ = diskMap[j--]
                            }
                            copyFrom = cellJ
                        }
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
            i++
        }
        if (copyFrom.size > 0) defragmented.add(copyFrom)

        // checkMemSize(defragmented)
        return calculateChecksum(defragmented)
    }

    override fun solvePart2(): Long {
        val defragmented = diskMap.toMutableList()
        for (dataIndex in diskMap.lastIndex downTo 1) {
            if (defragmented[dataIndex] is MemCell.Data) {
                for (spaceIndex in 0..dataIndex) {
                    if (defragmented[spaceIndex] is MemCell.Space) {
                        val freeSpace = (defragmented[spaceIndex] as MemCell.Space).size
                        val dataSize = (defragmented[dataIndex] as MemCell.Data).size
                        if (freeSpace >= dataSize) {
                            defragmented.swap(spaceIndex, dataIndex)
                            break
                        }
                    }
                }
            }
        }
        // checkMemSize(defragmented)
        return calculateChecksum(defragmented)
    }

    private fun MutableList<MemCell>.swap(
        spaceIndex: Int,
        dataIndex: Int,
    ) {
        val spaceCell = get(spaceIndex) as MemCell.Space
        val dataCell = get(dataIndex) as MemCell.Data
        apply {
            removeAt(spaceIndex)
            add(spaceIndex, dataCell)
            removeAt(dataIndex)
            add(dataIndex, MemCell.Space(dataCell.size))
            if (spaceCell.size > dataCell.size) {
                add(spaceIndex + 1, MemCell.Space(spaceCell.size - dataCell.size))
            }
        }
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
    runDay(Day09(testInput), Day09(realInput), year, printTimings = true)
}
