package io.liodev.aoc

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.reflect.KClass

class AllDaysTest {
    companion object {
        @JvmStatic
        fun data(): List<Array<*>> {
            val tests2022: List<Array<*>> = (1..16).map { arrayOf("Day ${it.pad()}", 2022) }
            val tests2023: List<Array<*>> = (1..25).map { arrayOf("Day ${it.pad()}", 2023) }
            val tests2024: List<Array<*>> = (1..13).map { arrayOf("Day ${it.pad()}", 2024) }
            return (tests2022 + tests2023 + tests2024)
        }

        private fun Int.pad() = toString().padStart(2, '0')
    }

    @Suppress("UNCHECKED_CAST")
    @ParameterizedTest
    @MethodSource("data")
    fun testAllDays(
        day: String,
        year: Int,
    ) {
        val dayClassName = "Day${day.drop(4)}"
        val dayClass = Class.forName("io.liodev.aoc.aoc$year.$dayClassName").kotlin
        runTestForDay(dayClass as KClass<Day<*>>, year)
    }

    private fun runTestForDay(
        kDay: KClass<out Day<*>>,
        year: Int = 2023,
    ) {
        val name = kDay.simpleName
        val testInput = readInputAsString("src/input/$year/${name}_test.txt")
        val realInput = readInputAsString("src/input/$year/$name.txt")
        val testDay = kDay.constructors.first().call(testInput)
        val realDay = kDay.constructors.first().call(realInput)
        runDay(testDay, realDay, year = year)
    }
}
