package com.momodding.backend.app.controller.v1.todo

import com.momodding.backend.app.dto.request.TodoRequest
import com.momodding.backend.utils.isContainNumber
import org.springframework.stereotype.Component
import org.springframework.validation.Errors

@Component
class TodoValidation {
	fun validateCreate(request: TodoRequest, errors: Errors) {
		if (request.name.isContainNumber()) errors.reject("name", "value contain number")
	}
}