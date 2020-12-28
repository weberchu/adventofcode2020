import java.io.File
import kotlin.math.sqrt

fun main() {
    val file = File("src/main/resources/dec22.txt")
    var isPlayer1 = true
    val player1Deck = mutableListOf<Int>()
    val player2Deck = mutableListOf<Int>()

    file.forEachLine { line ->
        when (line) {
            "Player 1:" -> isPlayer1 = true
            "Player 2:" -> isPlayer1 = false
            "" -> {
            }
            else -> {
                val deck = if (isPlayer1) {
                    player1Deck
                } else {
                    player2Deck
                }

                deck.add(line.toInt())
            }
        }
    }

    println("Playing Combat")
    playCombat(player1Deck, player2Deck)

    println("Playing Recursive Combat")
    val (winner, finalDeck) = playRecursiveCombat(player1Deck, player2Deck)
    println("winner = ${winner}")
    println("finalDeck = ${finalDeck}")
    println("score = ${calculateScore(finalDeck)}")
}

fun playCombat(player1InitDeck: List<Int>, player2InitDeck: List<Int>) {
    val player1Deck = player1InitDeck.toMutableList()
    val player2Deck = player2InitDeck.toMutableList()

    do {
        val card1 = player1Deck.removeAt(0)
        val card2 = player2Deck.removeAt(0)

        if (card1 > card2) {
            // 1 wins this round
            player1Deck.add(card1)
            player1Deck.add(card2)
        } else {
            // 2 wins this round
            player2Deck.add(card2)
            player2Deck.add(card1)
        }


//        println("player1Deck = ${player1Deck}")
//        println("player2Deck = ${player2Deck}")
    } while (player1Deck.size > 0 && player2Deck.size > 0)

    if (player1Deck.size > 0) {
        // 1 win
        println("Player 1 wins")
        val score = calculateScore(player1Deck)
        println("score = ${score}")
    } else {
        // 1 win
        println("Player 2 wins")
        val score = calculateScore(player2Deck)
        println("score = ${score}")
    }

}

fun calculateScore(deck: List<Int>): Int {
    val deckSize = deck.size
    return deck
        .mapIndexed { index, card -> card * (deckSize - index) }
        .sum()
}

enum class Player{
    One, Two
}

val previousCombats = mutableSetOf<List<List<Int>>>()

fun playRecursiveCombat(player1InitDeck: List<Int>, player2InitDeck: List<Int>): Pair<Player, List<Int>> {
    val player1Deck = player1InitDeck.toMutableList()
    val player2Deck = player2InitDeck.toMutableList()


    do {
        val combatSignature = listOf(player1Deck, player2Deck)
        if (previousCombats.contains(combatSignature)) {
            println("previous combat encountered")
            println("combatSignature = ${combatSignature}")
            return Pair(Player.One, player1Deck)
        }
        previousCombats.add(combatSignature)

        val card1 = player1Deck.removeAt(0)
        val card2 = player2Deck.removeAt(0)


        val roundWinner = if (player1Deck.size >= card1 && player2Deck.size >= card2) {
            playRecursiveCombat(
                player1Deck.subList(0, card1),
                player2Deck.subList(0, card2))
                .first
        } else {
            if (card1 > card2) {
                Player.One
            } else {
                Player.Two
            }
        }

        if (roundWinner == Player.One) {
            player1Deck.add(card1)
            player1Deck.add(card2)
        } else {
            player2Deck.add(card2)
            player2Deck.add(card1)
        }

//        println("player1Deck = ${player1Deck}")
//        println("player2Deck = ${player2Deck}")
    } while (player1Deck.size > 0 && player2Deck.size > 0)

    return if (player1Deck.size > 0) {
        Pair(Player.One, player1Deck)
    } else {
        Pair(Player.Two, player2Deck)
    }

}