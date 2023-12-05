package io.liodev.aoc

import java.io.IOException
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads full content from the given input txt file.
 */
fun readInputAsString(filename: String): String {
    try {
        val path = Path(filename)
        return path.readText()
    } catch (e: IOException) {
        throw IllegalArgumentException("File $filename not found")
    }
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun checkResult(testname: String, actual: Any?, expected: Any?) {
    if (actual == expected) {
        println("✅ $testname Correct, $actual == $expected")
    } else {
        error("❌ $testname WRONG, $actual != $expected")
    }
}