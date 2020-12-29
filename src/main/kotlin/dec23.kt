import java.io.File
import java.util.*

const val GAME_MOVE_COUNT = 10000000
//const val GAME_MOVE_COUNT = 1000000
//const val GAME_MOVE_COUNT = 10

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

data class Cup(
    val label: Int,
    var next: Cup?
) {
    override fun toString(): String {
        return "Cup(label=$label, next=${next?.label})"
    }
}

const val numOfCups = 1000000

fun playMillionCrabGame(initCups: List<Int>) {
    val cups = mutableMapOf<Int, Cup>()
    for (i in numOfCups downTo initCups.size + 1) {
        cups[i] = Cup(i, cups[i+1])
    }
    var nextCup = cups[initCups.size + 1]
    initCups.reversed().forEach { label ->
        val cup = Cup(label, nextCup)
        cups[label] = cup
        nextCup = cup
    }
    val lastCup = if (numOfCups > initCups.size) {
        numOfCups
    } else {
        initCups.last()
    }
    cups[lastCup]!!.next = nextCup

    var currentCup = cups[initCups[0]]!!
    val toPrint = GAME_MOVE_COUNT < 20

    for (i in 1..GAME_MOVE_COUNT) {
        if (toPrint) {
            println("\nMOVE ${i}")
            println("currentCup = ${currentCup.label}")
            println("cups = ${printMillionCups(cups, 999996)}")
        }

        // remove next 3
        val moveList = listOf(currentCup.next!!, currentCup.next!!.next!!, currentCup.next!!.next!!.next!!)
        currentCup.next = currentCup.next!!.next!!.next!!.next!!

        // find position to insert
        val moveLabels = moveList.map { it.label }
        if (toPrint) {
            println("moveLabels = ${moveLabels}")
        }
        var labelToInsertAfter = currentCup.label
        do {
            labelToInsertAfter--
            if (labelToInsertAfter == 0) {
                labelToInsertAfter = numOfCups
            }
        } while (moveLabels.contains(labelToInsertAfter))

        if (toPrint) {
            println("labelToInsertAfter = ${labelToInsertAfter}")
        }

        // insert
        val cupToInsertAfter = cups[labelToInsertAfter]
        val cupToInsertBefore = cupToInsertAfter!!.next!!
        cupToInsertAfter.next = moveList[0]
        moveList[2].next = cupToInsertBefore

        currentCup = currentCup.next!!
    }

    println("final cups = ${printMillionCups(cups, 999996)}")

    // 2 cups after 1
    val cup1 = cups[1]!!

    println("next1 = ${cup1.next!!}")
    println("next2 = ${cup1.next!!.next!!}")
    println("product = ${cup1.next!!.label.toLong() * cup1.next!!.next!!.label}")
}

fun printMillionCups(cups: Map<Int, Cup>, startLabel: Int): String {
    var print = startLabel.toString()
    var nextLabel = startLabel
    for (i in 1 until 40) {
        val nextCup = cups[nextLabel]!!.next!!
        print += " " + nextCup.label
        nextLabel = nextCup.label
    }
    return print
}