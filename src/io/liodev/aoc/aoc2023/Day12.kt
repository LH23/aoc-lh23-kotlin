package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2023 Day 12: Hot Springs ---
class Day12(input: String) : Day<Long> {
    override val expectedValues = listOf(21L, 7173, 525152, 29826669191291)

// part1
//    ???.### 1,1,3 - 1 arrangement
//    .??..??...?##. 1,1,3 - 4 arrangements
//    ?#?#?#?#?#?#?#? 1,3,1,6 - 1 arrangement
//    ????.#...#... 4,1,1 - 1 arrangement
//    ????.######..#####. 1,6,5 - 4 arrangements
//    ?###???????? 3,2,1 - 10 arrangements

// part2
//  ???.### 1,1,3 - 1 arrangement
//  .??..??...?##. 1,1,3 - 16384 arrangements
//  ?#?#?#?#?#?#?#? 1,3,1,6 - 1 arrangement
//  ????.#...#... 4,1,1 - 16 arrangements
//  ????.######..#####. 1,6,5 - 2500 arrangements
//  ?###???????? 3,2,1 - 506250 arrangements

    private val records = input.split("\n").map { it.toConditionRecord() }
    private val recordsUnfolded = records.map {
        ConditionRecord(
            "${it.part}?${it.part}?${it.part}?${it.part}?${it.part}",
            it.sequence + it.sequence + it.sequence + it.sequence + it.sequence
        )
    }

    override fun solvePart1() = records.sumOf { record ->
        record.calculateOptions()
    }

    override fun solvePart2() = recordsUnfolded.sumOf { record ->
        record.calculateOptions()
    }
}

private fun String.toConditionRecord(): ConditionRecord {
    val (parts, seq) = this.split(" ")
    return ConditionRecord(parts, seq.split(",").map { it.toInt() })
}

data class ConditionRecord(val part: String, val sequence: List<Int>) {
    private val cache = mutableMapOf<Pair<Int, Int>, Long>()
    private val memo = List(part.length) { part.drop(it).takeWhile { c -> c != '.' }.length }

    fun calculateOptions() = validSequences(0, 0)

    private fun validSequences(partIndex: Int, sequenceIndex: Int): Long = when {
        sequenceIndex == sequence.size && part.drop(partIndex).none { it == '#' } -> 1
        sequenceIndex == sequence.size || partIndex >= part.length -> 0
        else -> {
            if (cache[partIndex to sequenceIndex] == null) {
                val usingBrokenPart = if (canTakeBrokens(partIndex, sequence[sequenceIndex]))
                    validSequences(partIndex + sequence[sequenceIndex] + 1, sequenceIndex + 1)
                else 0
                val usingWorkingPart = if (part[partIndex] != '#')
                    validSequences(partIndex + 1, sequenceIndex)
                else 0
                cache[partIndex to sequenceIndex] = usingBrokenPart + usingWorkingPart
                usingBrokenPart + usingWorkingPart
            } else {
                cache[partIndex to sequenceIndex]!!
            }
        }
    }

    private fun canTakeBrokens(index: Int, numBrokens: Int) =
        memo[index] >= numBrokens && (index + numBrokens == part.length || part[index + numBrokens] != '#')

}

fun main() {
    val name = Day12::class.simpleName
    val year = 2023
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/${name}.txt")
    runDay(Day12(testInput), Day12(realInput), year, printTimings = true)
}