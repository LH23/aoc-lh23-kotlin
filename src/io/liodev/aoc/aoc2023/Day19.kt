package io.liodev.aoc.aoc2023

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay


// --- 2023 Day 19: Aplenty ---
class Day19(input: String) : Day<Long> {
    override val expectedValues = listOf(19114L, 532551, 167409079868000, 134343280273968)

    private val workflows =
        input.split("\n\n")[0].lines().map { it.toWorkflow() }.associateBy { it.label }

    private fun String.toWorkflow(): Workflow {
        val label = this.substringBefore('{')
        val rules = this.substringAfter('{').substringBefore('}').split(',').map { it.toRule() }
        return Workflow(label, rules)
    }

    private fun String.toRule(): Rule {
        val condition = if (this.contains(':')) {
            val cond = this.substringBefore(':').split('<', '>')
            Condition(cond[0][0], if (this.contains('<')) '<' else '>', cond[1].toInt())
        } else {
            Condition('x', '>', -1) // True
        }
        val sendTo = if (this.contains(':')) this.substringAfter(':') else this
        return Rule(condition, sendTo)
    }

    data class Workflow(val label: String, val rules: List<Rule>) {
        fun runRules(batch: Batch): String {
            for (rule in rules) {
                if (rule.condition.valid(batch)) return rule.sendTo
            }
            return "R"
        }
    }

    private fun calculateRulesTree(label: String): ConditionTree {
        return when (label) {
            "A" -> A
            "R" -> R
            else -> calculateRulesTree(workflows[label]!!.rules)
        }
    }

    private fun calculateRulesTree(rules: List<Rule>): ConditionTree {
        return if (rules.isEmpty()) R
        else ConditionTree(
            rules[0].condition,
            calculateRulesTree(rules[0].sendTo),
            calculateRulesTree(rules.drop(1))
        )
    }

    data class ConditionTree(
        val condition: Condition,
        val valid: ConditionTree?,
        val invalid: ConditionTree?
    )

    val A = ConditionTree(Condition('x', '>', 0), null, null) // True
    val R = ConditionTree(Condition('x', '<', 0), null, null) // False

    data class Rule(val condition: Condition, val sendTo: String)
    data class Condition(
        val type: Char,
        val op: Char,
        val value: Int,
    ) {
        fun valid(batch: Batch): Boolean {
            val t = when (type) {
                'x' -> batch.x
                'm' -> batch.m
                'a' -> batch.a
                's' -> batch.s
                else -> error("?")
            }
            return if (op == '<') t < value else t > value
        }

        override fun toString() = "$type $op $value"
    }

    private val batches = input.split("\n\n")[1].lines().map { it.toBatch() }

    data class Batch(val x: Int, val m: Int, val a: Int, val s: Int) {
        fun totalAccepted(workflows: Map<String, Workflow>): Int {
            var dest = workflows["in"]!!.runRules(this)
            while (true) {
                if (dest == "A") return x + m + a + s
                if (dest == "R") return 0
                dest = workflows[dest]!!.runRules(this)
            }
        }
    }

    private fun String.toBatch(): Batch {
        val parsed = this.substringBefore('}').split(',').map { it.substringAfter('=').toInt() }
        return Batch(parsed[0], parsed[1], parsed[2], parsed[3])
    }

    override fun solvePart1(): Long = batches.sumOf { it.totalAccepted(workflows) }.toLong()

    override fun solvePart2(): Long {
        val tree = calculateRulesTree("in")
        val pathsToA = getAllPaths(tree)
            .filter { it.contains(A.condition) }
            .map { list ->
                list.filter { it.value != -1 }.takeWhile { it != A.condition }
            }
        return pathsToA.sumOf { path ->
            "xmas".map { type -> rangeSize(type.getRange(path)) }.reduce { a, b -> a * b }
        }
    }

    private fun rangeSize(it: IntRange) = (it.last + 1 - it.first).toLong()

    private fun Char.getRange(path: List<Condition>): IntRange {
        var start = 1
        var end = 4000
        for (cond in path) {
            if (this == cond.type) {
                if (cond.op == '>') start = start.coerceAtLeast(cond.value + 1)
                if (cond.op == '<') end = end.coerceAtMost(cond.value - 1)
            }
        }
        return start..end
    }

    private fun getAllPaths(tree: ConditionTree): List<List<Condition>> {
        return if (tree.valid == null && tree.invalid == null) {
            listOf(listOf(tree.condition))
        } else {
            val valid = if (tree.valid == null) listOf()
            else getAllPaths(tree.valid).map { conditions -> listOf(tree.condition) + conditions }
            val invalid = if (tree.invalid == null) listOf()
            else getAllPaths(tree.invalid).map { conditions -> listOf(negated(tree.condition)) + conditions }
            return listOf(valid, invalid).flatten()
        }
    }

    private fun negated(condition: Condition): Condition {
        return condition.copy(
            op = if (condition.op == '<') '>' else '<',
            value = if (condition.op == '<') condition.value - 1 else condition.value + 1
        )
    }

    private fun ConditionTree.printTree() {
        val sb = StringBuilder()
        traversePreOrder(sb, "", "", this)
        println(sb.toString())
    }

    private fun traversePreOrder(
        sb: java.lang.StringBuilder,
        padding: String?,
        pointer: String?,
        node: ConditionTree?
    ) {
        if (node != null) {
            sb.append(padding)
            sb.append(pointer)
            sb.append(node.condition)
            sb.append("\n")
            val paddingBuilder = java.lang.StringBuilder(padding)
            paddingBuilder.append("│  ")
            val paddingForBoth = paddingBuilder.toString()
            val pointerForRight = "└──"
            val pointerForLeft = if (node.valid != null) "├──" else "└──"
            traversePreOrder(sb, paddingForBoth, pointerForLeft, node.valid)
            traversePreOrder(sb, paddingForBoth, pointerForRight, node.invalid)
        }
    }
}


fun main() {
    val name = Day19::class.simpleName
    val testInput = readInputAsString("src/input/2023/${name}_test.txt")
    val realInput = readInputAsString("src/input/2023/${name}.txt")
    runDay(Day19(testInput), Day19(realInput))
}