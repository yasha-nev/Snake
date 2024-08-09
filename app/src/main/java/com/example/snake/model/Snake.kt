package com.example.snake.model

import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.snake.data.Direction
import com.example.snake.data.SnakeState

class Snake (displayWidth: Int, displayHeight: Int): ViewModel(){

    val mapWidth : Int = displayWidth / 25
    val mapHeight : Int = displayHeight / 25

    private var _state = MutableStateFlow(SnakeState(
        fruit = Pair<Int, Int> (0, mapWidth / 2),
        direction =  Direction.up,
        body = listOf(Pair<Int, Int> (2, 5)),
        gameOver = false
    ))

    val state: StateFlow<SnakeState> get() = _state

    init{
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                update()
                delay(250)
            }
        }
    }

    fun update(){
        if (_state.value.gameOver == true){
            return
        }

        val currentBody = _state.value.body
        val direction = _state.value.direction
        val head = currentBody.get(0)
        val newBody = mutableListOf(head)
        val eatFruit = checkEatFruit()

        val newFruit = if (eatFruit) Pair<Int, Int> ((0..mapWidth).random(), (0..mapHeight).random())
        else _state.value.fruit

        for ((index, peace) in currentBody.withIndex()){
            if ((index == currentBody.size - 1) and (!eatFruit) ){
                break
            }

            newBody.add(peace)
        }

        newBody[0] = makeMove(head, direction)

        val newGameOver = if (checkGameOver() == true) true
        else false

        _state.update {
            SnakeState(
                fruit = newFruit,
                direction = direction,
                body = newBody,
                gameOver = newGameOver
            )
        }
    }

    fun makeMove(head : Pair<Int, Int>, direction: Direction) : Pair<Int, Int>{
        var x : Int
        var y : Int

        if (direction == Direction.up){
            x = head.first
            y = head.second - 1
        }
        else if (direction == Direction.down) {
            x = head.first
            y = head.second + 1
        }
        else if (direction == Direction.left){
            x = head.first - 1
            y = head.second
        }
        else if (direction == Direction.right) {
            x = head.first + 1
            y = head.second
        }
        else {
            x = head.first
            y = head.second
        }

        if (x < 0){
            x = mapWidth
        } else if (x > mapWidth){
            x = 0
        } else if (y < 0){
            y = mapHeight
        } else if (y > mapHeight){
            y = 0
        }

        return Pair<Int, Int> (x, y)
    }

    fun reset(){
        _state.update {
            SnakeState(
                fruit = Pair<Int, Int> (0, mapHeight / 2),
                direction =  Direction.up,
                body = listOf(Pair<Int, Int> (0, 0)),
                gameOver = false
            )
        }
    }

    fun updateDiraction(direction: Direction){
        val curDirection = _state.value.direction
        if (direction == curDirection){
            return
        }

        if (((direction == Direction.up) && (curDirection == Direction.down)) or
            ((direction == Direction.down) && (curDirection == Direction.up))){
            return
        }

        if (((direction == Direction.left) && (curDirection == Direction.right)) or
            ((direction == Direction.right) && (curDirection == Direction.left))){
            return
        }

        _state.update {
            SnakeState(
                fruit = _state.value.fruit,
                gameOver = _state.value.gameOver,
                body = _state.value.body,
                direction = direction
            )
        }
    }

    fun checkEatFruit() : Boolean{
        val head = _state.value.body.get(0)
        val fruit = _state.value.fruit

        if (head == fruit){
            return true
        }

        return false
    }

    fun checkGameOver() : Boolean{
        val curBody = _state.value.body
        val head = curBody.get(0)

        for ((index, position) in curBody.withIndex()){
            if (index != 0 && position == head){
                return true
            }
        }
        return false
    }
}