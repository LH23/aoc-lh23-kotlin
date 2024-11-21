package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.lcm

// --- 2023 Day 20: Pulse Propagation ---
class Day20(input: String) : Day<Long> {
    override val expectedValues = listOf(32000000L, 825167435, -1, 225514321828633)

    private val modules = input.split("\n").map { it.toModule() }.associateBy { it.label }
    private fun String.toModule(): Module {
        val (label, destinations) = this.split(" -> ")
        return Module(
            label[0],
            if (label[0] == 'b') label else label.substring(1),
            destinations.split(',').map { it.trim() }
        )
    }

    data class ModuleConfig(val modules: Map<String, Module>, val cycles: List<String> = listOf()) {
        private val pulseQueue = ArrayDeque<Pulse>()
        var low = 0L
        var high = 0L

        // part2
        private var i: Long = 0
        var cyclesFound: MutableMap<String, Long> = cycles.associateWith { -1L }.toMutableMap()

        init {
            modules.values.filter { it.mtype == '&' }.forEach { conjunction ->
                conjunction.conjunctionInputs = calculateInputs(conjunction.label)
            }
        }

        private fun calculateInputs(label: String): MutableMap<String, Char> =
            modules.values.filter { it.destinations.contains(label) }
                .map { it.label }
                .associateWith { 'L' }
                .toMutableMap()

        fun sendLowPulse(label: String, origin: String) {
            low++
            pulseQueue.addLast(Pulse(label, 'L', origin))
        }

        fun sendHighPulse(label: String, origin: String) {
            high++
            pulseQueue.addLast(Pulse(label, 'H', origin))
        }

        fun run() {
            while (pulseQueue.isNotEmpty()) {
                val pulse = pulseQueue.removeFirst()
                if (cycles.isNotEmpty()) checkCycles(pulse)
                val module = modules[pulse.label]
                module?.receive(pulse, this)
            }
        }

        private fun checkCycles(pulse: Pulse) {
            if (pulse.origin == "button") i++
            if ((pulse.origin in cycles && pulse.ptype == 'H')) {
                // I need to save the first one but is not the cycle yet, so I save it as negative
                cyclesFound[pulse.origin] = if (cyclesFound[pulse.origin] == -1L) -i
                else cyclesFound[pulse.origin]!! + i
            }
        }

    }

    data class Pulse(val label: String, val ptype: Char, val origin: String)
    data class Module(val mtype: Char, val label: String, val destinations: List<String>) {

        private var flipFlopState = false
        var conjunctionInputs: MutableMap<String, Char>? = null

        fun receive(pulse: Pulse, moduleConfig: ModuleConfig) {
            when (mtype) {
                'b' -> destinations.map {
                    if (pulse.ptype == 'L') moduleConfig.sendLowPulse(it, pulse.label)
                    else moduleConfig.sendHighPulse(it, pulse.label)
                }
                '%' -> {
                    if (pulse.ptype == 'H') return
                    flipFlopState = if (!flipFlopState) { // off
                        destinations.map { moduleConfig.sendHighPulse(it, pulse.label) }
                        true
                    } else { // on
                        destinations.map { moduleConfig.sendLowPulse(it, pulse.label) }
                        false
                    }
                }
                '&' -> {
                    conjunctionInputs!![pulse.origin] = pulse.ptype
                    if (conjunctionInputs!!.values.all { it == 'H' }) { // all H
                        destinations.map { moduleConfig.sendLowPulse(it, pulse.label) }
                    } else { // some L
                        destinations.map { moduleConfig.sendHighPulse(it, pulse.label) }
                    }
                }
            }
        }
    }

    override fun solvePart1(): Long {
        val moduleConfig = ModuleConfig(modules)
        repeat(1000) {
            moduleConfig.sendLowPulse("broadcaster", "button")
            moduleConfig.run()
        }
        return moduleConfig.low * moduleConfig.high
    }

    override fun solvePart2(): Long {
        // when "&nb" activates all it's inputs it will send "rx" the required low pulse,
        // therefore we need to detect the cycle of a high pulse for each of the inputs of "&nb"
        val cycles = listOf("pl", "zm", "mz", "lz") // hardcoded for my input

        val moduleConfig = ModuleConfig(modules, cycles)
        if (cycles.any { !moduleConfig.modules.contains(it) }) return -1L // ignore test case

        while (moduleConfig.cyclesFound.values.any { it < 0 }) {
            moduleConfig.sendLowPulse("broadcaster", "button")
            moduleConfig.run()
        }
        return lcm(moduleConfig.cyclesFound.values.toList())
    }
}

fun main() {
    val name = Day20::class.simpleName
    val year = 2023
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/${name}.txt")
    runDay(Day20(testInput), Day20(realInput), year, printTimings = true)
}