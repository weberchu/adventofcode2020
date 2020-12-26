import java.io.File
import kotlin.math.ceil

fun main() {
    val file = File("src/main/resources/dec13.txt")
    val lines = file.readLines()

    val earliestCanDepart = lines[0].toInt()
    val buses = lines[1].split(',')
        .filter { it != "x" }
        .map { it.toInt() }

    val busSchedules = lines[1].split(',')

    findNextBus(earliestCanDepart, buses)
    findFunTime(busSchedules)
}

fun findFunTime(busSchedules: List<String>) {
    val funSchedules = busSchedules.mapIndexedNotNull { index, bus ->
        if (bus == "x") {
            null
        } else {
            Pair(bus.toLong(), index)
        }
    }

    println("funSchedules = ${funSchedules}")

    var commonT = 1L
    var step = 1L
    funSchedules.forEach { funSchedule ->
        if (commonT == 1L) {
            commonT = funSchedule.first - funSchedule.second
            step = funSchedule.first
        } else {
            var nextPotentialCommonT = commonT
            while ((nextPotentialCommonT + funSchedule.second) % funSchedule.first != 0L) {
                nextPotentialCommonT += step
            }

            commonT = nextPotentialCommonT
            step *= funSchedule.first
        }
        println("funSchedule = ${funSchedule} ${commonT}. step = ${step}")
    }

    println("commonT = ${commonT}")
}

fun findNextBus(earliestCanDepart: Int, buses: List<Int>) {
    val earliestBus = buses
        .map {
            // find next departure time for this bus
            val nextDepartureTime = ceil(earliestCanDepart.toDouble() / it).toInt() * it
            println("${it} nextDepartureTime = ${nextDepartureTime}")
            Pair(it, nextDepartureTime)
        }
        .reduceRight { i, acc ->
            if (i.second < acc.second) {
                i
            } else {
                acc
            }
        }

    println("earliestBus = ${earliestBus}")

    println("result = ${(earliestBus.second - earliestCanDepart) * earliestBus.first}")
}
