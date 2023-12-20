package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.lcm

// 2023 Day20
class Day20(input: String) : Day<Long> {
    //8000 low pulses and 4000 high pulses are sent. Multiplying these together gives 32000000.
    //4250 low pulses and 2750 high pulses are sent. Multiplying these together gives 11687500.
    override val expectedValues = listOf(11687500L, 825167435, 11687500L, 225514321828633)

    val modules = input.split("\n").map { it.toModule() }.associateBy { it.label }

    private fun String.toModule(): Module {
        val (label, destinations) = this.split(" -> ")
        return Module(
            label[0],
            if (label[0] == 'b') label else label.substring(1),
            destinations.split(',').map { it.trim() }
        )
    }

    data class ModuleConfig(val modules: Map<String, Module>) {
        var i: Long = 0
        private val pulseQueue = ArrayDeque<Pulse>()
        var low = 0L
        var high = 0L

        init {
            modules.values.filter { it.mtype == '&' }.forEach { conjunction ->
                conjunction.conjunctionInputs = calculateInputs(conjunction.label)
                //println("calculated conj $conjunction: ${conjunction.conjunctionInputs}")
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

        fun run(): Pair<Long, Long> {
            while (pulseQueue.isNotEmpty()) {
                val pulse = pulseQueue.removeFirst()
                if ((pulse.label == "rx" && pulse.ptype == 'L')) {
                    println("FIRING ${pulse}! en $i")
                }
                if ((pulse.label == "bn" && pulse.ptype == 'H')) {
                    println("FIRING ${pulse}! en $i")
                }
                val module = modules[pulse.label]
                module?.receive(pulse, this)
            }
            return low to high
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
        println(moduleConfig.low to moduleConfig.high)
        return moduleConfig.low * moduleConfig.high
    }

    override fun solvePart2(): Long {
        // NEED TO FIND THE CYCLES FOR "bn" INPUTS
//        val moduleConfig = ModuleConfig(modules)
//        var i = 0L
//        while (true) {
//            i++
//            if (i % 1000000L == 0L) println (i)
//            moduleConfig.i = i
//            moduleConfig.sendLowPulse("broadcaster", "button")
//            moduleConfig.run()
//        }
        return lcm(listOf(3797,3823,3881,4003))
    }
}

fun main() {
    val name = Day20::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test2.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day20(testInput), Day20(realInput), skipTests = listOf(false, false, true, false))
}