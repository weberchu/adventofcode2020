import java.io.File
import kotlin.math.sqrt

data class Food(
    val ingredients: List<String>,
    val allergens: List<String>,
)

fun main() {
    val foods = mutableListOf<Food>()

    val file = File("src/main/resources/dec21.txt")
    file.forEachLine { line ->
        val indexOfAllergenStart = line.indexOf(" (contains ")
        val allergens = line.substring(indexOfAllergenStart + 11, line.length - 1).split(", ")
        val ingredients = line.substring(0, indexOfAllergenStart).split(" ")
        foods.add(Food(ingredients, allergens))
    }

    val allergenToPossibleIngredients = mutableMapOf<String, MutableSet<String>>()

    foods.forEach { food ->
        food.allergens.forEach { allergen ->
            if (allergenToPossibleIngredients.containsKey(allergen)) {
                allergenToPossibleIngredients[allergen] = food.ingredients.filter {
                    allergenToPossibleIngredients[allergen]!!.contains(it)
                }.toMutableSet()
            } else {
                allergenToPossibleIngredients[allergen] = food.ingredients.toMutableSet()
            }
        }
    }

    val allergenToIngredient = mutableMapOf<String, String>()
    while (allergenToPossibleIngredients.any { it.value.size == 1 }) {
        allergenToPossibleIngredients.filter { it.value.size == 1 }.forEach { confirmedMap ->
            val confirmedIngredient = confirmedMap.value.first()
            println("confirmedIngredient = ${confirmedIngredient}")
            allergenToIngredient[confirmedMap.key] = confirmedIngredient
            allergenToPossibleIngredients.remove(confirmedMap.key)

            allergenToPossibleIngredients.forEach { possibleMap ->
                allergenToPossibleIngredients[possibleMap.key] = possibleMap.value.filter {
                    it != confirmedIngredient
                }.toMutableSet()
            }
        }
    }

    println("allergenToPossibleIngredients = ${allergenToPossibleIngredients}")

    println("allergenToIngredient = ${allergenToIngredient}")

    countNonAllergenIngredient(foods, allergenToIngredient)

    canonicalSort(allergenToIngredient)

}

fun countNonAllergenIngredient(foods: List<Food>, allergenToIngredient: Map<String, String>) {
    val allergicIngredients = allergenToIngredient.values.toSet()

    val count = foods
            .flatMap { it.ingredients }
        .filter { !allergicIngredients.contains(it) }
        .count()

    println("count = ${count}")
}

fun canonicalSort(allergenToIngredient: Map<String, String>) {
    val sortedString = allergenToIngredient
        .toList()
        .sortedBy { it.first }
        .map { it.second }
        .joinToString(separator = ",")

    println("sortedString = ${sortedString}")
}
