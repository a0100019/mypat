@file:Suppress("UNUSED_EXPRESSION")

package com.a0100019.mypat.presentation.main.second


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.a0100019.mypat.data.room.Todo
import com.a0100019.mypat.presentation.main.MainState
import com.a0100019.mypat.presentation.main.MainViewModel
import com.a0100019.mypat.ui.theme.MypatTheme
import org.orbitmvi.orbit.compose.collectAsState


@Composable
fun TodoScreen(
    viewModel: TodoViewModel = hiltViewModel(),
    viewModel2: MainViewModel = hiltViewModel()
) {

    //state 이용
    val state : TodoState = viewModel.collectAsState().value
    val state2 : MainState = viewModel2.collectAsState().value

    TodoScreen(
        todoText = state.todoText,
        onTodoTextChange = viewModel::onTodoTextChange,
        onAddTodoButtonClick = viewModel::addTodo,
        onDeleteTodoButtonClick = viewModel::deleteTodo,
        onUpdateTodoButtonClick = viewModel::updateTodo,
        todoList = state.todoList
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    todoText: String,
    onTodoTextChange: (String) -> Unit,
    onAddTodoButtonClick: () -> Unit,
    onDeleteTodoButtonClick: () -> Unit,
    onUpdateTodoButtonClick: () -> Unit,
    todoList: List<Todo>
) {

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextField(
                value = todoText,
                onValueChange = onTodoTextChange,
                modifier = Modifier.weight(1f).padding(8.dp)
            )
            Button(onClick = onAddTodoButtonClick) {
                Text("Add")
            }
            Button(onClick = onDeleteTodoButtonClick) {
                Text("Delete")
            }
            Button(onClick = onUpdateTodoButtonClick) {
                Text("Update")
            }
        }
        Row {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(todoList) { todo ->
                    Text(text = todo.id.toString(), modifier = Modifier.padding(8.dp))
                }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(todoList) { todo ->
                    Text(text = todo.title, modifier = Modifier.padding(8.dp))
                }
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(todoList) { todo ->
                    Text(text = todo.isDone.toString(), modifier = Modifier.padding(8.dp))
                }
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun TodoScreenPreview() {
    MypatTheme {
        TodoScreen(
            todoText = "안녕",
            onTodoTextChange = {},
            onAddTodoButtonClick = {},
            onDeleteTodoButtonClick = {},
            onUpdateTodoButtonClick = {},
            todoList = listOf(
                Todo(title = "first"),
                Todo(title = "second"),
                Todo(title = "third")
            )
        )
    }
}