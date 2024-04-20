package world

import definitions.*
import kotlin.random.Random

fun generateSimpleGraz(): World {
    // Area codes from 0 to 9
    class Area(val x: Int, val y: Int, val density: Int) {
        fun toPosition(random: Random): Position {
            val shift = when (random.nextInt(0, 4)) {
                0 -> if (x % 2 == 0) 20 else 60
                1 -> if (y % 2 == 0) 40 else 80
                2 -> 90
                3 -> if (x == 0 || y == 0 || x == 9 || y == 9) 30 else 120
                else -> throw Exception()
            }
            return Position(
                (50 + 90 * x + random.nextInt(-shift, shift)).coerceIn(10, 990),
                (50 + 90 * y + random.nextInt(-shift, shift)).coerceIn(10, 990),
            )
        }
    }

    val homeAreas = listOf(
        Area(0, 5, 10),
        Area(0, 6, 10),
        Area(0, 7, 10),
        Area(1, 0, 10),
        Area(1, 1, 10),
        Area(2, 3, 15),
        Area(2, 4, 20),
        Area(2, 5, 20),
        Area(2, 6, 20),
        Area(2, 7, 15),
        Area(2, 8, 10),
        Area(3, 3, 20),
        Area(3, 5, 30),
        Area(3, 6, 20),
        Area(3, 7, 10),
        Area(4, 4, 10),
        Area(4, 5, 10),
        Area(4, 6, 10),
        Area(4, 7, 10),
        Area(5, 5, 10),
        Area(5, 6, 10),
        Area(5, 7, 10),
        Area(6, 5, 5),
        Area(6, 8, 5),
        Area(7, 5, 5),
        Area(7, 8, 5),
        Area(8, 5, 4),
        Area(8, 9, 3),
        Area(9, 5, 3),
        Area(9, 9, 2),
    )

    val workAreas = listOf(
        Area(1, 9, 10),
        Area(2, 8, 10),
        Area(2, 9, 10),
        Area(3, 4, 10),
        Area(3, 5, 10),
        Area(3, 6, 10),
        Area(3, 8, 10),
        Area(3, 9, 10),
        Area(4, 4, 10),
        Area(4, 5, 10),
        Area(4, 6, 10),
        Area(4, 7, 10),
        Area(4, 8, 20),
        Area(4, 9, 20),
        Area(5, 7, 30),
        Area(5, 8, 30),
        Area(6, 4, 10),
        Area(6, 8, 20),
        Area(6, 9, 20),
        Area(7, 7, 10),
        Area(7, 8, 10),
        Area(7, 9, 10),
        Area(8, 7, 10),
    )

    val shopAreas = listOf(
        Area(1, 2, 10),
        Area(1, 8, 10),
        Area(2, 2, 10),
        Area(2, 3, 20),
        Area(2, 4, 20),
        Area(2, 5, 20),
        Area(3, 4, 40),
        Area(3, 5, 40),
        Area(3, 6, 20),
        Area(4, 3, 10),
        Area(5, 7, 10),
    )

    val educationAreas = listOf(
        Area(1, 5, 5),
        Area(3, 4, 10),
        Area(3, 5, 10),
        Area(3, 6, 10),
        Area(4, 3, 30),
        Area(5, 3, 10),
    )

    val random = Random(123456)

    val actors = mutableListOf<Actor>()
    val homePlaces = mutableListOf<Home>()
    val workPlaces = mutableListOf<Work>()
    val foodPlaces = mutableListOf<FoodShop>()

    homeAreas.forEach { homeArea ->
        repeat(homeArea.density * 5) {
            val homePlace = Home(homeArea.toPosition(random))
            homePlaces += homePlace
            actors += Actor.create(random, homePlace)
        }
    }

    workAreas.forEach { workArea ->
        repeat(workArea.density) {
            workPlaces += Work(workArea.toPosition(random), maxPeople = random.nextInt(5, 100))
        }
    }

    shopAreas.forEach { shopArea ->
        repeat(shopArea.density) {
            val position = shopArea.toPosition(random)
            workPlaces += Work(position, maxPeople = random.nextInt(1, 30))
            foodPlaces += FoodShop(position)
        }
    }

    educationAreas.forEach { educationArea ->
        repeat(educationArea.density) {
            workPlaces += Work(educationArea.toPosition(random), maxPeople = random.nextInt(100, 500))
        }
    }

    // Randomly assign people work. Override workPlace, so not every workPlace is 100% filled.
    workPlaces.shuffled(random).forEach { workPlace ->
        repeat(workPlace.maxPeople) {
            actors.random(random).work = workPlace
        }
    }

    return World(
        width = 1000,
        height = 1000,
        places = homePlaces + workPlaces + foodPlaces,
        actors = actors,
    )
}