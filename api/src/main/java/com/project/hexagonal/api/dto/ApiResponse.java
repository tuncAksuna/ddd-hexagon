package com.project.hexagonal.api.dto;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private String errorMessage;
    private int status;
    private T data;

    public ApiResponse(int status,String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = status;
    }

    public ApiResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public ApiResponse(T data) {
        this.data = data;
    }

    public ApiResponse(int status) {
        this.status = status;
    }
}
