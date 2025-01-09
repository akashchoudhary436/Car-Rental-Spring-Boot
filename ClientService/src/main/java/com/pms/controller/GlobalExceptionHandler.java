package com.pms.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles HttpClientErrorException (e.g., 4xx errors).
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public String handleHttpClientError(HttpClientErrorException e, Model model) {
        model.addAttribute("error", "Client error occurred: " + e.getResponseBodyAsString());
        return "error"; // Redirects to a generic error page
    }

    /**
     * Handles HttpServerErrorException (e.g., 5xx errors).
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public String handleHttpServerError(HttpServerErrorException e, Model model) {
        model.addAttribute("error", "Server error occurred: " + e.getResponseBodyAsString());
        return "error"; // Redirects to a generic error page
    }

    /**
     * Handles RestClientException for connection-related errors.
     */
    @ExceptionHandler(RestClientException.class)
    public String handleRestClientException(RestClientException e, Model model) {
        model.addAttribute("error", "Service is unavailable. Please try again later.");
        return "error"; // Redirects to a generic error page
    }

    /**
     * Handles generic exceptions.
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model) {
        model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
        return "error"; // Redirects to a generic error page
    }
}
