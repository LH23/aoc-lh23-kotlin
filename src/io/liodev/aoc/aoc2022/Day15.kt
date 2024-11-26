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

    override fun solvePart1(): Long {
        val rowToCheck = if (sensors[0].sensorPos.r == 18) 10 else 2000000
        val sensorRanges = getSensorRanges(rowToCheck)

        var count = 0
        for (i in sensorRanges.minOf { it.first }..sensorRanges.maxOf { it.last }) {
            count += if (sensorRanges.any { range -> i in range }) 1 else 0
        }
        val beaconsInRow = sensors.map { it.closestBeacon }.toSet().filter { it.r == rowToCheck }
        //println("beacons in row $beaconsInRow")
        return count.toLong() - beaconsInRow.size
    }

    override fun solvePart2(): Long {
        val rangeToCheck = if (sensors[0].sensorPos.r == 18) 0..20 else 0..4000000

        for (i in rangeToCheck) {
            val sensorRanges = getSensorRanges(i)
            if (sensorRanges.size > 1 && sensorRanges[0].last in rangeToCheck) {
                //println("sensorRanges: $sensorRanges")
                val hiddenBeacon = Coord(i, sensorRanges[0].last + 1)
                return hiddenBeacon.c.toLong() * 4000000 + hiddenBeacon.r
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
        val distanceToBeacon: Int,
    ) {
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
                return SensorData(sensor, beacon, manhattanDistance(sensor, beacon))
            }

            private fun manhattanDistance(
                a: Coord,
                b: Coord,
            ): Int = abs(a.r - b.r) + abs(a.c - b.c)
        }
    }
}

private fun List<IntRange>.simplify(): List<IntRange> {
    val sorted = this.sortedBy { it.first }
    return sorted.fold(listOf()) { acc, range ->
        when {
            acc.isEmpty() -> {
                listOf(range)
            }
            acc.last().last >= range.first -> {
                acc.dropLast(1) + listOf(acc.last().first..maxOf(acc.last().last, range.last))
            }
            else -> {
                acc + listOf(range)
            }
        }
    }
}

fun main() {
    val name = Day15::class.simpleName
    val year = 2022
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day15(testInput), Day15(realInput), year)
}
