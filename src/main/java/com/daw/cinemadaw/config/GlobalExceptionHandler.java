package com.daw.cinemadaw.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(HttpServletRequest req, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("errorTitle", "Pàgina no trobada");
        model.addAttribute("errorMessage", "La pàgina que busques no existeix o ha estat moguda.");
        model.addAttribute("requestUri", req.getRequestURI());
        return "error";
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(HttpServletRequest req, Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("errorTitle", "Accés denegat");
        model.addAttribute("errorMessage", "No tens permís per accedir a aquesta pàgina.");
        model.addAttribute("requestUri", req.getRequestURI());
        return "error";
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(HttpServletRequest req, Model model) {
        model.addAttribute("status", 409);
        model.addAttribute("errorTitle", "Conflicte de dades");
        model.addAttribute("errorMessage",
                "No s'ha pogut completar l'operació perquè hi ha dades relacionades. "
                        + "Elimina primer els elements associats.");
        model.addAttribute("requestUri", req.getRequestURI());
        return "error";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(HttpServletRequest req, Model model, IllegalArgumentException ex) {
        model.addAttribute("status", 400);
        model.addAttribute("errorTitle", "Petició incorrecta");
        model.addAttribute("errorMessage", ex.getMessage() != null ? ex.getMessage()
                : "La petició no és vàlida. Revisa les dades introduïdes.");
        model.addAttribute("requestUri", req.getRequestURI());
        return "error";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String handleGeneric(HttpServletRequest req, Model model, Exception ex) {
        model.addAttribute("status", 500);
        model.addAttribute("errorTitle", "Error intern del servidor");
        model.addAttribute("errorMessage",
                "Ha ocorregut un error inesperat. Si el problema persisteix, contacta amb l'administrador.");
        model.addAttribute("requestUri", req.getRequestURI());
        model.addAttribute("exceptionName", ex.getClass().getSimpleName());
        return "error";
    }
}
