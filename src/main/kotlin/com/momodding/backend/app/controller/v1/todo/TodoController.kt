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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
@RequestMapping(value = ["v1/todo"], produces = [MediaType.APPLICATION_JSON_VALUE])
class TodoController @Autowired constructor(
		val todoValidation: TodoValidation,
		val todosService: TodosService
) : BaseController() {

	@PostMapping
	fun todoAdd(@Valid @RequestBody req: TodoRequest, error: Errors, http: HttpServletRequest) : ResponseEntity<ResultResponse<Any>> {
		todoValidation.validate(req, error)
		if (error.hasErrors()) throw FormValidationException(error.generateResponse())
		todosService.doSave(req = req, http = http)
		return generateResponse(req).done("success")
	}
}