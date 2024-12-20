package io.liodev.aoc.utils

import kotlin.math.abs

data class Coord(
    val r: Int,
    val c: Int,
) {
    constructor(pair: Pair<Int, Int>) : this(pair.first, pair.second)

    fun getBorder(length: Int = 1): List<Coord> =
        buildList {
            add(Coord(r, c - 1))
            repeat(length + 2) {
                add(Coord(r - 1, c - 1 + it))
                add(Coord(r + 1, c - 1 + it))
            }
            add(Coord(r, c + length))
        }

    val cardinalBorderDirs = listOf(Dir.West, Dir.East, Dir.North, Dir.South)

    fun getCardinalBorder(): List<Coord> =
        buildList {
            add(Coord(r, c - 1)) // WEST
            add(Coord(r, c + 1)) // EAST
            add(Coord(r - 1, c)) // NORTH
            add(Coord(r + 1, c)) // SOUTH
        }

    fun getHorizontalBorder(): List<Coord> =
        buildList {
            add(Coord(r, c - 1)) // WEST
            add(Coord(r, c + 1)) // EAST
        }

    fun getVerticalBorder(): List<Coord> =
        buildList {
            add(Coord(r - 1, c)) // NORTH
            add(Coord(r + 1, c)) // SOUTH
        }

    override fun toString(): String = "($r,$c)"

    fun <E> validIndex(array: List<List<E>>): Boolean = validIndex(array[0].size, array.size)

    fun validIndex(
        w: Int,
        h: Int,
    ) = r in 0..<h && c in 0..<w

    operator fun plus(other: Coord): Coord = Coord(this.r + other.r, this.c + other.c)

    fun goUp(n: Int = 1) = this + Coord(-n, 0)

    fun goLeft(n: Int = 1) = this + Coord(0, -n)

    fun goDown(n: Int = 1) = this + Coord(n, 0)

    fun goRight(n: Int = 1) = this + Coord(0, n)

    fun goUpLeft(n: Int = 1) = this.goUp(n).goLeft(n)

    fun goUpRight(n: Int = 1) = this.goUp(n).goRight(n)

    fun goDownLeft(n: Int = 1) = this.goDown(n).goLeft(n)

    fun goDownRight(n: Int = 1) = this.goDown(n).goRight(n)

    fun move(
        dir: Dir,
        n: Int = 1,
    ): Coord =
        when (dir) {
            Dir.North -> this.goUp(n)
            Dir.West -> this.goLeft(n)
            Dir.South -> this.goDown(n)
            Dir.East -> this.goRight(n)
        }

    fun moveInverse(
        dir: Dir,
        n: Int = 1,
    ): Coord =
        when (dir) {
            Dir.North -> this.goDown(n)
            Dir.West -> this.goRight(n)
            Dir.South -> this.goUp(n)
            Dir.East -> this.goLeft(n)
        }

    fun manhattanDistance(other: Coord): Int = abs(this.r - other.r) + abs(this.c - other.c)
}

fun parseDir(c: Char): Dir =
    when (c) {
        'L' -> Dir.West
        'D' -> Dir.South
        'R' -> Dir.East
        'U' -> Dir.North
        else -> throw IllegalArgumentException("Unknown dir: $c")
    }

fun dirFromTo(
    current: Coord,
    next: Coord,
): Dir =
    when (next) {
        current.move(Dir.North) -> Dir.North
        current.move(Dir.South) -> Dir.South
        current.move(Dir.East) -> Dir.East
        current.move(Dir.West) -> Dir.West
        else -> throw IllegalArgumentException("Not neighbouring coords")
    }

enum class Dir {
    North,
    West,
    South,
    East, ;

    fun turnRight(): Dir =
        when (this) {
            North -> East
            East -> South
            South -> West
            West -> North
        }
}

fun Pair<Int, Int>.toCoord() = Coord(this)

fun List<List<Any?>>.validIndex(coord: Coord) = coord.r in this.indices && coord.c in this[0].indices

operator fun <E> List<MutableList<E>>.set(
    coordinates: Coord,
    value: E,
) {
    this[coordinates.r][coordinates.c] = value
}

operator fun <E> List<List<E>>.get(coordinates: Coord): E = this[coordinates.r][coordinates.c]

fun <E> List<List<E>>.findFirstOrNull(e: E): Coord? {
    for (i in this.indices) {
        for (j in this[0].indices) {
            if (this[i][j] == e) {
                return Coord(i, j)
            }
        }
    }
    return null
}

fun <E> List<List<E>>.findAll(e: E): List<Coord> {
    val list = mutableListOf<Coord>()
    for (i in this.indices) {
        for (j in this[0].indices) {
            if (this[i][j] == e) {
                list.add(Coord(i, j))
            }
        }
    }
    return list
}

fun <E> List<List<E>>.printMatrix(separator: String = "") {
    this.forEach { row ->
        println(row.joinToString(separator) { it.toString() })
    }
}

fun <E> List<List<E>>.printPathInMatrix(
    path: List<Coord>,
    empty: E? = null,
    fill: E? = null,
) {
    val visualizedResult =
        List(this.size) { i ->
            MutableList(this[0].size) { j -> empty ?: this[i][j] }
        }
    for (p in path) {
        visualizedResult[p] = (fill ?: this[p])!!
    }
    visualizedResult.printMatrix()
}

fun vDist(
    a: Coord,
    b: Coord,
): Int = abs(a.r - b.r)

fun hDist(
    a: Coord,
    b: Coord,
): Int = abs(a.c - b.c)

/**
 * Returns the Cartesian product of this [IntRange] with another [IntRange].
 *
 * @param other The other [IntRange] to compute the Cartesian product with.
 * @return A list of pairs representing the Cartesian product.
 */
operator fun IntRange.times(other: IntRange): List<Pair<Int, Int>> = this.flatMap { x -> other.map { y -> x to y } }

/**
 * Returns the Cartesian product of this [Collection] with another [Iterable].

 * @param other The other [Iterable] to compute the Cartesian product with.
 * @return A list of pairs representing the Cartesian product.
 */
operator fun <T, S> Collection<T>.times(other: Iterable<S>): List<Pair<T, S>> = cartesianProduct(other) { first, second -> first to second }

/**
 * Computes the Cartesian product of this [Collection] with another [Iterable],
 * applying a transformation to each pair of elements.

 * @param other The other [Iterable] to compute the Cartesian product with.
 * @param transformer A function that takes a pair of elements (first from this [Collection] and
 *                    second from [other]) and transforms them into a value of type [V].
 * @return A list of values representing the Cartesian product after applying the transformer function.
 */
fun <T, S, V> Collection<T>.cartesianProduct(
    other: Iterable<S>,
    transformer: (first: T, second: S) -> V,
): List<V> = this.flatMap { first -> other.map { second -> transformer.invoke(first, second) } }

fun <E> List<MutableList<E>>.floodFill(
    at: Coord,
    water: E,
    space: E,
) {
    if (!this.validIndex(at)) return
    val queue = ArrayDeque<Coord>().apply { add(at) }
    while (queue.isNotEmpty()) {
        val next = queue.removeFirst()
        if (this[next] == space) {
            this[next] = water
            queue.addAll(next.getBorder(1).filter { this.validIndex(it) })
        }
    }
}
