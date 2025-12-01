package io.liodev.aoc

import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.writer

fun prepareDay(
    day: Int,
    year: Int,
): Boolean {
    val paddedDay = day.toString().padStart(2, '0')

    val srcKtFile = Path("src/io/liodev/aoc/aoc$year/Day$paddedDay.kt")
    if (!srcKtFile.exists()) {
        val dayTemplate =
            readInputAsString("src/io/liodev/aoc/Day99.kt")
                .replace("package io.liodev.aoc", "package io.liodev.aoc.aoc$year")
                .replace("Day99", "Day$paddedDay")
                .replace("val year = 2024", "val year = $year")
                .replace("""// TEMPLATE .*?\n""".toRegex(), "// --- $year $day\n")

        srcKtFile.createParentDirectories()
        srcKtFile.createFile()
        val fileWriter = srcKtFile.writer()
        fileWriter.write(dayTemplate)
        fileWriter.close()

        val realInputFile = Path("src/input/$year/Day$paddedDay.txt")
        if (!realInputFile.exists()) {
            realInputFile.createParentDirectories()
            realInputFile.createFile()
        }

        val testInputFile = Path("src/input/$year/Day${paddedDay}_test.txt")
        if (!testInputFile.exists()) {
            testInputFile.createFile()
        }
        return true
    } else {
        return false
    }
}

@Suppress("KotlinConstantConditions")
fun main() {
    val day = 2
    val year = 2025

    if (year in 2015..2030 && day in 1..25) {
        if (prepareDay(day, year)) {
            println("Created AOC Day $day/$year")
        } else {
            println("Error, AOC Day $day/$year already exists")
        }
    } else {
        println("Error, invalid day/year: $day/$year")
    }
}
