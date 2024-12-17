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
        input.split("\n\n")[0].lines().map { it.substringAfter(": ").toLong() }
    private val program =
        input
            .split("\n\n")[1]
            .substringAfter("Program: ")
            .split(",")
            .map { it.toInt() }

    override fun solvePart1(): String = Computer(program).run(initRegisters).joinToString(",")

    override fun solvePart2(): String = findReverseRecursive("", program, 1)!!

    class Computer(
        private val program: List<Int>,
    ) {
        private var ip = 0
        private var regA = 0L
        private var regB = 0L
        private var regC = 0L
        private val output = mutableListOf<Int>()

        fun run(registers: List<Long>): List<Int> {
            regA = registers[0]
            regB = registers[1]
            regC = registers[2]

            var halt = false
            ip = 0
            while (!halt) {
                processInstruction(program[ip] to program[ip + 1])
                ip += 2
                halt = ip >= program.size
            }
            return output
        }

        private fun processInstruction(instruction: Pair<Int, Int>) {
            val (opcode, value) = instruction
            when (opcode) {
                0 -> adv(value)
                1 -> bxl(value)
                2 -> bst(value)
                3 -> jnz(value)
                4 -> bxc()
                5 -> out(value)
                6 -> bdv(value)
                7 -> cdv(value)
                else -> throw IllegalArgumentException("Unknown opcode: $opcode")
            }
        }

        private fun adv(value: Int) {
            regA = regA shr comboOperand(value).toInt()
        }

        private fun bxl(value: Int) {
            regB = regB xor value.toLong()
        }

        private fun bst(value: Int) {
            regB = comboOperand(value) % 8
        }

        private fun jnz(value: Int) {
            if (regA != 0L) {
                ip = value - 2
            }
        }

        private fun bxc() {
            regB = regB xor regC
        }

        private fun out(value: Int) {
            output.add((comboOperand(value) % 8).toInt())
        }

        private fun bdv(value: Int) {
            regB = regA shr comboOperand(value).toInt()
        }

        private fun cdv(value: Int) {
            regC = regA shr comboOperand(value).toInt()
        }

        private fun comboOperand(value: Int): Long =
            when (value) {
                6 -> regC
                5 -> regB
                4 -> regA
                else -> value.toLong()
            }
    }

    private fun findReverseRecursive(
        result: String,
        program: List<Int>,
        n: Int,
    ): String? {
        val bits = listOf("000", "001", "010", "011", "100", "101", "110", "111")
        for (o in bits) {
            val newRegA = (result + o).toLong(2)
            val output = Computer(program).run(listOf(newRegA, initRegisters[1], initRegisters[2]))
            if (output == program) {
                return "$newRegA"
            } else if (output.takeLast(n) == program.takeLast(n)) {
                (findReverseRecursive(result + o, program, n + 1))?.let { regA -> return regA }
            }
        }
        return null
    }
}

fun main() {
    val name = Day17::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day17(testInput), Day17(realInput), year)
}
