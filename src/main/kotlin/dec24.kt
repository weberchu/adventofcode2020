import java.io.File

fun main() {
    val file = File("src/main/resources/dec24.txt")
    val instructions = file.readLines()

    val blackTiles = mutableSetOf<Pair<Int, Int>>()

    instructions.forEach { instruction ->
        val destination = findDestination(instruction)
        if (blackTiles.contains(destination)) {
            blackTiles.remove(destination)
        } else {
            blackTiles.add(destination)
        }
    }

//    println("blackTiles = ${blackTiles}")
    println("Day 0 black tiles = ${blackTiles.size}")

    flipFor100Days(blackTiles)
}

/**
 * Represents the hex with 2D array
 * Starting point at (0, 0)
 */
fun findDestination(instruction: String): Pair<Int, Int> {
    var currentDirection = ""
    var x = 0
    var y = 0
    instruction.forEach { char ->
        currentDirection += char

        when (currentDirection) {
            "e", "se", "sw", "w", "nw", "ne" -> {
                val destination = move(x, y, currentDirection)
                x = destination.first
                y = destination.second
                currentDirection = ""
            }
        }
    }

    return Pair(x, y)
}

fun move(x: Int, y: Int, direction: String): Pair<Int, Int> {
    var newX = x
    var newY = y
    when (direction) {
        "e" -> {
            newX++
            if (x % 2 != 0) {
                newY++
            }
        }
        "se" -> {
            newX++
            if (x % 2 == 0) {
                newY--
            }
        }
        "sw" -> newY--
        "w" -> {
            newX--
            if (x % 2 == 0) {
                newY--
            }
        }
        "nw" -> {
            newX--
            if (x % 2 != 0) {
                newY++
            }
        }
        "ne" -> newY++
        else -> throw Error("unknown direction $direction")
    }
    return Pair(newX, newY)
}

fun flipFor100Days(initialTiles: Set<Pair<Int, Int>>) {
    var blackTiles = initialTiles.toMutableSet()
    for (i in 1..100) {
        var maxX = 0
        var maxY = 0
        var minX = 0
        var minY = 0
        blackTiles.forEach { (x, y) ->
            if (x > maxX) {
                maxX = x
            }
            if (y > maxY) {
                maxY = y
            }
            if (x < minX) {
                minX = x
            }
            if (y < minY) {
                minY = y
            }
        }

//        println("x = [$minX - $maxX] y = [$minY - $maxY]")

        val nextDayBlackTiles = mutableSetOf<Pair<Int, Int>>()
        for (x in minX - 1..maxX + 1) {
            for (y in minY - 1..maxY + 1) {
                val adjacentBlackTileCount = countAdjacentBlackTiles(blackTiles, x, y)

                if (blackTiles.contains(Pair(x, y))) {
                    // today is black
                    if (!(adjacentBlackTileCount == 0 || adjacentBlackTileCount > 2)) {
                        // keep it black
                        nextDayBlackTiles.add(Pair(x, y))
                    }
                    // otherwise flip to white means store nothing in new tiles set
                } else {
                    // today is white
                    if (adjacentBlackTileCount == 2) {
                        // flip it black
                        nextDayBlackTiles.add(Pair(x, y))
                    }
                }
            }
        }
//        println("nextDayBlackTiles = ${nextDayBlackTiles}")
        println("Day ${i} = ${nextDayBlackTiles.size}")

        blackTiles = nextDayBlackTiles
    }
}

val allDirections = listOf(
    "e", "se", "sw", "w", "nw", "ne"
)

fun countAdjacentBlackTiles(blackTiles: Set<Pair<Int, Int>>, x: Int, y: Int): Int {
    return allDirections
        .filter { direction ->
            blackTiles.contains(move(x, y, direction))
        }
        .count()
}
