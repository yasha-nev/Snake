package com.example.snake.data

data class SnakeState(
    val fruit: Pair<Int, Int>,
    val direction: Direction,
    val body: List<Pair<Int, Int>>,
    val gameOver: Boolean
)