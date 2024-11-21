package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2023 Day 5: If You Give A Seed A Fertilizer ---
class Day05(val input: String) : Day<Long> {
    override val expectedValues = listOf(35L, 289863851, 46, 60568880)

    private val almanac = input.toSeedAlmanac()
    private val almanacWithRanges = input.toSeedAlmanacWithRanges()

    data class SeedAlmanac(val seedsRange: List<LongRange>, val mappers: List<Mapper>) {
        fun calculateMinLocationSlow() = // MANY minutes in Part2
            seedsRange.minOf { seedRange ->
                var min = Long.MAX_VALUE
                for (seed in seedRange) {
                    var result = seed
                    for (mapper in mappers) {
                        result = mapper.pass(result)
                    }
                    min = min.coerceAtMost(result)
                }
                min
            }

        fun calculateMinLocationReversed(): Long = // 9 seconds in Part2 (but very bad for Part1)
            (1..Long.MAX_VALUE).first {
                var result = it
                for (mapper in mappers.reversed()) {
                    result = mapper.reverse(result)
                }
                seedsRange.any { seedRange -> result in seedRange }
            }

        fun calculateMinLocation() = // 3 milliseconds in Part2
            seedsRange.let { seedsRange ->
                var listOfRanges = seedsRange
                for (mapper in mappers) {
                    listOfRanges = listOfRanges.fold(listOf()) { acc, range ->
                        acc + mapper.passRange(range)
                    }
                }
                listOfRanges.minOf { it.first }
            }
    }

    data class Mapper(val ranges: List<MapperRange>) {
        fun pass(value: Long): Long {
            for (range in ranges) {
                if (value in range.source) return range.destination.first + value - range.source.first
            }
            return value
        }

        fun reverse(value: Long): Long {
            for (range in ranges) {
                if (value in range.destination) return range.source.first + value - range.destination.first
            }
            return value
        }

        fun passRange(range: LongRange): List<LongRange> {
            val convert = mutableListOf<LongRange>()
            val result = mutableListOf<LongRange>()
            for (mapperRange in ranges) {
                val y1 = maxOf(range.first, mapperRange.source.first)
                val y2 = minOf(range.last, mapperRange.source.last)
                if (y1 <= y2) {
                    val s1 = mapperRange.source.first
                    val d1 = mapperRange.destination.first
                    convert.add(y1..y2)
                    result.add(y1 - s1 + d1..y2 - s1 + d1)
                }
            }
            convert.sortBy { it.first }
            var cur = range.first
            for (convertRange in convert) {
                if (convertRange.first > cur) result.add(cur..<convertRange.first)
                cur = convertRange.last + 1
            }
            if (cur <= range.last - 1) result.add(cur..range.last)
            return result
        }
    }

    data class MapperRange(val source: LongRange, val destination: LongRange)

    private fun String.toSeedAlmanac(): SeedAlmanac {
        return SeedAlmanac(
            substringBefore("\n\n").parseSeedsAsSingleRange(),
            substringAfter("\n\n").parseMappers()
        )
    }

    private fun String.toSeedAlmanacWithRanges(): SeedAlmanac {
        return SeedAlmanac(
            substringBefore("\n\n").parseSeedsAsRanges(),
            substringAfter("\n\n").parseMappers()
        )
    }

    private fun String.parseSeedsAsSingleRange(): List<LongRange> =
        substringAfter("seeds: ")
            .split(" ")
            .map { it.toLong()..it.toLong() + 1 }

    private fun String.parseSeedsAsRanges(): List<LongRange> =
        substringAfter("seeds: ")
            .split(" ")
            .map { it.toLong() }
            .chunked(2)
            .map { (it[0]..it[0] + it[1]) }

    private fun String.parseMappers(): List<Mapper> =
        split("\n\n").map { mapperStr ->
            Mapper(mapperStr.split("\n").drop(1).map { it.toMapperRange() })
        }

    private fun String.toMapperRange(): MapperRange {
        val range = this.split(" ").map { it.toLong() }
        return MapperRange(range[1]..<range[1] + range[2], range[0]..<range[0] + range[2])
    }

    override fun solvePart1() = almanac.calculateMinLocationSlow()

    override fun solvePart2() = almanacWithRanges.calculateMinLocation()

}

fun main() {
    val name = Day05::class.simpleName
    val year = 2023
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/${name}.txt")
    runDay(Day05(testInput), Day05(realInput), year, printTimings = true)
}