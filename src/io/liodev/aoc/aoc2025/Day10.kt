package io.liodev.aoc.aoc2025

import com.microsoft.z3.Context
import com.microsoft.z3.IntExpr
import com.microsoft.z3.Status
import io.liodev.aoc.Day
import io.liodev.aoc.println
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay

// --- 2025 Day 10: Factory ---
class Day10(
    input: String,
) : Day<Long> {
    override val expectedValues = listOf(7L, 512, 33, 19857)

    private val machines = input.split("\n").map { Machine.fromString(it) }

    data class Machine(
        val indicatorLightsOn: List<Boolean>,
        val buttonActivations: List<Set<Int>>,
        val joltageRequirements: List<Int>,
    ) {
        companion object {
            fun fromString(string: String): Machine {
                val lights = string.drop(1).substringBefore("]").toList().map { it == '#' }
                val activations =
                    string.substringBefore(" {").substringAfter(" ").split(" ").map { activation ->
                        activation.drop(1).dropLast(1).split(",").map { it.toInt() }.toSet()
                    }
                val joltages = string.substringAfter("{").dropLast(1).split(",").map { it.toInt() }
                return Machine(lights, activations, joltages)
            }
        }

        fun minStepsToMatchLights(): Int {
            val initial = List(indicatorLightsOn.size) { false }
            return minStepsToMatchLightsBfs(initial, indicatorLightsOn) ?: Int.MAX_VALUE
        }

        private fun minStepsToMatchLightsBfs(
            initial: List<Boolean>,
            expected: List<Boolean>,
        ): Int? {
            val queue = ArrayDeque<Pair<List<Boolean>, Int>>()
            queue.addLast(Pair(initial, 0))

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                val (state, steps) = current
                if (state == expected) {
                    return steps
                }
                for (activation in buttonActivations) {
                    val newState = state.mapIndexed { index, value ->
                        if (index in activation) !value else value
                    }
                    queue.addLast(Pair(newState, steps + 1))
                }
            }
            return null
        }

        private fun List<Boolean>.toLights(): String =
            "[" + this.map { if (it) '#' else '.' }.joinToString("") + "]"

        fun minStepsToMatchJoltagesZ3(): Int {
            val context = Context()
            val solver = context.mkSolver()

            val vars: List<IntExpr> = buttonActivations.mapIndexed { index, _ ->
                val v = context.mkIntConst("bp$index")
                solver.add(context.mkGe(v, context.mkInt(0)))
                v
            }
            val constants = joltageRequirements.mapIndexed { index, joltage ->
                context.mkInt(joltage)
            }
            joltageRequirements.mapIndexed { i, joltage ->
                val sumTerms = buttonActivations.mapIndexed { j, activation ->
                    if (i in activation) {
                        vars[j]
                    } else {
                        null
                    }
                }.filterNotNull()
                solver.add(context.mkEq(constants[i], context.mkAdd(*sumTerms.toTypedArray())))
            }
            val count = context.mkIntConst("totalCount")
            solver.add(context.mkEq(count, context.mkAdd(*vars.toTypedArray())))
            
            var min = Int.MAX_VALUE
            while (solver.check() == Status.SATISFIABLE) {
                min = solver.model.eval(count, true).toString().toInt()
                val newMin = context.mkLt(count, context.mkInt(min))
                solver.add(newMin)
            }
            return min
        }
        
        // still not working :( too slow or invalid optimizations
        fun minStepsToMatchJoltages(): Int {
            val final = List(joltageRequirements.size) { 0 }
            return minStepsToMatchJoltagesBfs(joltageRequirements, final).also { it.println() }
        }

        private fun minStepsToMatchJoltagesBfs(
            initial: List<Int>,
            final: List<Int>,
        ): Int {
            val cache = mutableMapOf<List<Int>, Int>()
            cache[final] = 0
            for (activation in buttonActivations) {
                (1..1000).forEach {
                    val state = final.mapIndexed { i, v ->
                        if (i in activation) it else v
                    }
                    //println("filling cache: $state -> $it steps")
                    cache[state] = it
                }
            }
            val queue = ArrayDeque<Pair<List<Int>, Int>>()
            queue.addLast(Pair(initial, 0))

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                //println("checking state: ${current.first}, steps: ${current.second}")

                val (state, steps) = current
                if (cache.containsKey(state)) {
                    return steps + cache[state]!!
                } else if (state == final) {
                    return steps
                }
                for (activation in buttonActivations) {
                    val max =
                        (state.mapIndexed { i, v -> if (i in activation) v else Int.MAX_VALUE }
                            .min()).coerceAtLeast(1)
                    val nr = max downTo 1

                    for (n in nr) {
                        //println("state $state activation: $activation, I can press $n times!")
                        val newState = state.mapIndexed { i, v ->
                            if (i in activation) v - n else v
                        }
                        if (newState.any { it < 0 }) {
                            break
                        }
                        if (cache.containsKey(newState)) {
                            val totalSteps = steps + n + cache[newState]!!
                            println("PRE Update cache for $state, steps: $totalSteps")
                            cache[state] = minOf(totalSteps, cache[state] ?: Int.MAX_VALUE)
                            return totalSteps
                        }
                        if (n == 1) {
                            queue.addLast(Pair(newState, steps + n))
                        }
                    }
                }
            }
            error("invalid state, cannot reach: $final")
        }
    }
    
    override fun solvePart1(): Long = machines.sumOf { it.minStepsToMatchLights() }.toLong()

    override fun solvePart2(): Long = machines.sumOf { it.minStepsToMatchJoltagesZ3().toLong() }
}


fun main() {
    val name = Day10::class.simpleName
    val year = 2025
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day10(testInput), Day10(realInput), year)
}
