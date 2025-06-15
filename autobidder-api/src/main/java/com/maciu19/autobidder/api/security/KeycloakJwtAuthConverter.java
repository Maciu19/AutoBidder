package com.maciu19.autobidder.api.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final String clientId;

    public KeycloakJwtAuthConverter(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                new JwtGrantedAuthoritiesConverter().convert(source).stream(),
                extractResourceRoles(source).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(source, authorities);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null || resourceAccess.isEmpty()) {
            return Collections.emptySet();
        }

        Map<String, Object> clientRoles = (Map<String, Object>) resourceAccess.get(this.clientId);

        if (clientRoles == null || clientRoles.isEmpty()) {
            return Collections.emptySet();
        }

        Collection<String> roles = (Collection<String>) clientRoles.get("roles");

        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
