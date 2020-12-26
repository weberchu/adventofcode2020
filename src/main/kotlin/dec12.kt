import java.io.File
import kotlin.math.abs

private data class Instruction12(
    val command: Char,
    val magnitude: Int
)

private enum class Direction(val moveX: Int, val moveY: Int) {
    North(0, 1),
    East(1, 0),
    South(0, -1),
    West(-1, 0)
}

private val clockwiseList = listOf(Direction.North, Direction.East, Direction.South, Direction.West)

fun main() {
    val instructions = mutableListOf<Instruction12>()

    File("src/main/resources/dec12.txt").forEachLine { line ->
        instructions.add(
            Instruction12(
                line.get(0),
                line.substring(1).toInt()
            )
        )
    }

    simpleMove(instructions)
    waypointMove(instructions)
}

private fun simpleMove(instructions: MutableList<Instruction12>) {
    var currDirection = Direction.East
    var currentX = 0
    var currentY = 0

    instructions.forEach {
        println("it = ${it}")
        when (it.command) {
            'N' -> currentY += it.magnitude
            'S' -> currentY -= it.magnitude
            'E' -> currentX += it.magnitude
            'W' -> currentX -= it.magnitude
            'R' -> currDirection = clockwiseList[(clockwiseList.indexOf(currDirection) + it.magnitude / 90) % 4]
            'L' -> currDirection = clockwiseList[(clockwiseList.indexOf(currDirection) - it.magnitude / 90 + 4) % 4]
            'F' -> {
                currentX += currDirection.moveX * it.magnitude
                currentY += currDirection.moveY * it.magnitude
            }
        }
        println("current = ${currentX}:${currentY}")
    }

    println("Manhattan distance = ${abs(currentX) + abs(currentY)}")
}

private fun waypointMove(instructions: MutableList<Instruction12>) {
    var shipX = 0
    var shipY = 0
    var waypointX = 10
    var waypointY = 1

    instructions.forEach {
        println("it = ${it}")
        when (it.command) {
            'N' -> waypointY += it.magnitude
            'S' -> waypointY -= it.magnitude
            'E' -> waypointX += it.magnitude
            'W' -> waypointX -= it.magnitude
            'R' ->  {
                for (i in 1 .. it.magnitude/90) {
                    val prevWaypointX = waypointX
                    waypointX = waypointY
                    waypointY = prevWaypointX * -1
                }
            }
            'L' -> {
                for (i in 1 .. it.magnitude/90) {
                    val prevWaypointX = waypointX
                    waypointX = waypointY * -1
                    waypointY = prevWaypointX
                }
            }
            'F' -> {
                shipX += waypointX * it.magnitude
                shipY += waypointY * it.magnitude
            }
        }
        println("current = ${shipX}:${shipY}")
    }

    println("Manhattan distance = ${abs(shipX) + abs(shipY)}")
}
