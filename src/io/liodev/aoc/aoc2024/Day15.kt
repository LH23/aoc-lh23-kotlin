package io.liodev.aoc.aoc2024

import io.liodev.aoc.Day
import io.liodev.aoc.readInputAsString
import io.liodev.aoc.runDay
import io.liodev.aoc.utils.Coord
import io.liodev.aoc.utils.Dir
import io.liodev.aoc.utils.findFirstOrNull
import io.liodev.aoc.utils.get
import io.liodev.aoc.utils.set

// --- 2024 Day 15: Warehouse Woes ---
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
            robotPos = moveAttempt(mExpWarehouse, robotPos, step)
        }
//        println("final map:")
//        mExpWarehouse.printMatrix()
        return gpsCoordSum(mExpWarehouse)
    }

    private fun moveAttempt(
        wh: List<MutableList<Char>>,
        robotPos: Coord,
        dir: Dir,
    ): Coord =
        if (wh[robotPos.move(dir)] == '.') {
            wh[robotPos] = '.'
            wh[robotPos.move(dir)] = '@'
            robotPos.move(dir)
        } else if (wh[robotPos.move(dir)] == '#') {
            robotPos
        } else if (wh[robotPos.move(dir)] == 'O') {
            var newPos = robotPos.move(dir)
            while (wh[newPos] == 'O') {
                newPos = newPos.move(dir)
            }
            if (wh[newPos] == '.') {
                wh[newPos] = 'O'
                wh[robotPos] = '.'
                wh[robotPos.move(dir)] = '@'
                robotPos.move(dir)
            } else { // new pos #
                robotPos
            }
        } else { // [ or ]
            if (dir in listOf(Dir.West, Dir.East)) {
                moveHorizontally(wh, robotPos, dir)
            } else {
                moveVertically(wh, robotPos, dir)
            }
        }

    private fun moveVertically(
        wh: List<MutableList<Char>>,
        robotPos: Coord,
        dir: Dir,
    ): Coord {
        val newPos = robotPos.move(dir)
        val otherSideOfTheBox = if (wh[newPos] == ']') newPos.move(Dir.West) else newPos.move(Dir.East)
        return if (checkCanMove(wh, listOf(newPos, otherSideOfTheBox), dir)) {
            move(wh, robotPos, dir)
            robotPos.move(dir)
        } else {
            robotPos
        }
    }

    private fun move(
        wh: List<MutableList<Char>>,
        pos: Coord,
        dir: Dir,
    ) {
        if (wh[pos] == '@') {
            move(wh, pos.move(dir), dir)
            wh[pos.move(dir)] = '@'
            if (wh[pos.move(dir).move(Dir.West)] == '[') wh[pos.move(dir).move(Dir.West)] = '.'
            if (wh[pos.move(dir).move(Dir.East)] == ']') wh[pos.move(dir).move(Dir.East)] = '.'
            wh[pos] = '.'
        } else if (wh[pos] == '[') {
            move(wh, pos.move(dir), dir)
            move(wh, pos.move(dir).move(Dir.East), dir)
            wh[pos.move(Dir.East)] = '.'
            wh[pos.move(dir)] = '['
            wh[pos.move(dir).move(Dir.East)] = ']'
        } else if (wh[pos] == ']') {
            move(wh, pos.move(dir), dir)
            move(wh, pos.move(dir).move(Dir.West), dir)
            wh[pos.move(Dir.West)] = '.'
            wh[pos.move(dir)] = ']'
            wh[pos.move(dir).move(Dir.West)] = '['
        }
    }

    private fun checkCanMove(
        wh: List<List<Char>>,
        places: List<Coord>,
        dir: Dir,
    ): Boolean =
        if (places.map { wh[it.move(dir)] }.any { it == '#' }) {
            false
        } else if (places.map { wh[it.move(dir)] }.all { it == '.' }) {
            true
        } else {
            val newPlaces = mutableListOf<Coord>()
            for (place in places) {
                if (wh[place.move(dir)] == ']') {
                    newPlaces.add(place.move(dir))
                    newPlaces.add(
                        place.move(dir).move(Dir.West),
                    )
                }
                if (wh[place.move(dir)] == '[') {
                    newPlaces.add(place.move(dir))
                    newPlaces.add(
                        place.move(dir).move(Dir.East),
                    )
                }
            }
            checkCanMove(wh, newPlaces, dir)
        }

    private fun moveHorizontally(
        wh: List<MutableList<Char>>,
        robotPos: Coord,
        dir: Dir,
    ): Coord {
        var newPos = robotPos.move(dir)
        while (wh[newPos] in listOf('[', ']')) {
            newPos = newPos.move(dir)
        }
        if (wh[newPos] == '.') {
            wh[robotPos] = '.'
            wh[robotPos.move(dir)] = '@'
            var tmp = robotPos.move(dir, 2)
            while (tmp != newPos) {
                wh[tmp] = if (wh[tmp] == '[') ']' else '['
                tmp = tmp.move(dir)
            }
            wh[newPos] = if (dir == Dir.West) '[' else ']'
            return robotPos.move(dir)
        } else { // new pos #
            return robotPos
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
