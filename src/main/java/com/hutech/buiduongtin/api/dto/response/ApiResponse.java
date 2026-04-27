package com.hutech.buiduongtin.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
		boolean success,
		String message,
		T data,
		List<ApiError> errors,
		LocalDateTime timestamp) {

	public static <T> ApiResponse<T> success(String message, T data) {
		return new ApiResponse<>(true, message, data, null, LocalDateTime.now());
	}

	public static <T> ApiResponse<T> failure(String message, List<ApiError> errors) {
		return new ApiResponse<>(false, message, null, errors, LocalDateTime.now());
	}
}

