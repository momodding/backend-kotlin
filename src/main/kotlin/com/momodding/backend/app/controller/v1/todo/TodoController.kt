package com.momodding.backend.app.controller.v1.todo

import com.momodding.backend.app.dto.request.TodoRequest
import com.momodding.backend.app.service.todos.TodosService
import com.momodding.backend.exception.FormValidationException
import com.momodding.backend.utils.generateResponse
import id.investree.app.config.base.BaseController
import id.investree.app.config.base.ResultResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping(value = ["v1/todo"], produces = [MediaType.APPLICATION_JSON_VALUE])
class TodoController @Autowired constructor(
		val todoValidation: TodoValidation,
		val todosService: TodosService
) : BaseController() {

	@GetMapping
	fun todoList(): ResponseEntity<ResultResponse<Any>> {
		val todos = todosService.findAll()
		return generateResponse(todos).done("success")
	}

	@GetMapping("/{todoId}")
	fun todo(@PathVariable("todoId") todoId: Long): ResponseEntity<ResultResponse<Any>> {
		val todo = todosService.findById(id = todoId)
		return generateResponse(todo).done("success")
	}

	@PostMapping
	fun todoAdd(@Valid @RequestBody req: TodoRequest,
				error: Errors, http: HttpServletRequest): ResponseEntity<ResultResponse<Any>> {
		todoValidation.validateCreate(req, error)
		if (error.hasErrors()) throw FormValidationException(error.generateResponse())
		todosService.doSave(req = req, http = http)
		return generateResponse(req).done("success")
	}

	@PutMapping("/{todoId}")
	fun todoUpdate(@PathVariable("todoId") todoId: Long, @Valid @RequestBody req: TodoRequest,
				   error: Errors, http: HttpServletRequest): ResponseEntity<ResultResponse<Any>> {
		val updateResult = todosService.doUpdate(id = todoId, req = req)
		return generateResponse(updateResult).done("success")
	}

	@DeleteMapping("/{todoId}")
	fun todoDelete(@PathVariable("todoId") todoId: Long): ResponseEntity<ResultResponse<Any>> {
		todosService.doDelete(id = todoId)
		return generateResponse(todoId).done("success")
	}
}