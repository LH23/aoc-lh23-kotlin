package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2024 Day 17: Chronospatial Computer ---
class Day17(
    input: String,
) : Day<String> {
    override val expectedValues =
        listOf("5,7,3,0", "6,2,7,2,3,1,6,0,5", "117440", "236548287712877")

    private val initRegisters =
        input.split("\n\n")[0].lines().map { it.substringAfter(": ").toInt() }
    private val program =
        input
            .split("\n\n")[1]
            .substringAfter("Program: ")
            .split(",")
            .map { it.toInt() }

    override fun solvePart1(): String = runProgram(initRegisters.map { it.toLong() }).joinToString(",")

    override fun solvePart2(): String = findReverseRecursive("", program, 1)!!

    private fun runProgram(registers: List<Long>): List<Int> {
        val output = mutableListOf<Int>()
        var regA = registers[0]
        var regB = registers[1]
        var regC = registers[2]

        var halt = false
        var ip = 0
        while (!halt) {
            val results = processInstruction(program[ip] to program[ip + 1], regA, regB, regC)

            regA = results[0]
            regB = results[1]
            regC = results[2]
            if (results.size > 3 && results[3] != -1L) output.add(results[3].toInt())
            ip = if (results.size > 4) results[4].toInt() else ip + 2
            halt = ip >= program.size
        }
        return output
    }

    private fun findReverseRecursive(
        result: String,
        program: List<Int>,
        n: Int,
    ): String? {
        val bin3 = listOf("000", "001", "010", "011", "100", "101", "110", "111")
        for (o in bin3) {
            val newRegA = (result + o).toLong(2)
            val output =
                runProgram(listOf(newRegA, initRegisters[1].toLong(), initRegisters[2].toLong()))
            if (output == program) {
                return "$newRegA"
            } else if (output.takeLast(n) == program.takeLast(n)) {
                (findReverseRecursive(result + o, program, n + 1))?.let { return it }
            }
        }
        return null
    }

    private fun processInstruction(
        instruction: Pair<Int, Int>,
        regA: Long,
        regB: Long,
        regC: Long,
    ): List<Long> {
        val (opcode, value) = instruction
        return when (opcode) {
            0 -> adv(value.toLong(), regA, regB, regC)
            1 -> bxl(value.toLong(), regA, regB, regC)
            2 -> bst(value.toLong(), regA, regB, regC)
            3 -> jnz(value.toLong(), regA, regB, regC)
            4 -> bxc(regA, regB, regC)
            5 -> out(value.toLong(), regA, regB, regC)
            6 -> bdv(value.toLong(), regA, regB, regC)
            7 -> cdv(value.toLong(), regA, regB, regC)
            else -> throw IllegalArgumentException("Unknown opcode: $opcode")
        }
    }

    private fun adv(
        value: Long,
        regA: Long,
        regB: Long,
        regC: Long,
    ): List<Long> = listOf(regA shr comboOperand(value, regA, regB, regC).toInt(), regB, regC)

    private fun bxl(
        value: Long,
        regA: Long,
        regB: Long,
        regC: Long,
    ): List<Long> = listOf(regA, regB xor value, regC)

    private fun bst(
        value: Long,
        regA: Long,
        regB: Long,
        regC: Long,
    ): List<Long> = listOf(regA, comboOperand(value, regA, regB, regC) % 8, regC)

    private fun jnz(
        value: Long,
        regA: Long,
        regB: Long,
        regC: Long,
    ): List<Long> =
        if (regA == 0L) {
            listOf(regA, regB, regC)
        } else {
            listOf(regA, regB, regC, -1, value)
        }

    private fun bxc(
        regA: Long,
        regB: Long,
        regC: Long,
    ): List<Long> = listOf(regA, regB xor regC, regC)

    private fun out(
        value: Long,
        regA: Long,
        regB: Long,
        regC: Long,
    ): List<Long> = listOf(regA, regB, regC, comboOperand(value, regA, regB, regC) % 8)

    private fun bdv(
        value: Long,
        regA: Long,
        regB: Long,
        regC: Long,
    ): List<Long> = listOf(regA, regA shr comboOperand(value, regA, regB, regC).toInt(), regC)

    private fun cdv(
        value: Long,
        regA: Long,
        regB: Long,
        regC: Long,
    ): List<Long> = listOf(regA, regB, regA shr comboOperand(value, regA, regB, regC).toInt())

    private fun comboOperand(
        value: Long,
        regA: Long,
        regB: Long,
        regC: Long,
    ): Long =
        when (value) {
            6L -> regC
            5L -> regB
            4L -> regA
            else -> value
        }
}

fun main() {
    val name = Day17::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day17(testInput), Day17(realInput), year)
}
