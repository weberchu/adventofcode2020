import java.io.File

enum class InstructionType {
    acc, jmp, nop
}

data class Instruction(
        val type: InstructionType,
        val value: Int
)

fun main() {
    val instructionSet = mutableListOf<Instruction>()

    File("src/main/resources/dec08.txt").forEachLine { line ->
        val split = line.split(' ')
        instructionSet.add(Instruction(
                InstructionType.valueOf(split[0]),
                split[1].toInt()
        ))
    }

    val accBeforeRepeat = executeUntilRepeat(instructionSet)
    println("accBeforeRepeat = ${accBeforeRepeat}")

    for (i in 0..instructionSet.size - 1) {
        val modifiedInstructionSet = instructionSet.toMutableList()
        var result = -1

        if (instructionSet[i].type == InstructionType.jmp) {
            modifiedInstructionSet[i] = Instruction(InstructionType.nop, instructionSet[i].value)
            result = executeWithoutRepeat(modifiedInstructionSet)
        } else if (instructionSet[i].type == InstructionType.nop) {
            modifiedInstructionSet[i] = Instruction(InstructionType.jmp, instructionSet[i].value)
            result = executeWithoutRepeat(modifiedInstructionSet)
        }


        if (result != -1) {
            println("i = ${i}")
            println("result = ${result}")
        }
    }
}

fun executeUntilRepeat(instructionSet: List<Instruction>): Int {
    var acc = 0
    val visitedInstruction = mutableSetOf<Int>()
    var currentInstruction = 0

    while (!visitedInstruction.contains(currentInstruction)) {
        visitedInstruction.add(currentInstruction)

        val instruction = instructionSet[currentInstruction]

        when (instruction.type) {
            InstructionType.acc -> {
                acc += instruction.value
                currentInstruction++
            }
            InstructionType.jmp -> {
                currentInstruction += instruction.value
            }
            else -> {
                currentInstruction++
            }
        }
    }

    return acc
}

fun executeWithoutRepeat(instructionSet: MutableList<Instruction>): Int {
    var acc = 0
    val visitedInstruction = mutableSetOf<Int>()
    var currentInstruction = 0

    while (currentInstruction < instructionSet.size) {
        if (visitedInstruction.contains(currentInstruction)) {
            return -1
        }
        visitedInstruction.add(currentInstruction)

        val instruction = instructionSet[currentInstruction]

        when (instruction.type) {
            InstructionType.acc -> {
                acc += instruction.value
                currentInstruction++
            }
            InstructionType.jmp -> {
                currentInstruction += instruction.value
            }
            else -> {
                currentInstruction++
            }
        }
    }

    return acc
}
