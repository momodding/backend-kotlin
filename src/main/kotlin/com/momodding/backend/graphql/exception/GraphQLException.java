package com.momodding.backend.graphql.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

public class GraphQLException extends RuntimeException implements GraphQLError {

	String defaultMessage;

	public GraphQLException(String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}

	@Override
	public List<SourceLocation> getLocations() {
		return null;
	}

	@Override
	public ErrorType getErrorType() {
		return null;
	}
}
