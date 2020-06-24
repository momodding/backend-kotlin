package com.momodding.backend.app.controller.v1.todo

import com.momodding.backend.app.dto.request.TodoRequest
import com.momodding.backend.utils.isContainNumber
import com.momodding.backend.utils.isNotNull
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class TodoValidation {
	fun validateCreate(request: TodoRequest, errors: Errors) {
		if (request.name.isContainNumber()) errors.reject("name", "value contain number")
	}

	fun validateUpdate(request: TodoRequest, errors: Errors) {
		if (!request.todoId.isNotNull()) errors.reject("name", "value contain number")
	}
}