package com.ada.pedido.security.jwt;

import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.jwt.build.JwtClaimsBuilder;

import java.time.Instant;
import java.util.Date;

public class JWTService {

    private static String ISSUER = "https://ada.com"; // Defina o emissor do token
    private static String SECRET_KEY = "MySuperSecretKeyForTheAdaCourseThatIsAtLeast256BitsLong"; // Defina sua chave secreta aqui

    public static String criarToken(String email, String tipoUsuario) {
        JwtClaimsBuilder claimsBuilder = Jwt.claims()
                .issuer(ISSUER)
                .subject(email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600)) // Token expira em 1 hora
                .groups(tipoUsuario); // Adicione grupos ou roles conforme necessário

        return claimsBuilder.jws().signWithSecret(SECRET_KEY);
    }

    public static void validarToken(String token) throws ParseException {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(new MACVerifier(SECRET_KEY))) {
                throw new RuntimeException("Invalid JWT signature");
            }

            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            if (expirationTime == null) {
                throw new RuntimeException("JWT token does not have an expiration time");
            }

            if (expirationTime.before(new Date())) {
                throw new RuntimeException("JWT token has expired");
            }

        } catch (Exception e) {
            throw new ParseException("Error on parser", e);
        }
    }
}
