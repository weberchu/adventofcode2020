import java.io.File

data class ImageTile(
    val id: Int,
    val image: List<String>
)

fun main() {
    val file = File("src/main/resources/dec20.txt")
    var tmpId = 0
    val tmpImage = mutableListOf<String>()

    val imageTiles = mutableListOf<ImageTile>()

    file.forEachLine { line ->
        if (line.startsWith("Tile ")) {
            tmpId = line.substring(5, line.length - 1).toInt()
        } else if (line.isBlank()) {
            imageTiles.add(ImageTile(tmpId, tmpImage.toList()))
            tmpImage.clear()
        } else {
            tmpImage.add(line)
        }
    }

    findCorners(imageTiles)
}

fun findCorners(imageTiles: List<ImageTile>) {
    val idToUniqueEdge = mutableMapOf<Int, MutableSet<String>>()
    val uniqueEdgeToId = mutableMapOf<String, Int>()
    val edgeToId =

    imageTiles.forEach { tile ->
        val id = tile.id
        edges(tile).forEach { edge ->
            if (uniqueEdgeToId.containsKey(edge)) {
                val idWithSameEdge = uniqueEdgeToId.remove(edge)
                idToUniqueEdge[idWithSameEdge]!!.remove(edge)
            } else if (uniqueEdgeToId.containsKey(edge.reversed())) {
                val reversedEdge = edge.reversed()
                val idWithSameEdge = uniqueEdgeToId.remove(reversedEdge)
                idToUniqueEdge[idWithSameEdge]!!.remove(reversedEdge)
            } else {
                uniqueEdgeToId[edge] = id
                idToUniqueEdge.putIfAbsent(id, mutableSetOf())
                idToUniqueEdge[id]!!.add(edge)
            }
        }
    }

    var edgeProduct = 1L
    idToUniqueEdge
        .filter { it.value.size == 2 }
        .forEach { (id, _) ->
            println("id = ${id}")
            edgeProduct *= id
        }

    println("edgeProduct = ${edgeProduct}")
}

fun edges(imageTile: ImageTile): List<String> {
    val left = imageTile.image.map { it.first() }.joinToString(separator = "")
    val right = imageTile.image.map { it.last() }.joinToString(separator = "")
    val top = imageTile.image.first()
    val bottom = imageTile.image.last()
    return listOf(top, bottom, left, right)
}
