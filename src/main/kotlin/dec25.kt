fun main() {
//    val cardPublicKey = 5764801L
//    val doorPublicKey = 17807724L
    val cardPublicKey = 3469259L
    val doorPublicKey = 13170438L

    val (cardLoopSize, doorLoopSize) = findLoopSize(cardPublicKey, doorPublicKey)
    println("cardLoopSize = ${cardLoopSize}")
    println("doorLoopSize = ${doorLoopSize}")

    if (cardLoopSize < doorLoopSize) {
        val encryptionKey = transform(doorPublicKey, cardLoopSize)
        println("encryptionKey = ${encryptionKey}")
    } else {
        val encryptionKey = transform(cardPublicKey, doorLoopSize)
        println("encryptionKey = ${encryptionKey}")
    }
}

fun findLoopSize(cardPublicKey: Long, doorPublicKey: Long): Pair<Int, Int> {
    val subjectNumber = 7L
    var cardLoopSize = -1
    var doorLoopSize = -1

    var loopResult = 1L
    var loopSize = 0
    do {
        loopResult = performLoop(loopResult, subjectNumber)
        loopSize++

        if (cardPublicKey == loopResult) {
            cardLoopSize = loopSize
        }
        if (doorPublicKey == loopResult) {
            doorLoopSize = loopSize
        }

    } while (cardLoopSize == -1 || doorLoopSize == -1)

    return Pair(cardLoopSize, doorLoopSize)
}

fun transform(subjectNumber: Long, loopSize: Int): Long {
    var result = 1L
    for (i in 1..loopSize) {
        result = performLoop(result, subjectNumber)
    }
    return result
}

private fun performLoop(input: Long, subjectNumber: Long): Long {
    return (input * subjectNumber) % 20201227
}
