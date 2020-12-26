import java.io.File

const val PREAMBLE_SIZE = 25

fun main() {
    val nums = mutableListOf<Long>()

    File("src/main/resources/dec09.txt").forEachLine { line ->
        nums.add(line.toLong())
    }

    val invalidNumber = findInvalidNumber(nums)

    val contiguousSet = findContiguousSet(nums, invalidNumber)

    println("contiguousSet = ${contiguousSet}")
    println("contiguousSet sum = ${contiguousSet[0] + contiguousSet[1]}")

}

fun findContiguousSet(nums: List<Long>, target: Long): List<Long> {
    for (i in nums.indices) {
        var sum = nums[i]
        for (j in i + 1 until nums.size) {
            sum += nums[j]
            if (sum == target) {
                var min = nums[i]
                var max = nums[i]
                for (x in i+1 .. j) {
                    if (nums[x] < min) {
                        min = nums[x]
                    } else if (nums[x] > max) {
                        max = nums[x]
                    }
                }

                return listOf(min, max)
            }
        }
    }

    return listOf(-1, -1)
}

private fun findInvalidNumber(nums: MutableList<Long>): Long {
    for (i in PREAMBLE_SIZE until nums.size) {
        if (!isValidPreamble(nums, i)) {
            println("invalid preamble at ${i} = ${nums[i]}")
            for (j in PREAMBLE_SIZE downTo 1) {
                println("nums[j] = ${nums[i - j]}")
            }
            return nums[i]
        }
    }

    return -1
}

fun isValidPreamble(nums: List<Long>, i: Int): Boolean {
    for (x in 1 until PREAMBLE_SIZE) {
        for (y in x + 1..PREAMBLE_SIZE) {
            if (nums[i - x] + nums[i - y] == nums[i]) {
                println("Found ${nums[i - x]} + ${nums[i - y]} == ${nums[i]}")
                return true
            }
        }
    }

    return false
}
