package io.liodev.aoc.aoc2023

import com.microsoft.z3.BoolExpr
import com.microsoft.z3.Context
import com.microsoft.z3.Expr
import com.microsoft.z3.Model
import com.microsoft.z3.RatNum
import com.microsoft.z3.RealExpr
import com.microsoft.z3.Sort
import com.microsoft.z3.Status
import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord3DL
import io.liodev.aoc.utils.times

// --- 2023 Day 24: Never Tell Me The Odds ---
class Day24(input: String) : Day<Long> {
    // 24, 13, 10 and throwing the rock at velocity -3, 1, 2.
    // Adding 24, 13, 10 together produces 47
    override val expectedValues = listOf(0L, 20361, 47, 558415252330828)

    private val hailstones = input.split("\n").map { it.toHailstone() }

    private data class Hailstone(val pos: Coord3DL, val speedNs: Coord3DL) {
        // f(t) = (x0 + x*t, y0 + y*t)
        fun pos(t: Double): Pair<Double, Double> = pos.x + speedNs.x * t to pos.y + speedNs.y * t
    }

    private fun String.toHailstone(): Hailstone {
        val parsed = this.split("@").map { it.split(",").map { it.trim().toLong() } }
        return Hailstone(
            Coord3DL(parsed[0][0], parsed[0][1], parsed[0][2]),
            Coord3DL(parsed[1][0], parsed[1][1], parsed[1][2]),
        )
    }

    override fun solvePart1(): Long {
        val pairs = (hailstones * hailstones).map { setOf(it.first, it.second) }.toSet()
            .filter { it.size == 2 }
        return pairs.map { it.toList()[0] to it.toList()[1] }.count { (a, b) ->
            intersects(a, b, 200000000000000L, 400000000000000L)
        }.toLong()
    }

    private fun intersects(a: Hailstone, b: Hailstone, start: Long, end: Long): Boolean {
        val (ax1, ay1) = a.pos.x to a.pos.y
        val (ax2, ay2) = a.pos(1.0)
        val A1 = ay2 - ay1
        val B1 = ax1 - ax2
        val C1 = A1 * ax1 + B1 * ay1

        val (bx1, by1) = b.pos.x to b.pos.y
        val (bx2, by2) = b.pos(1.0)
        val A2 = by2 - by1
        val B2 = bx1 - bx2
        val C2 = A2 * bx1 + B2 * by1

        val det = A1 * B2 - A2 * B1
        return if (det == 0.0) {
            false
        } else {
            val x = (B2 * C1 - B1 * C2) / det
            val y = (A1 * C2 - A2 * C1) / det

            if ((x < a.pos.x && a.speedNs.x > 0 ||
                        x > a.pos.x && a.speedNs.x < 0)
            ) {
                return false
            }
            if ((x < b.pos.x && b.speedNs.x > 0 ||
                        x > b.pos.x && b.speedNs.x < 0)
            ) {
                return false
            }
            x in start.toDouble()..end.toDouble() && y in start.toDouble()..end.toDouble()
        }
    }

    override fun solvePart2(): Long = solveWithZ3Solver()

    private fun solveWithZ3Solver(): Long = with(Context()) {
        val solver = mkSolver()
        val x = mkRealConst("x")
        val y = mkRealConst("y")
        val z = mkRealConst("z")
        val sx = mkRealConst("sx")
        val sy = mkRealConst("sy")
        val sz = mkRealConst("sz")

        hailstones.take(3).forEachIndexed { i, hailstone ->
            val ti = mkRealConst("t$i")
            // x + sx * ti = hx + hsx * ti
            solver.add(
                mkEq(
                    mkAdd(x, mkMul(sx, ti)),
                    mkAdd(mkReal(hailstone.pos.x), mkMul(mkReal(hailstone.speedNs.x), ti))
                )
            )
            // y + sy * ti = hy + hsy * ti
            solver.add(
                mkEq(
                    mkAdd(y, mkMul(sy, ti)),
                    mkAdd(mkReal(hailstone.pos.y), mkMul(mkReal(hailstone.speedNs.y), ti))
                )
            )
            // z + sz * tii = hz + hsz * ti
            solver.add(
                mkEq(
                    mkAdd(z, mkMul(sz, ti)),
                    mkAdd(mkReal(hailstone.pos.z), mkMul(mkReal(hailstone.speedNs.z), ti))
                )
            )
            // ti >= 0
            solver.add(mkGe(ti, mkReal(0)))
        }

        return if (solver.check() == Status.SATISFIABLE) {
            solver.model[x] + solver.model[y] + solver.model[z]
        } else -1L
    }

    private operator fun Model.get(x: RealExpr?): Long {
        return (this.getConstInterp(x) as RatNum).let { real ->
            real.bigIntNumerator.toLong() / real.bigIntDenominator.toLong()
        }
    }
}

fun main() {
    val name = Day24::class.simpleName
    val year = 2023
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val realInput = readInputAsString("src/input/$year/${name}.txt")
    runDay(Day24(testInput), Day24(realInput), year, printTimings = true)
}