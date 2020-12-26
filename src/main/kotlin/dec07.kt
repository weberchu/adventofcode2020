import java.io.File

fun main() {
    val rules = mutableMapOf<String, Map<String, Int>>()

    File("src/main/resources/dec07.txt").forEachLine { line ->
        val colour = line.substringBefore(" bags contain ")
        val ruleStr = line.substringAfter(" bags contain ")
        val rule = if (ruleStr == "no other bags.") {
            emptyMap()
        } else {
            ruleStr.split(", ")
                    .map { it.substringBefore(" bag") }
                    .map {
                        it.substringAfter(' ') to it.substringBefore(' ').toInt()
                    }
                    .toMap()
        }

        rules[colour] = rule
    }

    val possibleOuterBags = findAllPossibleOuterBags(rules, "shiny gold")

    println("possibleOuterBags = ${possibleOuterBags}")
    println("possibleOuterBags.size = ${possibleOuterBags.size}")

    val countInnerBags = countInnerBags(rules, "shiny gold")
    println("countInnerBags = ${countInnerBags}")
}

fun countInnerBags(rules: Map<String, Map<String, Int>>, colour: String): Int {
    if (rules[colour]!!.size == 0) {
        return 0;
    }

    return rules[colour]!!.map {
        val subColour = it.key
        val count = it.value
        count + count * countInnerBags(rules, subColour)
    }.reduce { acc, i -> acc + i }
}

fun findAllPossibleOuterBags(rules: Map<String, Map<String, Int>>, colour: String): Set<String> {
    val firstLevelOuterBags = possibleOuterBags(rules, colour)
    println("firstLevelOuterBags = ${colour} ${firstLevelOuterBags}")
    val allPossibleOuterBags = firstLevelOuterBags
            .map { findAllPossibleOuterBags(rules, it) }
            .flatten()
            .toSet()
    println("all = ${colour} ${firstLevelOuterBags + allPossibleOuterBags}")

    return firstLevelOuterBags + allPossibleOuterBags
}

fun possibleOuterBags(rules: Map<String, Map<String, Int>>, colour: String): Set<String> {
    return rules.filter { it.value.containsKey(colour) }
            .keys
}