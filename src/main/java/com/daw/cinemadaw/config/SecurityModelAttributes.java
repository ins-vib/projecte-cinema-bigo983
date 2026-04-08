package com.daw.cinemadaw.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class SecurityModelAttributes {

    @ModelAttribute
    public void addSecurityAttributes(Model model, Authentication authentication) {
        if (authentication == null) {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("isAdmin", false);
            model.addAttribute("isClient", false);
            return;
        }

        Authentication auth = authentication;
        boolean authenticated = auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);

        boolean isAdmin = false;
        boolean isClient = false;

        if (authenticated && auth.getAuthorities() != null) {
            isAdmin = auth.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
            isClient = auth.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_CLIENT".equals(authority.getAuthority()));
        }

        model.addAttribute("isAuthenticated", authenticated);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isClient", isClient);
    }
}