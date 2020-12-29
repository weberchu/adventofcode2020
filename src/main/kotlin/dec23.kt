import java.io.File
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.LinkedHashSet

const val GAME_MOVE_COUNT = 1000000

fun main() {
    val file = File("src/main/resources/dec23.txt")
    val lines = file.readLines()

    val cups = lines[0].toList().map { it.toString().toInt() }

//    playCrabGame(cups)
    playMillionCrabGame(cups)
}

fun playCrabGame(initCups: List<Int>) {
    val numOfCups = initCups.size
    val cups = LinkedList(initCups)
    var currentCupIndex = 0

    for (i in 1..GAME_MOVE_COUNT) {
        println("MOVE ${i}")
        val currentLabel = cups[currentCupIndex]
        println("cups = ${cups}")
        println("currentLabel = ${currentLabel}")

        // remove 3 cups
        val removeList = mutableListOf<Int>()
        for (j in 1..3) {
            if (currentCupIndex < cups.size - 1) {
                removeList.add(cups.removeAt(currentCupIndex + 1))
            } else {
                removeList.add(cups.removeFirst())
                currentCupIndex--
            }
        }
        println("removeList = ${removeList}")
        println("temp cups = ${cups}")

        // find index to insert
        var insertIndex: Int
        var labelToSearch = currentLabel
        do {
            labelToSearch--
            if (labelToSearch == 0) {
                labelToSearch += numOfCups
            }
            insertIndex = cups.indexOf(labelToSearch)
        } while (insertIndex == -1)
        insertIndex++
        println("insertIndex = ${insertIndex}")

        // insert 3 cups
        cups.add(insertIndex, removeList[2])
        cups.add(insertIndex, removeList[1])
        cups.add(insertIndex, removeList[0])

//        println("result cups = ${cups}")
        println()

        if (currentCupIndex >= insertIndex) {
            currentCupIndex += 3
        }
        currentCupIndex = (currentCupIndex + 1) % numOfCups
    }

    println("final cups = ${cups}")

    // sequence after 1
    var sequenceAfter1 = ""
    val oneIndex = cups.indexOf(1)
    for (i in 1 until numOfCups) {
        sequenceAfter1 += cups[(oneIndex + i) % numOfCups].toString()
    }

    println("sequenceAfter1 = ${sequenceAfter1}")
}

val labelsInTheBack = mutableSetOf<Int>()

fun playMillionCrabGame(initCups: List<Int>) {
    val numOfCups = 1000000
    val cups = mutableListOf<Int>()
    cups.addAll(initCups)
    for (i in initCups.size + 1 .. numOfCups) {
        cups.add(i)
    }
    var currentCupIndex = 0

    var timer1 = 0L
    var timer2 = 0L
    var timer3 = 0L
    var timer4 = 0L

    val removeList = mutableListOf<Int>()
    for (i in 1..GAME_MOVE_COUNT) {
//        println("MOVE ${i}")
        if (i % 1000 == 1) {
            println("Time = ${LocalDateTime.now()}")
            println("MOVE ${i}")
            println("timer1 = ${timer1}")
            println("timer2 = ${timer2}")
            println("timer3 = ${timer3}")
            println("timer4 = ${timer4}")
        }
        val currentLabel = cups[currentCupIndex]
//        println("cups = ${printMillionCups(cups)}")
//        println("currentLabel = ${currentLabel}")

        // remove 3 cups
        removeList.clear()
        val start1 = System.currentTimeMillis()
        for (j in 1..3) {
            if (currentCupIndex < cups.size - 1) {
                removeList.add(cups.removeAt(currentCupIndex + 1))
            } else {
                removeList.add(cups.removeFirst())
                currentCupIndex--
            }
        }
        timer1 += System.currentTimeMillis() - start1
//        println("removeList = ${removeList}")
//        println("temp cups = ${printMillionCups(cups)}")

        // find index to insert
        val start2 = System.currentTimeMillis()
        var insertIndex: Int
        var labelToSearch = currentLabel
        do {
            labelToSearch--
            if (labelToSearch == 0) {
                labelToSearch += numOfCups
            }
        } while (removeList.contains(labelToSearch))
        val start4 = System.currentTimeMillis()
        insertIndex = findLabel(cups, labelToSearch)
        insertIndex++
        timer4 += System.currentTimeMillis() - start4
        timer2 += System.currentTimeMillis() - start2

//        println("insertIndex = ${insertIndex}")

        // insert 3 cups
        val start3 = System.currentTimeMillis()
        cups.add(insertIndex, removeList[2])
        cups.add(insertIndex, removeList[1])
        cups.add(insertIndex, removeList[0])

        if (insertIndex > 500000) {
            labelsInTheBack.addAll(removeList)
        }

//        println("result cups = ${printMillionCups(cups)}")
//        println()

        if (currentCupIndex >= insertIndex) {
            currentCupIndex += 3
        }
        currentCupIndex = (currentCupIndex + 1) % numOfCups
        timer3 += System.currentTimeMillis() - start3
    }

    println("final cups = ${printMillionCups(cups)}")

    // 2 cups before 1
    val oneIndex = cups.indexOf(1)
    val beforeOne1 = cups[(oneIndex - 1) + numOfCups % numOfCups]
    val beforeOne2 = cups[(oneIndex - 2) + numOfCups % numOfCups]

    println("beforeOne1 = ${beforeOne1}")
    println("beforeOne2 = ${beforeOne2}")
    println("product = ${beforeOne1.toLong() * beforeOne2}")
}

fun printMillionCups(cups: List<Int>): String {
    return cups.toString()
//    return cups.subList(0, 15).toString() + " ... " + cups.subList(cups.size - 8, cups.size).toString()
}

fun findLabel(cups: List<Int>, labelToSearch: Int): Int {
//    println("labelToSearch = ${labelToSearch}")
//    println("cups = ${printMillionCups(cups)}")
    if (labelsInTheBack.contains(labelToSearch)) {
//        println("backward")
        for (i in cups.size - 1 downTo 0) {
            if (cups[i] == labelToSearch) {
                return i
            }
        }
        return -1
    } else {
//        println("forward")
        return cups.indexOf(labelToSearch)
    }
}