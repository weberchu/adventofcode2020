import java.io.File

fun part1() {
    val currentGroup = mutableSetOf<Char>()

    var total = 0

    File("src/main/resources/dec06.txt").forEachLine { line ->
        if (line.isBlank()) {
            println("currentGroup.size = ${currentGroup.size}")
            total += currentGroup.size
            currentGroup.clear()
        } else {
            currentGroup.addAll(line.toCharArray().toList())
        }
    }
    total += currentGroup.size

    println("total = ${total}")
}

fun main() {

    val currentGroup = mutableMapOf<Char, Int>()
    var groupSize = 0;

    var total = 0

    File("src/main/resources/dec06.txt").forEachLine { line ->
        if (line.isBlank()) {
            val allYesCount = currentGroup.filterValues { it == groupSize }
                    .size
            println("allYesCount = ${allYesCount}")
            total += allYesCount

            currentGroup.clear()
            groupSize = 0
        } else {
            line.forEach { c ->
                if (currentGroup[c] != null) {
                    currentGroup[c] = currentGroup[c]!! + 1
                } else {
                    currentGroup[c] = 1
                }
            }
            groupSize++
        }
    }

    val allYesCount = currentGroup.filterValues { it == groupSize }
            .size
    println("allYesCount = ${allYesCount}")
    total += allYesCount

    println("total = ${total}")
}
