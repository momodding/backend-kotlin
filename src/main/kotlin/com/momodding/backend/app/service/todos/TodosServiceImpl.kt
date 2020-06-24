package com.momodding.backend.app.service.todos

import com.momodding.backend.app.dto.request.TodoRequest
import com.momodding.backend.app.entity.Todos
import com.momodding.backend.app.repository.TodosRepository
import com.momodding.backend.config.auth.JwtUtils
import com.momodding.backend.exception.DataNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.transaction.Transactional

@Service
class TodosServiceImpl @Autowired constructor(
		val todosRepository: TodosRepository,
		val jwtUtils: JwtUtils
) : TodosService {
	override fun findAll(): List<Todos> {
		return todosRepository.findAll()
	}

	override fun findById(id: Long): Todos {
		return todosRepository.findById(id).orElse(null)
	}

	@Transactional
	override fun doSave(req: TodoRequest, http: HttpServletRequest): Todos {
		val data = Todos(
				ucId = jwtUtils.getLoginId(http),
				name = req.name,
				description = req.description
		)

		return todosRepository.saveAndFlush(data)
	}

	@Transactional
	override fun doUpdate(id: Long, req: TodoRequest): Todos {
		val data = todosRepository.findById(id).orElseThrow { throw DataNotFoundException("todo") }
		data.apply {
			name = req.name;
			description = req.description
		}
		return todosRepository.saveAndFlush(data)
	}

	@Transactional
	override fun doDelete(id: Long): Todos {
		val data = todosRepository.findById(id).orElseThrow { throw DataNotFoundException("todo") }
		data.deletedAt = Date()
		return todosRepository.saveAndFlush(data)
	}
}