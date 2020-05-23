package com.momodding.backend.app.service.todos

import com.momodding.backend.app.entity.Todos
import com.momodding.backend.app.repository.TodosRepository
import com.momodding.backend.config.auth.JwtUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*


@ExtendWith(MockitoExtension::class)
class TodosServiceTest {

    @Mock
    private lateinit var todosRepository: TodosRepository

    @Mock
    private lateinit var jwtUtils: JwtUtils

    @InjectMocks
    private lateinit var todosServiceImpl: TodosServiceImpl

    @Test
    fun shouldSuccess_findTodosById() {
        val todos = Todos(
                id = 1,
                name = "lorem",
                description = "ipsum"
        )
        given(todosRepository.findById(1)).willReturn(Optional.of(todos))
        val find = todosServiceImpl.findById(1)

        assertThat(find).isNotNull
        assertThat(find.id).isEqualTo(todos.id)
    }
}