package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.findFirstOrNull
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.printMatrix
import io.liodev.aoc.utils.set

// --- 2024 15
class Day15(
    input: String,
) : Day<Int> {
    override val expectedValues = listOf(10092, 1499739, 9021, 1522215, 908, 618)

    private val warehouse = input.split("\n\n")[0].lines().map { it.toList() }
    private val moves =
        input
            .split("\n\n")[1]
            .replace("\n", "")
            .map {
                when (it) {
                    '<' -> Dir.West
                    '>' -> Dir.East
                    '^' -> Dir.North
                    'v' -> Dir.South
                    else -> throw IllegalArgumentException("Unknown direction: $it")
                }
            }

    override fun solvePart1(): Int {
        val mwarehouse = warehouse.indices.map { i -> warehouse[i].toMutableList() }
        var robotPos = warehouse.findFirstOrNull('@')!!
        for (step in moves) {
            robotPos = moveAttempt(mwarehouse, robotPos, step)
        }
        return gpsCoordSum(mwarehouse)
    }

    override fun solvePart2(): Int {
        val mExpWarehouse = warehouse.indices.map { i -> warehouse[i].expand() }
        var robotPos = mExpWarehouse.findFirstOrNull('@')!!
        for (step in moves) {
            robotPos = moveAttemptExpanded(mExpWarehouse, robotPos, step)
        }
//        println("final map:")
//        mExpWarehouse.printMatrix()
        return gpsCoordSum(mExpWarehouse)
    }

    private fun moveAttemptExpanded(
        mwarehouse: List<MutableList<Char>>,
        robotPos: Coord,
        dir: Dir,
    ): Coord =
        if (mwarehouse[robotPos.move(dir)] == '.') {
            mwarehouse[robotPos] = '.'
            mwarehouse[robotPos.move(dir)] = '@'
            robotPos.move(dir)
        } else if (mwarehouse[robotPos.move(dir)] == '#') {
            robotPos
        } else { // [ or ]
            if (dir in listOf(Dir.West, Dir.East)) {
                moveHorizontally(mwarehouse, robotPos, dir)
            } else {
                moveVertically(mwarehouse, robotPos, dir)
            }
        }

    private fun moveVertically(
        mwarehouse: List<MutableList<Char>>,
        robotPos: Coord,
        dir: Dir,
    ): Coord {
        val newPos = robotPos.move(dir)
        val canMove =
            if (mwarehouse[newPos] == ']') {
                checkCanMove(
                    mwarehouse,
                    listOf(newPos.move(Dir.West), newPos),
                    dir,
                )
            } else {
                checkCanMove(mwarehouse, listOf(newPos, newPos.move(Dir.East)), dir)
            }

        return if (canMove) {
            move(mwarehouse, robotPos, dir)
            robotPos.move(dir)
        } else {
            robotPos
        }
    }

    private fun move(
        mwarehouse: List<MutableList<Char>>,
        pos: Coord,
        dir: Dir,
    ) {
        if (mwarehouse[pos] == '@') {
            move(mwarehouse, pos.move(dir), dir)
            mwarehouse[pos.move(dir)] = '@'
            if (mwarehouse[pos.move(dir).move(Dir.West)] == '[') mwarehouse[pos.move(dir).move(Dir.West)] = '.'
            if (mwarehouse[pos.move(dir).move(Dir.East)] == ']') mwarehouse[pos.move(dir).move(Dir.East)] = '.'
            mwarehouse[pos] = '.'
        } else if (mwarehouse[pos] == '[') {
            move(mwarehouse, pos.move(dir), dir)
            move(mwarehouse, pos.move(dir).move(Dir.East), dir)
            mwarehouse[pos] = '.'
            mwarehouse[pos.move(Dir.East)] = '.'
            mwarehouse[pos.move(dir)] = '['
            mwarehouse[pos.move(dir).move(Dir.East)] = ']'
        } else if (mwarehouse[pos] == ']') {
            move(mwarehouse, pos.move(dir), dir)
            move(mwarehouse, pos.move(dir).move(Dir.West), dir)
            mwarehouse[pos] = '.'
            mwarehouse[pos.move(Dir.West)] = '.'
            mwarehouse[pos.move(dir)] = ']'
            mwarehouse[pos.move(dir).move(Dir.West)] = '['
        }
    }

    private fun checkCanMove(
        mwarehouse: List<List<Char>>,
        places: List<Coord>,
        dir: Dir,
    ): Boolean =
        if (places.map { mwarehouse[it.move(dir)] }.any { it == '#' }) {
            false
        } else if (places.map { mwarehouse[it.move(dir)] }.all { it == '.' }) {
            true
        } else {
            val newPlaces = mutableListOf<Coord>()
            for (place in places) {
                if (mwarehouse[place.move(dir)] == ']') {
                    newPlaces.add(place.move(dir))
                    newPlaces.add(
                        place.move(dir).move(Dir.West),
                    )
                }
                if (mwarehouse[place.move(dir)] == '[') {
                    newPlaces.add(place.move(dir))
                    newPlaces.add(
                        place.move(dir).move(Dir.East),
                    )
                }
            }
            checkCanMove(mwarehouse, newPlaces, dir)
        }

    private fun moveHorizontally(
        mwarehouse: List<MutableList<Char>>,
        robotPos: Coord,
        dir: Dir,
    ): Coord {
        var newPos = robotPos.move(dir)
        while (mwarehouse[newPos] in listOf('[', ']')) {
            newPos = newPos.move(dir)
        }
        if (mwarehouse[newPos] == '.') {
            mwarehouse[robotPos] = '.'
            mwarehouse[robotPos.move(dir)] = '@'
            var tmp = robotPos.move(dir, 2)
            while (tmp != newPos) {
                mwarehouse[tmp] = if (mwarehouse[tmp] == '[') ']' else '['
                tmp = tmp.move(dir)
            }
            mwarehouse[newPos] = if (dir == Dir.West) '[' else ']'
            return robotPos.move(dir)
        } else { // new pos #
            return robotPos
        }
    }

    private fun moveAttempt(
        mwarehouse: List<MutableList<Char>>,
        robotPos: Coord,
        dir: Dir,
    ): Coord {
        if (mwarehouse[robotPos.move(dir)] == '.') {
            mwarehouse[robotPos] = '.'
            mwarehouse[robotPos.move(dir)] = '@'
            return robotPos.move(dir)
        } else if (mwarehouse[robotPos.move(dir)] == '#') {
            return robotPos
        } else { // O
            var newPos = robotPos.move(dir)
            while (mwarehouse[newPos] == 'O') {
                newPos = newPos.move(dir)
            }
            if (mwarehouse[newPos] == '.') {
                mwarehouse[newPos] = 'O'
                mwarehouse[robotPos] = '.'
                mwarehouse[robotPos.move(dir)] = '@'
                return robotPos.move(dir)
            } else { // new pos #
                return robotPos
            }
        }
    }

    private fun gpsCoordSum(map: List<List<Char>>): Int =
        map.indices.sumOf { r ->
            map[0].indices.sumOf { c ->
                if (map[r][c] == 'O' || map[r][c] == '[') r * 100 + c else 0
            }
        }
}

private fun List<Char>.expand(): MutableList<Char> =
    this
        .flatMap {
            when (it) {
                '#' -> listOf('#', '#')
                '.' -> listOf('.', '.')
                'O' -> listOf('[', ']')
                '@' -> listOf('@', '.')
                else -> throw IllegalArgumentException("Unknown elem: $it")
            }
        }.toMutableList()

fun main() {
    val name = Day15::class.simpleName
    val year = 2024
    val testInput = readInputAsString("src/input/$year/${name}_test.txt")
    val testInput2 = readInputAsString("src/input/$year/${name}_test2.txt")
    val realInput = readInputAsString("src/input/$year/$name.txt")
    runDay(Day15(testInput), Day15(realInput), year, extraDays = listOf(Day15(testInput2)))
}
