import java.io.File

data class FieldRule(
    val name: String,
    val range: List<Pair<Int, Int>>
)

fun main() {
    val file = File("src/main/resources/dec16.txt")
    val allLines = file.readLines()

    val fieldRules = mutableListOf<FieldRule>()
    var myTicket: List<Int> = emptyList()
    var nearbyTickets = mutableListOf<List<Int>>()

    var section = 1 // 1: field rules, 2: my ticket, 3: nearby tickets

    allLines.forEach { line ->
        if (line == "your ticket:") {
            section = 2
        } else if (line == "nearby tickets:") {
            section = 3
        } else if (line.isNotBlank()) {
            when (section) {
                1 -> fieldRules.add(parseFieldRule(line))
                2 -> myTicket = parseTicket(line)
                3 -> nearbyTickets.add(parseTicket(line))
            }
        }
    }

    val validTickets = getValidTickets(fieldRules, nearbyTickets)
    println("validTickets = ${validTickets}")

    val fieldNames = determineFields(fieldRules, validTickets + listOf(myTicket))
    println("fieldNames = ${fieldNames}")

    println("myTicket = ${myTicket}")

    val departureProduct = fieldNames
        .mapIndexed { index, fieldName ->
            if (fieldName.startsWith("departure")) {
                myTicket[index].toLong()
            } else {
                1L
            }
        }
        .fold(1L) { acc, value -> acc * value }

    println("departureProduct = ${departureProduct}")
}

fun determineFields(fieldRules: List<FieldRule>, tickets: List<List<Int>>): List<String> {
    val possibilities = fieldRules.map {
        fieldRules.toMutableSet()
    }.toMutableList()

    tickets.forEach { ticket ->
//        println("possibilities = ${possibilities}")
        ticket.forEachIndexed { index, value ->
            possibilities[index] = possibilities[index].filter { rule ->
                isValid(rule, value)
            }.toMutableSet()
        }
    }

//    println("possibilities = ${possibilities}")

//    println("phase 2")

    while (possibilities.any { it.size > 1 }) {
        val determinedFields = possibilities.filter { it.size == 1 }.map { it.toList()[0] }
        possibilities.filter { it.size > 1 }.forEach { it.removeAll(determinedFields) }

//        println("possibilities = ${possibilities}")
    }

    return possibilities.flatten().map { it.name }
}

fun isValid(rule: FieldRule, value: Int): Boolean {
    return rule.range.any { range ->
        value >= range.first && value <= range.second
    }
}

fun parseTicket(line: String): List<Int> {
    return line.split(",").map { it.toInt() }
}

fun parseFieldRule(line: String): FieldRule {
    val split = line.split(": ")
    val name = split[0]

    val rules = split[1]
        .split(" or ")
        .map {
            val range = it.split("-")
            Pair(range[0].toInt(), range[1].toInt())
        }

    return FieldRule(name, rules)
}

fun getValidTickets(fieldRules: List<FieldRule>, tickets: List<List<Int>>): List<List<Int>> {
    var errorRate = 0
    val validTickets = tickets.filter { ticket ->
        ticket.all { field ->
            val fieldMatchingAnyRule = fieldRules.any { rule ->
                isValid(rule, field)
            }
            if (!fieldMatchingAnyRule) {
                errorRate += field
                println("field ${field} of ${ticket} does not match any rules")
            }

            fieldMatchingAnyRule
        }
    }

    println("errorRate = ${errorRate}")

    return validTickets
}
