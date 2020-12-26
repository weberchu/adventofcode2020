import java.io.File

val ALL_FIELDS_EXCEPT_CID = setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid").sorted()
val ALL_FIELDS = setOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid", "cid").sorted()
val VALID_ECL = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")

fun main() {
    val currentPassport = mutableMapOf<String, String>()

    var validCount = 0

    File("src/main/resources/dec04.txt").forEachLine { line ->
        if (line.isBlank()) {
            if (isValidPassport(currentPassport)) {
                validCount++
            }
            currentPassport.clear()
        } else {
            line.split(" ").forEach { field ->
                val split = field.split(":")
                currentPassport[split[0]] = split[1]
            }
        }
    }

    if (isValidPassport(currentPassport)) {
        validCount++;
    }

    println("validCount = ${validCount}")
}

private fun isValidPassport(fields: Map<String, String>): Boolean {
    val sortedKeys = fields.keys.sorted()
    if (sortedKeys == ALL_FIELDS || sortedKeys == ALL_FIELDS_EXCEPT_CID) {
        val byr = fields["byr"]!!
        val iyr = fields["iyr"]!!
        val eyr = fields["eyr"]!!
        val hgt = fields["hgt"]!!
        val hcl = fields["hcl"]!!
        val ecl = fields["ecl"]!!
        val pid = fields["pid"]!!

        return (byr >= "1920" && byr <= "2002")
                && (iyr >= "2010" && iyr <= "2020")
                && (eyr >= "2020" && eyr <= "2030")
                && (isValidHeight(hgt))
                && "#[0-9,a-f]{6}".toRegex().matches(hcl)
                && VALID_ECL.contains(ecl)
                && "[0-9]{9}".toRegex().matches(pid)
    }
    return false
}

fun isValidHeight(hgt: String): Boolean {
    if (hgt.endsWith("cm")) {
        val num = hgt.substring(0, hgt.length - 2).toInt()
        return num in 150..193
    } else if (hgt.endsWith("in")) {
        val num = hgt.substring(0, hgt.length - 2).toInt()
        return num in 59..76
    } else {
        return false
    }
}
