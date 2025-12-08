package io.liodev.aoc.utils

import kotlin.math.sqrt

data class Coord3D(val x: Int, val y: Int, val z : Int) {
    constructor(triple: Triple<Int, Int, Int>) : this(triple.first, triple.second, triple.third)

    override fun toString(): String {
        return "$x,$y,$z"
    }

    fun validIndex(array: List<List<List<Char>>>): Boolean {
        return validIndex(array.size, array[0].size, array[0][0].size)
    }

    private fun validIndex(w: Int, d: Int, h: Int) = z in 0 until h && x in 0 until w && y in 0 until d

    operator fun plus(other: Coord3D): Coord3D = Coord3D(this.x + other.x, this.y + other.y, this.z + other.z)

    fun goUp(n: Int = 1) = this + Coord3D(0, 0, n)
    fun goDown(n: Int = 1) = this + Coord3D(0, 0, -n)

    fun goLeft(n: Int = 1) = this + Coord3D(-n, 0, 0)
    fun goRight(n: Int = 1) = this + Coord3D(n,0,0)

    fun goFront(n: Int = 1) = this + Coord3D(0, -n, 0)
    fun goBack(n: Int = 1) = this + Coord3D(0, n, 0)

    fun euclideanDistance(other: Coord3D): Double {
        return sqrt(
            ((other.x - this.x).toDouble() * (other.x - this.x).toDouble() +
                    (other.y - this.y).toDouble() * (other.y - this.y).toDouble() +
                    (other.z - this.z).toDouble() * (other.z - this.z).toDouble())
        )
    }

    fun manhattanDistance(other: Coord3D): Int {
        return kotlin.math.abs(other.x - this.x) +
                kotlin.math.abs(other.y - this.y) +
                kotlin.math.abs(other.z - this.z)
    }
}