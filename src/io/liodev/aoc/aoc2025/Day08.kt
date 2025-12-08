package io.liodev.aoc.aoc2025

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord3D

// --- 2025 Day 8: Playground ---
class Day08(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(40L, 46398, 25272, 8141888143)

    private val junctionBoxes = input.split("\n").map {
        it.split(',')
            .let { (x, y, z) ->
                Coord3D(x.toInt(), y.toInt(), z.toInt())
            }
    }

    override fun solvePart1(): Long {
        val circuits = mutableListOf<MutableSet<Coord3D>>()
        val distances = mutableMapOf<Set<Coord3D>, Double>()
        for (box in junctionBoxes) {
            for (otherBox in junctionBoxes) {
                if (box != otherBox && !distances.containsKey(setOf(box, otherBox))) {
                    val distance = box.euclideanDistance(otherBox)
                    distances[setOf(box,otherBox)] = distance
                }
            }
        }
        val sortedDistances = distances.toList().sortedBy { (_, value) -> value }.map { it.first }
        val numConnections = if (isTestInput()) 10 else 1000
        repeat(numConnections) { i ->
            val pairCoords = sortedDistances[i]
            val (coordA, coordB) = pairCoords.toList()
            val circuitA = circuits.singleOrNull { circuit ->
                circuit.contains(coordA)
            }
            val circuitB = circuits.singleOrNull { circuit ->
                circuit.contains(coordB)
            }
            if (circuitA == null && circuitB == null) {
                circuits.add(pairCoords.toMutableSet())
            } else if (circuitA != null && circuitB != null && circuitA != circuitB) {
                val removed = circuits.remove(circuitB)
                assert(removed)
                circuitA.addAll(circuitB)
            } else {
                circuitA?.addAll(pairCoords)
                circuitB?.addAll(pairCoords)
            }
        }
        return circuits.sortedByDescending{ it.size }.take(3).fold(1) { acc, set -> acc * set.size }
    }

    override fun solvePart2(): Long {
        val circuits = mutableListOf<MutableSet<Coord3D>>()
        val distances = mutableMapOf<Set<Coord3D>, Double>()
        for (box in junctionBoxes) {
            for (otherBox in junctionBoxes) {
                if (box != otherBox && !distances.containsKey(setOf(box, otherBox))) {
                    val distance = box.euclideanDistance(otherBox)
                    distances[setOf(box,otherBox)] = distance
                }
            }
        }
        val sortedDistances = distances.toList().sortedBy { (_, value) -> value }.map { it.first }
        var pairCoords = sortedDistances[0]
        var i = -1
        while(circuits.firstOrNull()?.size != junctionBoxes.size) {
            i++
            pairCoords = sortedDistances[i]
            val (coordA, coordB) = pairCoords.toList()
            val circuitA = circuits.singleOrNull { circuit ->
                circuit.contains(coordA)
            }
            val circuitB = circuits.singleOrNull { circuit ->
                circuit.contains(coordB)
            }
            if (circuitA == null && circuitB == null) {
                circuits.add(pairCoords.toMutableSet())
            } else if (circuitA != null && circuitB != null && circuitA != circuitB) {
                val removed = circuits.remove(circuitB)
                assert(removed)
                circuitA.addAll(circuitB)
            } else {
                circuitA?.addAll(pairCoords)
                circuitB?.addAll(pairCoords)
            }
        }
        return pairCoords.toList().let { (a, b) -> a.x.toLong() * b.x}
    }
    
    fun isTestInput(): Boolean {
        return junctionBoxes.size <= 20
    }
}

fun main() {
    val name = Day08::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day08(testInput), Day08(realInput), year, printTimings = true)
}
