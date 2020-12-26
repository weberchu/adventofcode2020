import java.io.File

fun main() {
    var allSeats = mutableListOf<Int>()

    File("src/main/resources/dec05.txt").forEachLine {
        val row = it.substring(0, 7)
                .replace('B', '1')
                .replace('F', '0')
                .toInt(2)
        val column = it.substring(7)
                .replace('L', '0')
                .replace('R', '1')
                .toInt(2)

        val seatId = row * 8 + column
        allSeats.add(seatId)

//        println("row = ${row} ${column} ${seatId}")
    }

    val sortedSeatIds = allSeats.sorted()
    println("max seat id = ${sortedSeatIds[sortedSeatIds.size - 1]}")

    var prev = sortedSeatIds[0] - 1;
    sortedSeatIds.forEach {
        if (it != prev + 1) {
            println("missing = ${it - 1}")
        }
        prev = it;
    }

}