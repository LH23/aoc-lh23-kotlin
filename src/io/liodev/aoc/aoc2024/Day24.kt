package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.times
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

// --- 2024 Day 24: Crossed Wires ---
class Day24(
    val input: String,
) : Day<String> {
    override val expectedValues =
        listOf("9", "59364044286798", "z00,z01,z02,z05", "cbj,cfk,dmn,gmt,qjj,z07,z18,z35")

    private val inputs =
        input
            .split("\n\n")[0]
            .lines()
            .map { it.substringBefore(": ") to (it.substringAfter(": ").toInt()) }
            .associate { it }
    private val gates =
        input
            .split("\n\n")[1]
            .lines()
            .associate { line ->
                line.split(" -> ").let {
                    val (a, op, b) = it[0].split(" ")
                    it[1] to Gate(a, b, gateOperation(op), it[1])
                }
            }

    data class Gate(
        val inputWireA: String,
        val inputWireB: String,
        val execute: (Int, Int) -> Int,
        val resultWire: String,
    )

    private fun gateOperation(s: String): (Int, Int) -> Int =
        when (s) {
            "AND" -> { a, b -> a and b }
            "OR" -> { a, b -> a or b }
            "XOR" -> { a, b -> a xor b }
            else -> throw IllegalArgumentException("Unknown gate operation $s")
        }

    override fun solvePart1(): String = runCircuit(inputs, gates).toString()

    override fun solvePart2(): String =
        fixCircuit(gates)
            .flatMap { listOf(it.first, it.second) }
            .sorted()
            .joinToString(",")

    private fun fixCircuit(gates: Map<String, Gate>): Set<Pair<String, String>> {
        val needToSwap = if (testInput()) 2 else 4
        val checkZn = if (testInput()) ::checkZnAnd else ::checkZnAddition
        val test = if (testInput()) { x: Long, y: Long -> x and y } else { x, y -> x + y }
        val maxBits = if (testInput()) 5 else 44

        val swappedGates =
            runBlocking {
                swapGatesRecursive(gates, setOf(), needToSwap, 0..maxBits, checkZn, test, maxBits)!!
            }
        return swappedGates
    }

    private fun testInput(): Boolean = gates.keys.first() == "z05"

    private fun addInputs(
        wire: String,
        gates: Map<String, Gate>,
    ): Set<String> =
        if (!gates.contains(wire)) {
            emptySet()
        } else {
            listOf(
                gates[wire]!!.inputWireA,
                gates[wire]!!.inputWireB,
            ).filter { !it.startsWith("x") && !it.startsWith("y") }.toSet() +
                addInputs(gates[wire]!!.inputWireA, gates) +
                addInputs(gates[wire]!!.inputWireB, gates)
        }

    private suspend fun swapGatesRecursive(
        swappedGates: Map<String, Gate>,
        swapped: Set<Pair<String, String>>,
        needToSwap: Int,
        fromTo: IntRange,
        checkZn: (Map<String, Gate>, Int) -> Boolean,
        test: (Long, Long) -> Long,
        maxBits: Int,
    ): Set<Pair<String, String>>? {
        if (needToSwap == 0) {
            if (passTestCases(swappedGates, test, maxBits)) {
                return swapped
            }
        } else {
            val zFailedList = fromTo.filter { zn -> !checkZn(swappedGates, zn) }

            val notBroken =
                buildSet {
                    (0..maxBits).filter { it !in zFailedList }.forEach {
                        add("z${it.pad2()}")
                    }
                }
            for (zFailed in zFailedList) {
                val brokenZ = "z${zFailed.pad2()}"

                val alreadyVisited = createAlreadyVisited(zFailed)
                val alreadySwapped = swapped.flatMap { listOf(it.first, it.second) }.toSet()

                val swapOptions = swappedGates.keys - alreadyVisited - alreadySwapped - brokenZ - notBroken

                val candidates =
                    (setOf(brokenZ) + addInputs(brokenZ, gates) - alreadyVisited)
                        .times(swapOptions)
                        .distinctBy { setOf(it.first, it.second) }
                        .filter { newSwap ->
                            checkZn(swappedGates.swap(newSwap), zFailed)
                        }

                val swapsResult =
                    coroutineScope {
                        val deferredResults =
                            candidates.map { candidate ->
                                async(Dispatchers.Default) {
                                    val fixSwaps =
                                        swapGatesRecursive(
                                            swappedGates.swap(candidate),
                                            swapped + candidate,
                                            needToSwap - 1,
                                            zFailed..fromTo.last,
                                            checkZn,
                                            test,
                                            maxBits,
                                        )
                                    return@async fixSwaps
                                }
                            }
                        deferredResults.awaitAll()
                    }.filterNotNull().firstOrNull()

                return swapsResult
            }
        }
        return null
    }

    private val alreadyVisitedCache = mutableMapOf<Int, Set<String>>()

    private fun createAlreadyVisited(zFailed: Int) =
        alreadyVisitedCache.getOrPut(zFailed) {
            buildSet {
                (zFailed - 1 downTo 0).forEach {
                    addAll(addInputs("z${it.pad2()}", gates))
                }
            }
        }

    private fun passTestCases(
        swappedGates: Map<String, Gate>,
        test: (Long, Long) -> Long,
        maxBits: Int,
    ): Boolean {
        val xs = inputs.keys.filter { it.startsWith("x") }.sortedDescending()
        val ys = inputs.keys.filter { it.startsWith("y") }.sortedDescending()
        val expected = test(xs.binToLong(inputs), ys.binToLong(inputs))

        val result = runCircuit(inputs, swappedGates)

        if (expected == result) {
            val randomChecksPassed =
                (0..100).map { Random.nextLong(2L shl maxBits) }.zipWithNext().all { (n, m) ->
                    val newInputs = createInputs(n, m)
                    test(n, m) == runCircuit(newInputs, swappedGates)
                }

            if (randomChecksPassed) {
                // println("REALLY FOUND (with high probability)!!!! $swapped")
                return true
            }
        }
        return false
    }

    private fun checkZnAnd(
        gates: Map<String, Gate>,
        zn: Int,
    ): Boolean {
        val num = 1L shl zn
        val newInputs = createInputs(num, ("1".repeat(44)).toLong(radix = 2))
        val result = runCircuit(newInputs, gates, zn + 1)
        return num == result
    }

    private fun checkZnAddition(
        gates: Map<String, Gate>,
        zn: Int,
    ): Boolean {
        val num = 1L shl zn
        val newInputs = createInputs(num, 0)
        val result = runCircuit(newInputs, gates, zn + 1)
        return num == result
    }

    private fun createInputs(
        x: Long,
        y: Long,
    ) = buildMap {
        x.toString(2).padStart(45, '0').reversed().forEachIndexed { i, b ->
            put("x${i.pad2()}", if (b == '1') 1 else 0)
        }
        y.toString(2).padStart(45, '0').reversed().forEachIndexed { i, b ->
            put("y${i.pad2()}", if (b == '1') 1 else 0)
        }
    }

    private fun runCircuit(
        inputs: Map<String, Int>,
        gates: Map<String, Gate>,
        takeLeastSignificant: Int = 45,
    ): Long {
        val outputs = mutableMapOf<String, Int>()
        outputs.putAll(inputs)
        gates.keys.forEach { outputs[it] = -1 }

        val zs =
            gates.keys
                .filter { it.startsWith("z") }
                .sortedDescending()
                .takeLast(takeLeastSignificant + 1)

        var changes: Boolean
        val remaining = HashSet<String>()
        remaining.addAll(zs)
        while (remaining.isNotEmpty()) {
            changes = false
            for (wire in remaining.toList()) {
                val gate = gates[wire]!!

                val missingInputs =
                    listOf(gate.inputWireA, gate.inputWireB)
                        .filter { outputs.getOrDefault(it, -1) == -1 }
                if (missingInputs.isNotEmpty()) {
                    remaining.addAll(missingInputs)
                    continue
                }

                val a = outputs[gate.inputWireA]!!
                val b = outputs[gate.inputWireB]!!
                outputs[gate.resultWire] = gate.execute(a, b)
                remaining.remove(gate.resultWire)
                changes = true
            }
            if (!changes) {
                break
            }
        }

        return if (zs.count { outputs[it] == -1 } == 0) {
            zs.binToLong(outputs)
        } else {
            -1
        }
    }

    private fun Map<String, Gate>.swap(pair: Pair<String, String>): Map<String, Gate> {
        val a = this[pair.first]!!.copy(resultWire = pair.second)
        val b = this[pair.second]!!.copy(resultWire = pair.first)
        return this.filter { it.key != pair.first && it.key != pair.second } +
            mapOf(Pair(pair.first, b), Pair(pair.second, a))
    }
}

private fun Int.pad2(): String = this.toString().padStart(2, '0')

private fun List<String>.binToLong(outputs: Map<String, Int>): Long =
    this
        .joinToString("") { outputs[it].toString() }
        .toLong(2)

fun main() {
    val name = Day24::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day24(testInput), Day24(realInput), year, printTimings = true, benchmark = false)
}
