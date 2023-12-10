package io.liodev.aoc.utils

data class Coord(val r: Int, val c: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    fun getBorder(length: Int = 1): List<Coord> {
        return buildList {
            add(Coord(r, c - 1))
            repeat(length + 2) {
                add(Coord(r - 1, c - 1 + it))
                add(Coord(r + 1, c - 1 + it))
            }
            add(Coord(r, c + length))
        }
    }

    override fun toString(): String {
        return "$r,$c"
    }

    fun validIndex(w: Int, h: Int) = r in 0 until h && c in 0 until w
    operator fun plus(other: Coord): Coord = Coord(this.r + other.r, this.c + other.c)
    fun validIndex(array: List<List<Char>>): Boolean {
        return validIndex(array.size, array[0].size)
    }

}

fun Pair<Int, Int>.toCoord() = Coord(this)

fun List<List<Any?>>.validIndex(coord: Coord) =
    coord.r in this.indices && coord.c in this[0].indices

operator fun <E> MutableList<MutableList<E>>.set(coord: Coord, value: E) {
    this[coord.r][coord.c] = value
}

operator fun <E> List<List<E>>.get(coord: Coord): E {
    return this[coord.r][coord.c]
}

fun <E> MutableList<MutableList<E>>.printMatrix() {
    this.forEach { row ->
        println(row.joinToString("") { it.toString() })
    }
}