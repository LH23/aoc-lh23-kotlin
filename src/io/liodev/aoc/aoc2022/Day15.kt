package io.liodev.aoc.aoc2022

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import kotlin.math.abs

// --- Day 15 2022: Beacon Exclusion Zone ---
class Day15(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(26L, 4951427, 56000011, 13029714573243)

    private val sensors = input.split("\n").map { SensorData.from(it) }

    override fun solvePart1() =
        getSensorRanges(if (sensors[0].sensorPos.r == 18) 10 else 2000000)
            .sumOf { it.last - it.first }
            .toLong()

    override fun solvePart2(): Long {
        val rangeToCheck = if (sensors[0].sensorPos.r == 18) 0..20 else 0..4000000

        for (i in rangeToCheck) {
            val sensorRanges = getSensorRanges(i)
            if (sensorRanges.size > 1 && sensorRanges[0].last in rangeToCheck) {
                val distressBeacon = Coord(i, sensorRanges[0].last + 1)
                return distressBeacon.c.toLong() * 4000000 + distressBeacon.r
            }
        }
        return -1
    }

    private fun getSensorRanges(rowToCheck: Int): List<IntRange> {
        val sensorRanges = mutableListOf<IntRange>()
        for (sensor in sensors) {
            val sr = sensor.sensorPos.r
            val sc = sensor.sensorPos.c
            val distanceToRow = abs(sr - rowToCheck)
            if (distanceToRow <= sensor.distanceToBeacon) {
                val reminder = sensor.distanceToBeacon - distanceToRow
                sensorRanges.add(sc - reminder..sc + reminder)
            }
        }
        return sensorRanges.simplify()
    }

    data class SensorData(
        val sensorPos: Coord,
        val closestBeacon: Coord,
    ) {
        val distanceToBeacon: Int = manhattanDistance(sensorPos, closestBeacon)

        private fun manhattanDistance(
            a: Coord,
            b: Coord,
        ): Int = abs(a.r - b.r) + abs(a.c - b.c)

        companion object {
            fun from(sensorString: String): SensorData {
                val (s, b) = sensorString.split(':')
                val sensor =
                    Coord(
                        s.substringAfter("y=").toInt(),
                        s.substringAfter("x=").substringBefore(",").toInt(),
                    )
                val beacon =
                    Coord(
                        b.substringAfter("y=").toInt(),
                        b.substringAfter("x=").substringBefore(",").toInt(),
                    )
                return SensorData(sensor, beacon)
            }
        }
    }
}

private fun List<IntRange>.simplify(): List<IntRange> =
    this.sortedBy { it.first }.fold(listOf()) { acc, range ->
        when {
            acc.isEmpty() || acc.last().last < range.first -> acc + listOf(range)
            else -> acc.dropLast(1) + listOf(acc.last().first..maxOf(acc.last().last, range.last))
        }
    }

fun main() {
    val name = Day15::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day15(testInput), Day15(realInput), year, printTimings = true)
}
