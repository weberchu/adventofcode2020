import java.io.File

enum class GridType {
    Empty, Tree
}

fun main() {
    val map = mutableListOf<List<GridType>>()
    File("src/main/resources/dec03.txt").forEachLine {
        map.add(it.toCharArray().map { c ->
            if (c == '.') {
                GridType.Empty
            } else {
                GridType.Tree
            }
        })
    }

    var product = 1L
    for (slope in listOf(
            listOf(1, 1),
            listOf(3, 1),
            listOf(5, 1),
            listOf(7, 1),
            listOf(1, 2)
    )) {
        val treeHit = calcTreeHit(map, slope[0], slope[1])
        println("treeHit = ${treeHit}")
        product *= treeHit
    }

    println("product = ${product}")
}

private fun calcTreeHit(map: MutableList<List<GridType>>, right: Int, down: Int): Int {
    val distance = map.size
    val mapWidth = map[0].size
    var x = 0
    var y = 0
    var treeHit = 0

//    println("distance = ${distance}")


    do {
        if (map[y][x] == GridType.Tree) {
            treeHit++
        }
        x = (x + right) % mapWidth
        y += down
    } while (y < distance)

    return treeHit
}