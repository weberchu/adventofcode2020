import java.io.File
import kotlin.math.sqrt

data class ImageTile(
    val id: Int,
    val image: List<String>
)

data class ImageEdge(
    val left: String,
    val right: String,
    val top: String,
    val bottom: String
)

fun main() {
    val file = File("src/main/resources/dec20.txt")
    var tmpId = 0
    val tmpImage = mutableListOf<String>()

    val imageTiles = mutableMapOf<Int, ImageTile>()

    file.forEachLine { line ->
        if (line.startsWith("Tile ")) {
            tmpId = line.substring(5, line.length - 1).toInt()
        } else if (line.isBlank()) {
            imageTiles[tmpId] = ImageTile(tmpId, tmpImage.toList())
            tmpImage.clear()
        } else {
            tmpImage.add(line)
        }
    }

    val alignedImages = reconstructImage(imageTiles)
    val roughness = findRoughness(alignedImages)
    println("roughness = ${roughness}")
}

fun reconstructImage(imageTiles: Map<Int, ImageTile>): List<List<ImageTile>> {
    val idToUniqueEdge = mutableMapOf<Int, MutableSet<String>>()
    val uniqueEdgeToId = mutableMapOf<String, Int>()
    val edgeToId = mutableMapOf<String, MutableList<Int>>()

    imageTiles.forEach { (id, tile) ->
        edgeList(tile).forEach { edge ->
            val reversedEdge = edge.reversed()

            if (uniqueEdgeToId.containsKey(edge)) {
                val idWithSameEdge = uniqueEdgeToId.remove(edge)
                idToUniqueEdge[idWithSameEdge]!!.remove(edge)
            } else if (uniqueEdgeToId.containsKey(reversedEdge)) {
                val idWithSameEdge = uniqueEdgeToId.remove(reversedEdge)
                idToUniqueEdge[idWithSameEdge]!!.remove(reversedEdge)
            } else {
                uniqueEdgeToId[edge] = id
                idToUniqueEdge.putIfAbsent(id, mutableSetOf())
                idToUniqueEdge[id]!!.add(edge)
            }

            if (edgeToId.containsKey(edge)) {
                edgeToId[edge]!!.add(id)
            } else if (edgeToId.containsKey(reversedEdge)) {
                edgeToId[reversedEdge]!!.add(id)
            } else {
                edgeToId[edge] = mutableListOf(id)
            }
        }
    }

    val cornerIds = idToUniqueEdge.filter { it.value.size == 2 }.keys
    val edgeProduct = cornerIds.fold(1L) { acc, i -> acc * i }

    println("edgeProduct = ${edgeProduct}")

    val numOfSubImagePerEdge = sqrt(imageTiles.size.toDouble()).toInt()

    val alignedTiles = mutableListOf<MutableList<ImageTile>>()
    for (y in 0 until numOfSubImagePerEdge) {
        for (x in 0 until numOfSubImagePerEdge) {
            if (x == 0) {
                alignedTiles.add(mutableListOf())
            }

            val alignedTile = if (x == 0 && y == 0) {
                // first row first column
                val id = cornerIds.first()
                println("${y},${x} = ${id}")
                val firstCornerEdges = idToUniqueEdge[id]!!.toList()
                val firstTile = imageTiles[id]!!


                alignTile(firstTile, firstCornerEdges[0], firstCornerEdges[1], true)
            } else if (y == 0) {
                // rest of first row
                val leftTile = alignedTiles[y][x - 1]
                val leftEdgeToMatch = leftTile.right()

                val ids = edgeToId[leftEdgeToMatch] ?: edgeToId[leftEdgeToMatch.reversed()]
                // filter the aligned one out
                val id = ids?.filter { it != leftTile.id }?.get(0)
                    ?: throw Error("Cannot find matching left edge $leftEdgeToMatch")

                println("${y},${x} = ${id}")

                alignTile(imageTiles[id]!!, leftEdgeToMatch)
            } else if (x == 0) {
                // second row onwards first column
                val topTile = alignedTiles[y - 1][x]
                val topEdgeToMatch = topTile.bottom()

                val ids = edgeToId[topEdgeToMatch] ?: edgeToId[topEdgeToMatch.reversed()]
                // filter the aligned one out
                val id = ids?.filter { it != topTile.id }?.get(0)
                    ?: throw Error("Cannot find matching top edge $topEdgeToMatch")

                println("${y},${x} = ${id}")
                alignTopTile(imageTiles[id]!!, topEdgeToMatch)
            } else {
                // second row onwards non-first column
                val leftTile = alignedTiles[y][x - 1]
                val leftEdgeToMatch = leftTile.right()

                val topTile = alignedTiles[y - 1][x]
                val topEdgeToMatch = topTile.bottom()

                println("leftEdgeToMatch = ${leftEdgeToMatch}")
                println("topEdgeToMatch = ${topEdgeToMatch}")

                val ids = edgeToId[leftEdgeToMatch] ?: edgeToId[leftEdgeToMatch.reversed()]
                println("ids = ${ids}")
                // filter the aligned one out
                val id = ids?.filter { it != leftTile.id }?.get(0)
                    ?: throw Error("Cannot find matching left edge $leftEdgeToMatch")

                println("${y},${x} = ${id}")

                alignTile(imageTiles[id]!!, leftEdgeToMatch, topEdgeToMatch)
            }

//            printTile(alignedTile)

            alignedTiles[y].add(alignedTile)
        }
    }

    return alignedTiles
}

fun alignTile(
    tile: ImageTile,
    leftEdge: String,
    topEdge: String? = null,
    canMatchReversedEdge: Boolean = false
): ImageTile {
    var alignedTile = if (leftEdge == tile.left()) {
        tile
    } else if (leftEdge == tile.left().reversed()) {
        tile.flipVertical()
    } else if (leftEdge == tile.right()) {
        tile.flipHorizontal()
    } else if (leftEdge == tile.right().reversed()) {
        tile.flipVertical().flipHorizontal()
    } else if (leftEdge == tile.bottom()) {
        tile.rotateClockwise()
    } else if (leftEdge == tile.bottom().reversed()) {
        tile.rotateClockwise().flipVertical()
    } else if (leftEdge == tile.top()) {
        tile.rotateAntiClockwise().flipVertical()
    } else if (leftEdge == tile.top().reversed()) {
        println("tile.top().reversed()")
        tile.rotateAntiClockwise()
    } else {
        println("Cannot find left edge $leftEdge")
        tile
    }

    if (topEdge != null) {
        if (!canMatchReversedEdge && topEdge != alignedTile.top()) {
            println("Cannot match top edge  ${topEdge}")
        } else if (canMatchReversedEdge) {
            if (topEdge == alignedTile.top() || topEdge == alignedTile.top().reversed()) {
                // no transform needed
            } else if (topEdge == alignedTile.bottom() || topEdge == alignedTile.bottom().reversed()) {
                alignedTile = alignedTile.flipVertical()
            } else {
                println("Cannot match top edge  ${topEdge}")
            }
        }
    }

    return alignedTile
}

fun alignTopTile(tile: ImageTile, topEdge: String): ImageTile {
    return if (topEdge == tile.top()) {
        tile
    } else if (topEdge == tile.top().reversed()) {
        tile.flipHorizontal()
    } else if (topEdge == tile.bottom()) {
        tile.flipVertical()
    } else if (topEdge == tile.bottom().reversed()) {
        tile.flipVertical().flipHorizontal()
    } else if (topEdge == tile.left()) {
        tile.rotateClockwise().flipHorizontal()
    } else if (topEdge == tile.left().reversed()) {
        tile.rotateClockwise()
    } else if (topEdge == tile.right()) {
        tile.rotateAntiClockwise()
    } else if (topEdge == tile.right().reversed()) {
        tile.rotateAntiClockwise().flipHorizontal()
    } else {
        println("Cannot find top edge $topEdge")
        tile
    }
}

fun ImageTile.flipVertical(): ImageTile {
    return ImageTile(
        this.id,
        this.image.reversed()
    )
}

fun ImageTile.flipHorizontal(): ImageTile {
    return ImageTile(
        this.id,
        this.image.map { it.reversed() }
    )
}

fun ImageTile.rotateClockwise(): ImageTile {
    return ImageTile(
        this.id,
        rotateImageClockwise(image)
    )
}

private fun rotateImageClockwise(image: List<String>): MutableList<String> {
    val newImage = mutableListOf<String>()
    for (i in 0 until image.size) {
        newImage.add(String(image.map { it[i] }.reversed().toCharArray()))
    }
    return newImage
}

fun ImageTile.rotateAntiClockwise(): ImageTile {
    val newImage = mutableListOf<String>()
    for (i in this.image.size - 1 downTo 0) {
        newImage.add(String(this.image.map { it[i] }.toCharArray()))
    }
    return ImageTile(
        this.id,
        newImage
    )
}

fun ImageTile.left(): String {
    return this.image.map { it.first() }.joinToString(separator = "")
}

fun ImageTile.right(): String {
    return this.image.map { it.last() }.joinToString(separator = "")
}

fun ImageTile.top(): String {
    return this.image.first()
}

fun ImageTile.bottom(): String {
    return this.image.last()
}

fun edgeList(imageTile: ImageTile): List<String> {
    return listOf(imageTile.left(), imageTile.right(), imageTile.top(), imageTile.bottom())
}

fun printImage(image: List<String>) {
    image.forEach { println(it) }
}

fun findRoughness(alignedImages: List<List<ImageTile>>): Int {
    val image = removeGaps(alignedImages)

    println("Full image:")
    image.forEach { println(it) }

    val imageWithoutMonsters = removeSeaMonsters(image)

    return imageWithoutMonsters.map { it.count { it == '#' } }.sum()
}

val seaMonster = listOf(
    "                  # ",
    "#    ##    ##    ###",
    " #  #  #  #  #  #   "
)

fun removeSeaMonsters(image: List<String>): List<String> {
    var imageWithoutSeaMonsters = removeSeaMonstersWithoutTransform(image, seaMonster)
    imageWithoutSeaMonsters = removeSeaMonstersWithoutTransform(imageWithoutSeaMonsters, seaMonster.map { it.reversed() })

    // vertical flip
    imageWithoutSeaMonsters = imageWithoutSeaMonsters.reversed()
    imageWithoutSeaMonsters = removeSeaMonstersWithoutTransform(imageWithoutSeaMonsters, seaMonster)
    imageWithoutSeaMonsters = removeSeaMonstersWithoutTransform(imageWithoutSeaMonsters, seaMonster.map { it.reversed() })

    // rotate clockwise
    imageWithoutSeaMonsters = rotateImageClockwise(imageWithoutSeaMonsters)
    imageWithoutSeaMonsters = removeSeaMonstersWithoutTransform(imageWithoutSeaMonsters, seaMonster)
    imageWithoutSeaMonsters = removeSeaMonstersWithoutTransform(imageWithoutSeaMonsters, seaMonster.map { it.reversed() })

    // vertical flip
    imageWithoutSeaMonsters = imageWithoutSeaMonsters.reversed()
    imageWithoutSeaMonsters = removeSeaMonstersWithoutTransform(imageWithoutSeaMonsters, seaMonster)
    imageWithoutSeaMonsters = removeSeaMonstersWithoutTransform(imageWithoutSeaMonsters, seaMonster.map { it.reversed() })

    return imageWithoutSeaMonsters
}

fun removeSeaMonstersWithoutTransform(image: List<String>, seaMonster: List<String>): List<String> {
    println("removeSeaMonstersWithoutTransform")
    printImage(image)

    val imageInProcess = image.toMutableList()

    val regexSeaMonster = seaMonster.map {
        Regex(it.replace(" ", ".").replace("#", "[#O]"))
    }

    for (y in 1 .. image.size - seaMonster.size) {
        val allMatches = regexSeaMonster[1].findAll(image[y])
        allMatches.forEach { bodyMatch ->
//            println("y=${y} bodyMatch = ${bodyMatch.range}")
//            println("-1 = ${image[y-1].substring(bodyMatch.range.start, bodyMatch.range.endInclusive+1)}")
//            println("+1 = ${image[y+1].substring(bodyMatch.range.start, bodyMatch.range.endInclusive+1)}")
            if (regexSeaMonster[0].matches(image[y-1].substring(bodyMatch.range.start, bodyMatch.range.endInclusive+1))
                && regexSeaMonster[2].matches(image[y+1].substring(bodyMatch.range.start, bodyMatch.range.endInclusive+1))) {
                println("sea monster found at y=${y} = ${bodyMatch.range}")

                seaMonster.forEachIndexed { row, str ->
                    str.forEachIndexed { column, char ->
                        if (char == '#') {
                            imageInProcess[y + row - 1] = imageInProcess[y + row - 1].substring(0, bodyMatch.range.start + column) +
                                        "O" +
                                        imageInProcess[y + row - 1].substring(bodyMatch.range.start + column + 1)
//                            imageInProcess[y + row - 1][bodyMatch.range.start + column] = 'O'
                        }
                    }
                }
            }
        }
    }

    return imageInProcess
}

fun removeGaps(alignedImages: List<List<ImageTile>>): List<String> {
    val fullImage = mutableListOf<String>()
    alignedImages.forEach { imageRow ->
        val imageEdge = imageRow[0].image.size
        for (i in 1..imageEdge - 2) {
            fullImage.add(
                imageRow.map { it.image[i].substring(1, imageEdge - 1) }.reduce { acc, s -> acc + s }
            )
        }
    }

    return fullImage
}
