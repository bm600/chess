package util;

import java.security.SecureRandom;
import java.util.Base64;

public class AuthTokenGenerator {

    public static String makeToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

        return encoder.encodeToString(tokenBytes);
    }
}
