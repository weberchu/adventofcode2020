import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL

fun main() {
    val nums = mutableListOf<Int>()
    File("src/main/resources/dec01.txt").forEachLine {
        nums.add(it.toInt())
    }

    println("nums.size = ${nums.size}")

    for (currentIndex in 0 until nums.size) {
        for (partnerIndex in currentIndex until nums.size) {
            if (nums[currentIndex] + nums[partnerIndex] == 2020) {
                println("Found it ${nums[currentIndex]} + ${nums[partnerIndex]} = 2020")
                println("         ${nums[currentIndex]} * ${nums[partnerIndex]} = ${nums[currentIndex]*nums[partnerIndex]}")
            }
        }
    }

    for (index1 in 0 until nums.size) {
        for (index2 in index1 until nums.size) {
            for (index3 in index2 until nums.size) {
                if (nums[index1] + nums[index2]+ nums[index3] == 2020) {
                    println("Found it ${nums[index1]} + ${nums[index2]} + ${nums[index3]} = 2020")
                    println("         ${nums[index1]} * ${nums[index2]} * ${nums[index3]} = ${nums[index1] * nums[index2] * nums[index3]}")
                }
            }
        }
    }
}