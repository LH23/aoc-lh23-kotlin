package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// 2023 Day 05
class Day05(input: String) : Day<Long> {                          //46
    override val expectedValues = listOf(35L, 289863851, 46, 60568880)

    private val almanac = input.toSeedAlmanac()
    private val almanacWithRanges = input.toSeedAlmanacWithRanges()

    data class SeedAlmanac(val seedsRange: List<LongRange>, val mappers: List<Mapper>) {
        fun calculateMinLocation(): Long {
            var min = Long.MAX_VALUE
            seedsRange.forEach { seedRange ->
                for (seed in seedRange) {
                    var result = seed
                    for (mapper in mappers) {
                        result = mapper.pass(result)
                    }
                    min = min.coerceAtMost(result)
                }
            }
            return min
        }
        fun calculateMinLocationReversed(): Long { // still slow but 20x better that the other
            return (1..Long.MAX_VALUE).first {
                var result = it
                for (mapper in mappers.reversed()) {
                    result = mapper.reverse(result)
                }
                seedsRange.any { seedRange -> result in seedRange}
            }
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
//        fun passRange(seedRange: LongRange): List<LongRange> {
//            for (range in ranges) {
//                if (seed in range.source) return range.destination.first + seed - range.source.first
//            }
//            return listOf(seedRange)
//        }
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
            .map { it.toLong() }
            .map { it..it + 1 }

    private fun String.parseSeedsAsRanges(): List<LongRange> =
        substringAfter("seeds: ")
            .split(" ")
            .map { it.toLong() }
            .chunked(2)
            .map { (it[0]..it[0] + it[1]) }

    private fun String.parseMappers(): List<Mapper> =
        split("\n\n").map { mapperStr ->
            Mapper(mapperStr.split("\n").drop(1).map { it.toMapperRange()})
        }

    private fun String.toMapperRange(): MapperRange {
        val range = this.split(" ").map { it.toLong() }
        return MapperRange(range[1]..<range[1] + range[2], range[0]..<range[0] + range[2])
    }

    override fun solvePart1() = almanac.calculateMinLocation()

    override fun solvePart2() = almanacWithRanges.calculateMinLocationReversed()
}

fun main() {
    val name = Day05::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day05(testInput), Day05(realInput))
}