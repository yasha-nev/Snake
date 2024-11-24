package com.example.snake.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snake.data.Direction
import com.example.snake.data.SnakeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class Snake(displayWidth: Int, displayHeight: Int) : ViewModel() {

    val mapWidth: Int = displayWidth / 25
    val mapHeight: Int = displayHeight / 25

    private val newDirection = MutableStateFlow(Direction.Up)

    private var _state = MutableStateFlow(
        SnakeState(
            fruit = Pair(mapHeight / 2, mapWidth / 2),
            direction = Direction.Up,
            body = listOf(Pair(2, 5)),
            gameOver = false
        )
    )

    val state: StateFlow<SnakeState> get() = _state

    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                update()
                delay(250)
            }
        }
    }

    private fun update() {
        if (_state.value.gameOver) {
            return
        }

        val currentBody = _state.value.body
        val direction = newDirection.value
        val head = currentBody[0]
        val newBody = mutableListOf(head)
        val eatFruit = checkEatFruit()

        val newFruit = if (eatFruit) updateFruit() else _state.value.fruit

        for ((index, peace) in currentBody.withIndex()) {
            if ((index == currentBody.size - 1) and (!eatFruit)) {
                break
            }

            newBody.add(peace)
        }

        newBody[0] = makeMove(head, direction)

        _state.update {
            SnakeState(
                fruit = newFruit,
                direction = direction,
                body = newBody,
                gameOver = checkGameOver()
            )
        }
    }

    private fun makeMove(head: Pair<Int, Int>, direction: Direction): Pair<Int, Int> {
        var x: Int
        var y: Int

        when (direction) {
            Direction.Up -> {
                x = head.first
                y = head.second - 1
            }

            Direction.Down -> {
                x = head.first
                y = head.second + 1
            }

            Direction.Left -> {
                x = head.first - 1
                y = head.second
            }

            Direction.Right -> {
                x = head.first + 1
                y = head.second
            }
        }

        if (x < 0) {
            x = mapWidth
        } else if (x > mapWidth) {
            x = 0
        } else if (y < 0) {
            y = mapHeight
        } else if (y > mapHeight) {
            y = 0
        }

        return Pair(x, y)
    }

    fun reset() {
        _state.update {
            SnakeState(
                fruit = Pair(mapHeight / 2, mapWidth / 2),
                direction = Direction.Up,
                body = listOf(Pair(0, 0)),
                gameOver = false
            )
        }
    }

    fun updateDirection(direction: Direction) {
        val curDirection = _state.value.direction
        if (direction == curDirection) {
            return
        }

        if (((direction == Direction.Up) && (curDirection == Direction.Down)) or
            ((direction == Direction.Down) && (curDirection == Direction.Up))
        ) {
            return
        }

        if (((direction == Direction.Left) && (curDirection == Direction.Right)) or
            ((direction == Direction.Right) && (curDirection == Direction.Left))
        ) {
            return
        }

        newDirection.value = direction
    }

    private fun updateFruit(): Pair<Int, Int> {
        val curBody = _state.value.body

        while (true) {
            val newFruit: Pair<Int, Int> = Pair((0..mapWidth).random(), (0..mapHeight).random())

            var flag = true
            for (position in curBody.withIndex()) {
                if (position.value == newFruit) {
                    flag = false
                }
            }

            if (flag) {
                return newFruit
            }
        }
    }

    private fun checkEatFruit(): Boolean {
        val head = _state.value.body[0]
        val fruit = _state.value.fruit
        return head == fruit
    }

    private fun checkGameOver(): Boolean {
        val curBody = _state.value.body
        val head = curBody[0]

        for ((index, position) in curBody.withIndex()) {
            if (index != 0 && position == head) {
                return true
            }
        }
        return false
    }
}