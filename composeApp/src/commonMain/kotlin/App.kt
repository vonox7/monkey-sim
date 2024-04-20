import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import definitions.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
  MaterialTheme {
    var showCanvas by remember { mutableStateOf(false) }
    val world = remember {
      World(
        width = 20,
        height = 20,
        places = listOf(
          Home(Position(0, 0)),
          Work(Position(10, 10), maxPeople = 10),
          FoodShop(Position(5, 5)),
        ),
        actors = listOf(
          Actor(
            name = "Alice",
            needs = Needs.default(),
            yearsOfEducation = 10,
            age = 30,
            sex = Sex.Female,
            currentPosition = Position(0, 0),
            home = Home(Position(0, 0)),
          ),
          Actor(
            name = "Bob",
            needs = Needs.default(),
            yearsOfEducation = 5,
            age = 24,
            sex = Sex.Male,
            currentPosition = Position(3, 5),
            home = Home(Position(8, 1)),
          )
        )
      )
    }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          CanvasDrawer(this, world.width, world.height).draw(world)
        }
      }
    }
  }
}