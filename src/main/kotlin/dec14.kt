import java.io.File
import kotlin.math.pow

fun main() {
    val instructions = File("src/main/resources/dec14.txt").readLines()

    version1(instructions)
    version2(instructions)
}

private fun version1(instructions: List<String>) {
    println()
    println("********")
    println("version1")
    println("********")
    val memory = mutableMapOf<Long, Long>()
    var andMask = -1L
    var orMask = -1L

    instructions.forEach { instruction ->
        val split = instruction.split(" = ")
        val command = split[0]
        val value = split[1]

        if (command == "mask") {
            andMask = value.replace('X', '1').toLong(2)
            orMask = value.replace('X', '0').toLong(2)
        } else {
            val address = command.substring(4, command.length - 1).toLong()
            memory[address] = if (andMask == -1L) {
                value.toLong()
            } else {
                value.toLong().and(andMask).or(orMask)
            }
        }
    }

    println("memory = ${memory}")

    val sum = memory.values.sum()

    println("sum = ${sum}")
}

private fun version2(instructions: List<String>) {
    println()
    println("********")
    println("version2")
    println("********")
    val memory = mutableMapOf<Long, Long>()
    var mask = ""
    val allZeros = "000000000000000000000000000000000000"
    val allOnes = "111111111111111111111111111111111111"

    instructions.forEach { instruction ->
        val split = instruction.split(" = ")
        val command = split[0]
        val value = split[1]

        println("instruction = ${instruction}")

        if (command == "mask") {
            mask = value
        } else {
            val address = command.substring(4, command.length - 1).toLong()
            if (mask == "") {
                println("no mask")
                memory[address] = value.toLong()
            } else {
                val orMask = mask.replace('X', '0').toLong(2)
                val xPositions = mask.toCharArray()
                    .mapIndexed { index, c -> Pair(index, c) }
                    .filter { it.second == 'X' }
                for (x in 0 until 2.0.pow(xPositions.size).toInt()) {
                    var modifiedAddress = address.or(orMask)
                    val xReplacements = x.toString(2).padStart(xPositions.size, '0')
                    xPositions.forEachIndexed { index, xPosition ->
                        val positionToReplace = xPosition.first
                        val bitToReplaceWith = xReplacements[index]
                        if (bitToReplaceWith == '1') {
                            val xMask = allZeros.replaceRange(
                                positionToReplace,
                                positionToReplace+1,
                                "1"
                            ).toLong(2)

                            modifiedAddress = modifiedAddress.or(xMask)
                        } else {
                            val xMask = allOnes.replaceRange(
                                positionToReplace,
                                positionToReplace+1,
                                "0"
                            ).toLong(2)
                            modifiedAddress = modifiedAddress.and(xMask)
                        }
                    }
                    memory[modifiedAddress] = value.toLong()
                    println("modifiedAddress ${modifiedAddress.toString(2)} = ${value.toLong()}")
                }
            }
        }

//        println("memory = ${memory}")
    }

    println("memory = ${memory}")

    val sum = memory.values.sum()

    println("sum = ${sum}")
}
