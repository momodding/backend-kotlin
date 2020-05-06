package com.momodding.backend.app.repository

import com.momodding.backend.app.entity.Todos
import org.springframework.data.jpa.repository.JpaRepository

interface TodosRepository : JpaRepository<Todos, Long> {
}