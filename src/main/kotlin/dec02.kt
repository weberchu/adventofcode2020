import java.io.File

data class Password(
        val min: Int,
        val max: Int,
        val character: Char,
        val password: String
)

fun main() {
    val passwords = mutableListOf<Password>()
    File("src/main/resources/dec02.txt").forEachLine {
        val split = it.split("-", " ")
        passwords.add(Password(
                min = split[0].toInt(),
                max = split[1].toInt(),
                character = split[2][0],
                password = split[3]
        ))
    }

    println("passwords.size = ${passwords.size}")

    val validCount = passwords.filter { policy ->
        val occurrences = policy.password.count { it == policy.character }
        occurrences >= policy.min && occurrences <= policy.max
    }.size

    println("rule 1 validCount = ${validCount}")

    val validCount2 = passwords.filter { policy ->
        val c1Matched = policy.password[policy.min - 1] == policy.character
        val c2Matched = policy.password[policy.max - 1] == policy.character
        (c1Matched && !c2Matched) || (!c1Matched && c2Matched)
    }.size

    println("rule 2 validCount = ${validCount2}")
}