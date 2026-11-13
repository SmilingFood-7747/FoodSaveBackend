package com.foodsave.platform.iam.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodsave.platform.iam.domain.model.Account;

@Service
public class TokenService {
    private final byte[] secret;
    private final ObjectMapper mapper;
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private final Base64.Decoder decoder = Base64.getUrlDecoder();

    public TokenService(@Value("${foodsave.auth.secret:local-foodsave-secret-change-before-deploy-0123456789}") String secret, ObjectMapper mapper) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        if (this.secret.length < 32) throw new IllegalArgumentException("foodsave.auth.secret must contain at least 32 bytes");
        this.mapper = mapper;
    }

    public String issue(Account account) {
        try {
            var header = encoder.encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
            var payload = encoder.encodeToString(mapper.writeValueAsBytes(new Claims(account.getId(), account.getRole().name(), Instant.now().plusSeconds(43_200).getEpochSecond())));
            var unsigned = header + "." + payload;
            return unsigned + "." + encoder.encodeToString(sign(unsigned));
        } catch (Exception exception) { throw new IllegalStateException(exception); }
    }

    public Claims verify(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3 || !MessageDigest.isEqual(sign(parts[0] + "." + parts[1]), decoder.decode(parts[2])))
                throw new IllegalArgumentException("auth.token.invalid");
            Claims claims = mapper.readValue(decoder.decode(parts[1]), Claims.class);
            if (claims.expiresAt() < Instant.now().getEpochSecond()) throw new IllegalArgumentException("auth.token.expired");
            return claims;
        } catch (IllegalArgumentException exception) { throw exception; }
        catch (Exception exception) { throw new IllegalArgumentException("auth.token.invalid"); }
    }

    private byte[] sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return mac.doFinal(value.getBytes(StandardCharsets.US_ASCII));
    }

    public record Claims(Long accountId, String role, long expiresAt) {}
}
