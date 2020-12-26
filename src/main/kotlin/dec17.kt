import java.io.File

data class GridState3D(
    val xRange: Pair<Int, Int>,
    val yRange: Pair<Int, Int>,
    val zRange: Pair<Int, Int>,
    val grid: Map<Triple<Int, Int, Int>, Char>
)

data class FourDCoordinate(
    val x: Int,
    val y: Int,
    val z: Int,
    val w: Int
)

data class GridState4D(
    val xRange: Pair<Int, Int>,
    val yRange: Pair<Int, Int>,
    val zRange: Pair<Int, Int>,
    val wRange: Pair<Int, Int>,
    val grid: Map<FourDCoordinate, Char>
)

val OFFSETS = listOf(-1, 0, 1)

fun main() {
    main4D()
}

fun main3D() {
    var xRange = Pair(0, 0)
    var yRange = Pair(0, 0)
    val zRange = Pair(0, 0)
    val grid = mutableMapOf<Triple<Int, Int, Int>, Char>() // (x,y,z) coordinate -> state

    val file = File("src/main/resources/dec17.txt")
    var y = 0
    file.forEachLine {
        it.forEachIndexed { index, char ->
            grid[Triple(index, y, 0)] = char
        }

        xRange = Pair(0, it.length - 1)
        yRange = Pair(0, y++)
    }

    val initialState = GridState3D(xRange, yRange, zRange, grid)

    println("initialState = ${initialState}")
    printState3D(initialState)

    var state3D: GridState3D = initialState
    for (i in 1..6) {
        println("### Cycle = ${i}")
        state3D = cycleState3D(state3D)
        printState3D(state3D)
    }

    val activeCubes = countActiveCubes3D(state3D.grid)
    println("activeCubes = ${activeCubes}")

}

fun countActiveCubes3D(grid: Map<Triple<Int, Int, Int>, Char>): Int {
    return grid.values.count { it == '#' }
}

fun printState3D(state3D: GridState3D) {
    for (z in state3D.zRange.first..state3D.zRange.second) {
        println("z = ${z}")
        for (y in state3D.yRange.first..state3D.yRange.second) {
            for (x in state3D.xRange.first..state3D.xRange.second) {
                print(state3D.grid[Triple(x, y, z)])
            }
            println()
        }
    }
}

fun cycleState3D(initialState3D: GridState3D): GridState3D {
    val xRange = initialState3D.xRange.let { Pair(it.first - 1, it.second + 1) }
    val yRange = initialState3D.yRange.let { Pair(it.first - 1, it.second + 1) }
    val zRange = initialState3D.zRange.let { Pair(it.first - 1, it.second + 1) }
    val newGrid = mutableMapOf<Triple<Int, Int, Int>, Char>()

    for (x in xRange.first..xRange.second) {
        for (y in yRange.first..yRange.second) {
            for (z in zRange.first..zRange.second) {
                val activeNeighbour = countActiveNeighbour3D(initialState3D.grid, x, y, z)
                val newCubeState = if (initialState3D.grid[Triple(x, y, z)] == '#') {
                    if (activeNeighbour == 2 || activeNeighbour == 3) {
                        '#'
                    } else {
                        '.'
                    }
                } else {
                    if (activeNeighbour == 3) {
                        '#'
                    } else {
                        '.'
                    }
                }
                newGrid[Triple(x, y, z)] = newCubeState
            }
        }
    }

    return GridState3D(xRange, yRange, zRange, newGrid)
}

fun countActiveNeighbour3D(grid: Map<Triple<Int, Int, Int>, Char>, x: Int, y: Int, z: Int): Int {
    var count = 0
    OFFSETS.forEach { xOffset ->
        OFFSETS.forEach { yOffset ->
            OFFSETS.forEach { zOffset ->
                if ((xOffset != 0 || yOffset != 0 || zOffset != 0)
                    && grid[Triple(x + xOffset, y + yOffset, z + zOffset)] == '#'
                ) {
                    count++
                }
            }
        }
    }
    return count
}

fun main4D() {
    var xRange = Pair(0, 0)
    var yRange = Pair(0, 0)
    val zRange = Pair(0, 0)
    val wRange = Pair(0, 0)
    val grid = mutableMapOf<FourDCoordinate, Char>() // (x,y,z,w) coordinate -> state

    val file = File("src/main/resources/dec17.txt")
    var y = 0
    file.forEachLine {
        it.forEachIndexed { index, char ->
            grid[FourDCoordinate(index, y, 0, 0)] = char
        }

        xRange = Pair(0, it.length - 1)
        yRange = Pair(0, y++)
    }

    val initialState = GridState4D(xRange, yRange, zRange, wRange, grid)

    println("initialState = ${initialState}")
    printState4D(initialState)

    var state4D: GridState4D = initialState
    for (i in 1..6) {
        println("### Cycle = ${i}")
        state4D = cycleState4D(state4D)
        printState4D(state4D)
    }

    val activeCubes = countActiveCubes4D(state4D.grid)
    println("activeCubes = ${activeCubes}")

}

fun countActiveCubes4D(grid: Map<FourDCoordinate, Char>): Int {
    return grid.values.count { it == '#' }
}

fun printState4D(state4D: GridState4D) {
    for (w in state4D.wRange.first..state4D.wRange.second) {
        for (z in state4D.zRange.first..state4D.zRange.second) {
            println("z = ${z}, w = $w")
            for (y in state4D.yRange.first..state4D.yRange.second) {
                for (x in state4D.xRange.first..state4D.xRange.second) {
                    print(state4D.grid[FourDCoordinate(x, y, z, w)])
                }
                println()
            }
        }
    }
}

fun cycleState4D(initialState4D: GridState4D): GridState4D {
    val xRange = initialState4D.xRange.let { Pair(it.first - 1, it.second + 1) }
    val yRange = initialState4D.yRange.let { Pair(it.first - 1, it.second + 1) }
    val zRange = initialState4D.zRange.let { Pair(it.first - 1, it.second + 1) }
    val wRange = initialState4D.wRange.let { Pair(it.first - 1, it.second + 1) }
    val newGrid = mutableMapOf<FourDCoordinate, Char>()

    for (x in xRange.first..xRange.second) {
        for (y in yRange.first..yRange.second) {
            for (z in zRange.first..zRange.second) {
                for (w in wRange.first..wRange.second) {
                    val activeNeighbour = countActiveNeighbour4D(initialState4D.grid, x, y, z, w)
                    val newCubeState = if (initialState4D.grid[FourDCoordinate(x, y, z, w)] == '#') {
                        if (activeNeighbour == 2 || activeNeighbour == 3) {
                            '#'
                        } else {
                            '.'
                        }
                    } else {
                        if (activeNeighbour == 3) {
                            '#'
                        } else {
                            '.'
                        }
                    }
                    newGrid[FourDCoordinate(x, y, z, w)] = newCubeState
                }
            }
        }
    }

    return GridState4D(xRange, yRange, zRange, wRange, newGrid)
}


fun countActiveNeighbour4D(grid: Map<FourDCoordinate, Char>, x: Int, y: Int, z: Int, w: Int): Int {
    var count = 0
    OFFSETS.forEach { xOffset ->
        OFFSETS.forEach { yOffset ->
            OFFSETS.forEach { zOffset ->
                OFFSETS.forEach { wOffset ->
                    if ((xOffset != 0 || yOffset != 0 || zOffset != 0 || wOffset != 0)
                        && grid[FourDCoordinate(x + xOffset, y + yOffset, z + zOffset, w + wOffset)] == '#'
                    ) {
                        count++
                    }
                }
            }
        }
    }
    return count
}

