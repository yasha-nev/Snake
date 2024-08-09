package com.example.snake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import com.example.snake.ui.theme.SnakeTheme
import com.example.snake.model.Snake
import com.example.snake.data.Direction

import android.util.Log

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val configuration = LocalConfiguration.current
            val screenHeight = configuration.screenHeightDp
            val screenWidth = configuration.screenWidthDp
            val snake = remember { Snake(screenWidth, screenHeight / 4 * 3) }
            SnakeTheme {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()

                            val (x,y) = dragAmount

                            if (abs(x) > abs(y)){
                                if (x < 0){
                                    snake.updateDiraction(Direction.left)
                                } else{
                                    snake.updateDiraction(Direction.right)
                                }

                            } else {
                                if (y > 0) {
                                    snake.updateDiraction(Direction.down)
                                } else {
                                    snake.updateDiraction(Direction.up)
                                }
                            }
                        }
                        detectTapGestures (
                            onDoubleTap = {
                                Log.v("doubleTap", "")
                                snake.reset()
                            }
                        )
                    }
                ) {
                    Greeting(
                        snake = snake, modifier = Modifier
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SnakeTheme {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp
        val screenWidth = configuration.screenWidthDp
        Greeting(Snake(screenWidth, screenHeight - 50),
            modifier = Modifier)
    }
}

@Composable
fun Greeting(snake: Snake, modifier: Modifier) {
    val snakeState = snake.state.collectAsState()
    val boxSize = 25

    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){


        Box(
            modifier = Modifier
                .background(Color(155, 255, 155))
                .height(((snake.mapHeight + 1) * boxSize).dp)
                .width(((snake.mapWidth + 1) * boxSize).dp)
        ) {

            val fruit = snakeState.value.fruit
            Box(modifier = modifier
                .size(boxSize.dp, boxSize.dp)
                .offset(fruit.first.dp * boxSize, fruit.second.dp * boxSize)
                .background(Color(255, 155, 155))
            )
            
            for (peace in snakeState.value.body){
                Box(modifier = modifier
                    .size(boxSize.dp, boxSize.dp)
                    .offset(peace.first.dp * boxSize, peace.second.dp * boxSize)
                    .background(Color(155, 155, 255))
                    .border(BorderStroke(1.dp, Color.Black))
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(onClick = { snake.reset() }) {
            Text(stringResource(R.string.restart))
        }


    }
}
