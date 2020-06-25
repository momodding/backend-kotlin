package com.momodding.backend.app.service.todos

import com.momodding.backend.app.dto.request.TodoRequest
import com.momodding.backend.app.entity.Todos
import javax.servlet.http.HttpServletRequest

interface TodosService {
	fun findAll(): List<Todos>

	fun findById(id: Long): Todos

	fun doSave(req: TodoRequest, http: HttpServletRequest): Todos

	fun doUpdate(id: Long, req: TodoRequest): Todos

	fun doDelete(id: Long): Any

	fun deletById(id: Long): Any
}