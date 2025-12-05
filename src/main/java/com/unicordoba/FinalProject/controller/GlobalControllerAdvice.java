package com.unicordoba.FinalProject.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute
    public void agregarDatosGlobales(Model model, HttpServletRequest request) {
        // Esto hace que la variable "currentUri" esté disponible en TODOS los HTML
        model.addAttribute("currentUri", request.getRequestURI());
    }
}