import java.io.File
import kotlin.math.pow

fun main() {
    val adapters = mutableListOf(0)

    File("src/main/resources/dec10.txt").forEachLine { line ->
        adapters.add(line.toInt())
    }

    adapters.sort()
    // phone
    adapters.add(adapters[adapters.size-1] + 3)

    var oneJoltDiff = 0;
    var twoJoltDiff = 0;
    var threeJoltDiff = 0;

    for (i in 1 until adapters.size) {
        println("i = ${adapters[i]}")
        when (adapters[i] - adapters[i-1]) {
            1 -> oneJoltDiff++
            2 -> twoJoltDiff++
            3 -> threeJoltDiff++
            else -> println("unknown diff = ${adapters[i] - adapters[i-1]}")
        }
    }

    println("oneJoltDiff = ${oneJoltDiff}")
    println("twoJoltDiff = ${twoJoltDiff}")
    println("threeJoltDiff = ${threeJoltDiff}")

    println("1 * 3 = ${oneJoltDiff * threeJoltDiff}")

    scan(adapters)
}

fun scan(adapters: List<Int>) {
    val skipMap = mutableMapOf(1 to 1, 2 to 1)
    var streak = 0
    var prevAdapter = -100

    var possibility = 1L

    for (adapter in adapters) {
        if (adapter == prevAdapter + 1) {
            streak++
        } else {
            if (streak > 0) {
                if (!skipMap.containsKey(streak)) {
                    skipMap[streak] = skippableCount(streak)
                }

                println("new possibility streak $streak = ${skipMap[streak]}")
                possibility *= skipMap[streak]!!
            }
            streak = 1
        }
        prevAdapter = adapter
    }

    if (streak > 0) {
        if (!skipMap.containsKey(streak)) {
            skipMap[streak] = skippableCount(streak)
        }

        println("new possibility streak $streak = ${skipMap[streak]}")
        possibility *= skipMap[streak]!!
    }

    println("possibility = ${possibility}")
}

private fun skippableCount(length: Int): Int {
    var count = 0
    for (i in 0 until 2.0.pow(length).toInt()) {
        val s = i.toString(2).padStart(length, '0')
        if (s.startsWith('1') && s.endsWith('1') && !s.contains("000")) {
//                println("ok s = ${s} ${i}")
            count++
//        } else {
//                println("xx s = ${s} ${i}")
        }
    }
    println("length ${length}, count = ${count}")
    return count
}
