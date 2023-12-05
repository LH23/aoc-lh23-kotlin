package io.liodev.aoc.utils

data class Coord(val x: Int, val y: Int) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    fun getBorder(length: Int = 1): List<Coord> {
        return buildList {
            add(Coord(x, y - 1))
            repeat(length + 2) {
                add(Coord(x - 1, y - 1 + it))
                add(Coord(x + 1, y - 1 + it))
            }
            add(Coord(x, y + length))
        }
    }

    fun validIndex(w: Int, h: Int) = x in 0 until w && y in 0 until h

}

fun Pair<Int, Int>.toCoord() = Coord(this)

fun List<List<Any?>>.validIndex(coord: Coord) =
    coord.x in this.indices && coord.y in this[0].indices