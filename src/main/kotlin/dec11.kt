import java.io.File

const val TOLERANT_THRESHOLD = 5

fun main() {
    var seatingMap = mutableListOf<MutableList<Char>>()

    File("src/main/resources/dec11.txt").forEachLine { line ->
        seatingMap.add(line.toCharArray().toMutableList())
    }

    var hasAnythingChanged: Boolean
    var iteration = 0
    do {
        val result = peopleArrive(seatingMap)
        seatingMap = result.first
        hasAnythingChanged = result.second

        printMap(seatingMap)
        iteration++
        println("${iteration} occupied = ${result.third}")
    } while (hasAnythingChanged)
}

fun printMap(seatingMap: MutableList<MutableList<Char>>) {
    println("-------")
    println("map")
    println("-------")
    seatingMap.forEach {
        println(it.joinToString(separator = ""))
    }
}

fun peopleArrive(seatingMap: List<List<Char>>): Triple<MutableList<MutableList<Char>>, Boolean, Int> {
    val updatedMap = mutableListOf<MutableList<Char>>()
    var isUpdated = false
    var occupied = 0
    for (x in seatingMap.indices) {
        updatedMap.add(mutableListOf())
        for (y in seatingMap[x].indices) {
            when (seatingMap[x][y]) {
                'L' -> {
                    if (countSeeableAdjacentSeat(seatingMap, x, y) == 0) {
                        updatedMap[x].add('#')
                        isUpdated = true
                        occupied++
                    } else {
                        updatedMap[x].add('L')
                    }
                }
                '#' -> {
                    if (countSeeableAdjacentSeat(seatingMap, x, y) >= TOLERANT_THRESHOLD) {
                        updatedMap[x].add('L')
                        isUpdated = true
                    } else {
                        updatedMap[x].add('#')
                        occupied++
                    }
                }
                else -> {
                    updatedMap[x].add(seatingMap[x][y])
                }
            }
        }
    }

    return Triple(updatedMap, isUpdated, occupied)
}

private val adjacentDirection = listOf(
    listOf(1, 1),
    listOf(1, 0),
    listOf(1, -1),
    listOf(0, 1),
    listOf(0, -1),
    listOf(-1, 1),
    listOf(-1, 0),
    listOf(-1, -1)
)

fun countOccupiedAdjacentSeat(seatingMap: List<List<Char>>, x: Int, y: Int): Int {
    var count = 0
    for ((i, j) in adjacentDirection) {
        if (x + i >= 0 && x + i < seatingMap.size
            && y + j >= 0 && y + j < seatingMap[x].size
            && seatingMap[x + i][y + j] == '#'
        ) {
            count++
        }
    }
    return count
}

fun countSeeableAdjacentSeat(seatingMap: List<List<Char>>, x: Int, y: Int): Int {
    var count = 0
    for ((i, j) in adjacentDirection) {
        var distance = 1
        var shouldStop = false
        do {
            if (x + distance * i >= 0 && x + distance * i < seatingMap.size
                && y + distance * j >= 0 && y + distance * j < seatingMap[x].size
            ) {
                when (seatingMap[x + distance * i][y + distance * j]) {
                    'L' -> shouldStop = true
                    '#' -> {
                        count++
                        shouldStop = true
                    }
                }
            } else {
                shouldStop = true
            }
            distance++
        } while (!shouldStop)
    }
    return count
}

