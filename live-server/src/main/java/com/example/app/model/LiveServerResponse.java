package com.example.app.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.io.Serializable;

public class LiveServerResponse<T> implements Serializable {

    private HttpStatusCode httpStatusCode = HttpStatus.OK;
    private String message = "";
    private final T object;

    public static <T> LiveServerResponse<T> ok(T body) {
        return new LiveServerResponse<T>(HttpStatus.OK, body);
    }

    public static <T> LiveServerResponse<T> error(T body, String message) {
        return new LiveServerResponse<T>(HttpStatus.INTERNAL_SERVER_ERROR, message, body);
    }

    public LiveServerResponse(HttpStatusCode httpStatusCode, T object) {
        this.httpStatusCode = httpStatusCode;
        this.object = object;
    }

    public LiveServerResponse(HttpStatusCode httpStatusCode, String message, T object) {
        this.httpStatusCode = httpStatusCode;
        this.message = message;
        this.object = object;
    }

    public T getBody() {
        return object;
    }

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return httpStatusCode.value();
    }

}
