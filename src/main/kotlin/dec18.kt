import java.io.File

enum class Operator {
    Plus, Times
}

fun main() {
    val file = File("src/main/resources/dec18.txt")
    val lines = file.readLines()

    val sum = lines
        .map { evaluate2(it.split(' ')) }
        .sum()

    println("sum = ${sum}")
}

fun evaluate1(expression: List<String>): Long {
    var runningResult = 0L
    var operator = Operator.Plus
    val expressionStack = expression.toMutableList()

    println("expression = ${expressionStack}")

    while (expressionStack.size > 0) {
        val nextToken = expressionStack.removeAt(0)

        if (nextToken == "+") {
            operator = Operator.Plus
        } else if (nextToken == "*") {
            operator = Operator.Times
        } else {
            val operatingValue = when {
                nextToken.toLongOrNull() != null -> {
                    nextToken.toLong()
                }
                nextToken.startsWith("(") -> {
                    var openBracketCount = 0
                    val bracketedExpresion = mutableListOf<String>()
                    var bracketedNextToken = nextToken
                    do {
//                        println("bracketedNextToken = ${bracketedNextToken}")
                        if (bracketedNextToken.startsWith("(")) {
                            openBracketCount += bracketedNextToken.count { it == '(' }
                        } else if (bracketedNextToken.endsWith(")")) {
                            openBracketCount -= bracketedNextToken.count { it == ')' }
                        }

                        bracketedExpresion.add(bracketedNextToken)

                        if (openBracketCount > 0) {
                            bracketedNextToken = expressionStack.removeAt(0)
                        }
                    } while (openBracketCount > 0)

                    // remove '(' from first and ')' from last
                    bracketedExpresion[0] = bracketedExpresion[0].substring(1, bracketedExpresion[0].length)
                    val lastIndex = bracketedExpresion.size - 1
                    bracketedExpresion[lastIndex] = bracketedExpresion[lastIndex].substring(0, bracketedExpresion[lastIndex].length - 1)

//                    println("bracketedExpresion = ${bracketedExpresion}")
                    evaluate1(bracketedExpresion)
                }
                else -> {
                    println("ERROR. UNKNOWN EXPRESSION $nextToken")
                    0
                }
            }

//            println("operator = ${operator} operatingValue = $operatingValue")
            if (operator == Operator.Plus) {
                runningResult += operatingValue
            } else if (operator == Operator.Times) {
                runningResult *= operatingValue
            }
//            println("nextToken = ${nextToken} value = $runningResult")
        }

    }

    println("final result = $runningResult")

    return runningResult
}

fun evaluate2(expression: List<String>): Long {
    var holdingNumberBeforeTimes: Long? = null
    val expressionStack = expression.toMutableList()

    println("expression = ${expressionStack}")

    var runningResult = nextNumberExpression(expressionStack)

    while (expressionStack.size > 0) {
        val nextToken = expressionStack.removeAt(0)

        if (nextToken == "+") {
            val nextNumber = nextNumberExpression(expressionStack)
            runningResult += nextNumber
        } else if (nextToken == "*") {
            if (holdingNumberBeforeTimes != null) {
                // 2 * in a row, evaluate the first one
                holdingNumberBeforeTimes *= runningResult
            } else {
                holdingNumberBeforeTimes = runningResult
            }
            runningResult = nextNumberExpression(expressionStack)
        } else {
            throw Exception("Expected * or + but got $nextToken")
        }
    }

    if (holdingNumberBeforeTimes != null) {
        runningResult *= holdingNumberBeforeTimes
    }

    println("final result = $runningResult")

    return runningResult
}

fun nextNumberExpression(expressionStack: MutableList<String>): Long {
    return if (expressionStack[0].startsWith("(")) {
        val bracketedExpresion = mutableListOf<String>()
        var openBracketCount = 0
        var nextToken: String
        do {
            nextToken = expressionStack.removeAt(0)
            if (nextToken.startsWith("(")) {
                openBracketCount += nextToken.count { it == '(' }
            } else if (nextToken.endsWith(")")) {
                openBracketCount -= nextToken.count { it == ')' }
            }
            bracketedExpresion.add(nextToken)
        } while (openBracketCount > 0)

        // remove '(' from first and ')' from last
        bracketedExpresion[0] = bracketedExpresion[0].substring(1, bracketedExpresion[0].length)
        val lastIndex = bracketedExpresion.size - 1
        bracketedExpresion[lastIndex] = bracketedExpresion[lastIndex].substring(0, bracketedExpresion[lastIndex].length - 1)

        evaluate2(bracketedExpresion)
    } else {
        expressionStack.removeAt(0).toLong()
    }
}

