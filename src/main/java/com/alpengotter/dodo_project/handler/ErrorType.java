package com.alpengotter.dodo_project.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// TODO Добавить кастомные параметры в сообщения
@AllArgsConstructor
@Getter
public enum ErrorType {
    UNAUTHORIZED("Unauthorized", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    CLINIC_NOT_FOUND("Clinic not found", HttpStatus.NOT_FOUND),
    ADMIN_NOT_FOUND("Admin not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND("Order not found", HttpStatus.NOT_FOUND),
    HISTORY_NOT_FOUND("History not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXIST("User already exist", HttpStatus.BAD_REQUEST),
    CLINIC_ALREADY_EXIST("Clinic already exist", HttpStatus.BAD_REQUEST),
    NOT_CORRECT_CURRENCY("Currency not correct", HttpStatus.BAD_REQUEST),
    INCORRECT_PASSWORD("Incorrect password", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("Access denied", HttpStatus.BAD_REQUEST),
    SERVER_ERROR("Server error", HttpStatus.INTERNAL_SERVER_ERROR);

    public final String message;
    public final HttpStatus status;
}
