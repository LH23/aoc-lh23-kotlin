package io.liodev.aoc.utils

data class Coord3DL(val x: Long, val y: Long, val z : Long) {
    constructor(triple: Triple<Long, Long, Long>) : this(triple.first, triple.second, triple.third)

    override fun toString(): String {
        return "$x,$y,$z"
    }

    fun validIndex(array: List<List<List<Char>>>): Boolean {
        return validIndex(array.size.toLong(), array[0].size.toLong(), array[0][0].size.toLong())
    }

    private fun validIndex(w: Long, d: Long, h: Long) = z in 0 until h && x in 0 until w && y in 0 until d

    operator fun plus(other: Coord3DL): Coord3DL = Coord3DL(this.x + other.x, this.y + other.y, this.z + other.z)

    fun goUp(n: Long = 1) = this + Coord3DL(0, 0, n)
    fun goDown(n: Long = 1) = this + Coord3DL(0, 0, -n)

    fun goLeft(n: Long = 1) = this + Coord3DL(-n, 0, 0)
    fun goRight(n: Long = 1) = this + Coord3DL(n,0,0)

    fun goFront(n: Long = 1) = this + Coord3DL(0, -n, 0)
    fun goBack(n: Long = 1) = this + Coord3DL(0, n, 0)

}