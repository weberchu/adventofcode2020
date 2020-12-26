import java.io.File

fun main() {
    val file = File("src/main/resources/dec15.txt")
    val numbers = file.readLines()[0].split(',').map { it.toInt() }

    playPart1(numbers, 30000000)
}

fun playPart1(numbers: List<Int>, round: Int) {
    val spokenNumbers = mutableMapOf<Int, Int>()
    var lastNumber = -1

    for (turn in 0 until round) {
        val thisNumber = if (turn < numbers.size) {
            numbers[turn]
        } else {
//            println("lastNumber = ${lastNumber}")
//            println("spokenNumbers = ${spokenNumbers}")
            if (spokenNumbers.containsKey(lastNumber)) {
                turn - 1 - spokenNumbers[lastNumber]!!
            } else {
                0
            }
        }

//        println("${turn} = ${lastNumber}")
        spokenNumbers[lastNumber] = turn - 1
        lastNumber = thisNumber
    }

    println("lastNumber = ${lastNumber}")
}
