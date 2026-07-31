package com.ada.pedido.security.jwt;

import io.smallrye.jwt.auth.principal.*;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import org.jose4j.jwt.JwtClaims;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Priority(1)
@Alternative
@ApplicationScoped
public class JWTCustomCallerPrincipal extends JWTCallerPrincipalFactory {

    @Override
    public JWTCallerPrincipal parse(String token, JWTAuthContextInfo authContextInfo) throws ParseException {
        System.out.println("Token recebido: " + token);
        JWTService.validarToken(token);

        try{

            String payload = new String(
                    Base64.getUrlDecoder().decode(token.split("\\.")[1]),
                    StandardCharsets.UTF_8
            );

            var claims = JwtClaims.parse(payload);

            return new DefaultJWTCallerPrincipal(token, "JWT", claims);

        } catch (Exception e) {
            System.err.println("Erro ao decodificar o token JWT: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
