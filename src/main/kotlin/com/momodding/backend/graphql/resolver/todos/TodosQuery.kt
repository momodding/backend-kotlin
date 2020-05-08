package com.momodding.backend.graphql.resolver.todos

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.momodding.backend.app.entity.Todos
import com.momodding.backend.app.service.todo.TodosService
import org.springframework.stereotype.Component

@Component
class TodosQuery constructor(
		val todosService: TodosService
) : GraphQLQueryResolver {

	fun getTodos() : List<Todos> {
		return todosService.findAll()
	}
}