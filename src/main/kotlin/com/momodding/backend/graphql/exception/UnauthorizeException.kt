package com.momodding.backend.graphql.exception

import graphql.ErrorType


class UnauthorizeException(defaultMessage: String) : GraphQLException(defaultMessage) {
    override val message: String?
        get() = super.message

    override fun getErrorType(): ErrorType {
        return ErrorType.ValidationError
    }
}

