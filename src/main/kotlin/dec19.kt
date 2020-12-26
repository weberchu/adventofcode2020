import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

data class MessageRule(
    val extentions: List<List<Int>>? = null,
    val requiredChar: Char? = null
)

fun main() {
    val isPart2 = true
    val rules = mutableMapOf<Int, MessageRule>()

    val file = File("src/main/resources/dec19.txt")
    var isProcessingRule = true
    var validRules = AtomicInteger(0)

    runBlocking {
        file.forEachLine { line ->
            println("line = ${line}")
            if (line.isBlank()) {
                isProcessingRule = false
                println("rules = ${rules}")

                if (isPart2) {
                    // replace rule 8 and 11
                    rules[8] = MessageRule(
                        extentions = listOf(
                            listOf(42),
                            listOf(42, 8)
                        )
                    )
                    rules[11] = MessageRule(
                        extentions = listOf(
                            listOf(42, 31),
                            listOf(42, 11, 31)
                        )
                    )
                }

            } else if (isProcessingRule) {
                val split = line.split(": ")
//            println("split = ${split}")
                val messageRule = if (split[1].startsWith("\"")) {
                    MessageRule(requiredChar = split[1][1])
                } else {
                    MessageRule(
                        extentions = split[1].split("|")
                            .map { it.split(" ").filter { it.isNotBlank() }.map { it.toInt() } })
                }
                rules[split[0].toInt()] = messageRule
            } else {
                launch {
                    println("evaluating line = ${line}")
                    val message = line.toList().toMutableList()
                    if (findRemainingMessages(rules, 0, setOf(message), emptyList()).any { it.isEmpty() }) {
                        println("isValid $line")
                        validRules.incrementAndGet()
                        println("validRules = ${validRules.get()}")
                    }
                    println("done")
                }
            }
        }
    }

    println("validRules = ${validRules.get()}")
}

fun findRemainingMessages(rules: Map<Int, MessageRule>, ruleNumber: Int, messages: Set<List<Char>>, ruleChain: List<Int>): Set<List<Char>> {
    if (messages.isEmpty() /*|| ruleChain.size >= messages.size*/) {
        return emptySet()
    }
    val remainingMessages = mutableSetOf<List<Char>>()
//    val padding = (ruleChain + ruleNumber).map { it.toString().padStart(2, '.') }.joinToString(separator = ">") + " "
//    println("${padding}checking ruleNumber = ${ruleNumber}, message = $messages")
    val thisRule = rules[ruleNumber]

    messages.forEach { message ->
        if (thisRule!!.requiredChar != null) {
            val isCharMatched = message.isNotEmpty() && message[0] == thisRule.requiredChar
//            println("${padding}isValidChar = ${isCharMatched}")
            if (isCharMatched) {
                remainingMessages.add(message.subList(1, message.size))
            }
        } else {
            thisRule.extentions!!.forEach { extensionRule ->
                if (extensionRule.size <= message.size) {
//                println("${padding}extensionRule = ${extensionRule}")
                    var tempRemainingMessages: Set<List<Char>> = messages
                    val allMatched = extensionRule.all {
                        tempRemainingMessages =
                            findRemainingMessages(rules, it, tempRemainingMessages, ruleChain + ruleNumber)
//                    println("tempRemainingMessages = ${tempRemainingMessages}")
                        tempRemainingMessages.isNotEmpty()
                    }

                    if (allMatched) {
                        remainingMessages.addAll(tempRemainingMessages)
//                    println("${padding}setting remainingMessages = ${remainingMessages}")
                    }
                }
            }

//            println("${padding}isValidMessage = ${remainingMessages.isNotEmpty()}")
//            println("${padding}remainingMessages = ${remainingMessages}")
        }
    }

    return remainingMessages
}
