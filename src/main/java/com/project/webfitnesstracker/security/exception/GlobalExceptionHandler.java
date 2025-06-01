package com.project.webfitnesstracker.security.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = NullEntityReferenceException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ModelAndView nullEntityReferencedExceptionHandler(HttpServletRequest request, NullEntityReferenceException ex){
        log.error("Null Entity ex: {} :: URL = {}", ex.getMessage(), request.getRequestURI(), ex);
        ModelAndView modelAndView = new ModelAndView("error/bad-request");
        modelAndView.addObject("code", HttpStatus.BAD_REQUEST.value() + " / " + HttpStatus.BAD_REQUEST.getReasonPhrase());
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public ModelAndView accessForbiddenExceptionHandler(HttpServletRequest request, AccessDeniedException ex){
        log.error("Access Denied: {} :: URL = {}", ex.getMessage(), request.getRequestURI(), ex);
        ModelAndView modelAndView = new ModelAndView("error/access-denied");
        modelAndView.addObject("code", HttpStatus.FORBIDDEN.value() + " / " + HttpStatus.FORBIDDEN.getReasonPhrase());
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ModelAndView entityNotFoundExceptionHandler(HttpServletRequest request, EntityNotFoundException ex) {
        return getModelAndView(request, HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ModelAndView noResourceFoundExceptionHandler(HttpServletRequest request, NoResourceFoundException ex) {
        log.error("Resource not found: {} :: URL = {}", ex.getMessage(), request.getRequestURI(), ex);
        ModelAndView modelAndView = new ModelAndView("error/not-found");
        modelAndView.addObject("code", HttpStatus.NOT_FOUND.value() + " / " + HttpStatus.NOT_FOUND.getReasonPhrase());
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView internalServerErrorHandler(HttpServletRequest request, Exception ex) {
        return getModelAndView(request, HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private ModelAndView getModelAndView(HttpServletRequest request, HttpStatus httpStatus, Exception exception) {
        log.error("Exception raised = {} :: URL = {}", exception.getMessage(), request.getRequestURI(), exception);
        ModelAndView modelAndView = new ModelAndView("error/error");
        modelAndView.addObject("code", httpStatus.value() + " / " + httpStatus.getReasonPhrase());
        modelAndView.addObject("message", exception.getMessage());
        return modelAndView;
    }
}
