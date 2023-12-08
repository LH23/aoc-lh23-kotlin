package io.liodev.aoc.utils

tailrec fun gcd(x: Long, y: Long): Long = if (y == 0L) x else gcd(y, x % y)

fun lcm(x: Long, y: Long) = x * y / gcd(x, y)

fun lcm(numbers: List<Long>): Long {
    var ans = 1L
    for (l in numbers) ans = lcm(ans, l)
    return ans
}