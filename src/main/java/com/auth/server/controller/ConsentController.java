package com.auth.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class ConsentController {

    private final RegisteredClientRepository registeredClientRepository;

    @GetMapping("/oauth2/consent")
    public String consent(
            Principal principal,
            Model model,
            @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId,
            @RequestParam(OAuth2ParameterNames.SCOPE) String scope,
            @RequestParam(OAuth2ParameterNames.STATE) String state) {

        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        Set<String> requestedScopes = Set.of(scope.split(" "));

        model.addAttribute("clientName", client != null ? client.getClientName() : clientId);
        model.addAttribute("principalName", principal.getName());
        model.addAttribute("requestedScopes", requestedScopes);
        model.addAttribute("clientId", clientId);
        model.addAttribute("state", state);

        return "consent";
    }
}
