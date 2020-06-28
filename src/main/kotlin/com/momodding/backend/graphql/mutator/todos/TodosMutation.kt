package com.momodding.backend.graphql.mutator.todos

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.momodding.backend.app.entity.Todos
import com.momodding.backend.app.repository.TodosRepository
import com.momodding.backend.config.graphql.AuthGraphQLContext
import com.momodding.backend.exception.DataNotFoundException
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import java.util.*

@Component
class TodosMutation constructor(
		val todosRepository: TodosRepository
) : GraphQLMutationResolver {
	fun newTodo(name: String, description: String, env: DataFetchingEnvironment) : Todos {
		val ctx = env.getContext<AuthGraphQLContext>()
		val data = Todos(
				name = name,
				description = description,
				ucId = ctx.getTokenPayload().ucId
		)

		return todosRepository.saveAndFlush(data)
	}
	fun updateTodo(id: Long, name: String, description: String) : Todos {
		val data = todosRepository.findById(id).orElseThrow { throw DataNotFoundException("todos") }
		data.name = name
		data.description = description

		return todosRepository.saveAndFlush(data)
	}
	fun deleteTodo(id: Long) : Boolean {
		val data = todosRepository.findById(id).orElseThrow { throw DataNotFoundException("todos") }
		data.deletedAt = Date()
		return Optional.ofNullable(todosRepository.saveAndFlush(data)).isPresent()
	}
}