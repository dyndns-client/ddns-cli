package ddns.webapi.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class UserSessionHolder {

    private static final Set<String> sessions = new HashSet<>();

    public static String generateAndSaveSessionId() {
        String sessionId = UUID.randomUUID().toString();
        sessions.add(sessionId);
        return sessionId;
    }

    public static boolean checkContainsSessionId(String sessionId) {
        return sessions.contains(sessionId);
    }

    public static String getSessionIdFromCookies(String cookies) {
        return extractSessionId(cookies);
    }

    private static String extractSessionId(String cookieString) {
        Map<String, String> cookies = parseCookies(cookieString);
        return cookies.get("sessionId");
    }

    private static Map<String, String> parseCookies(String cookieString) {
        Map<String, String> cookies = new HashMap<>();

        if (cookieString == null || cookieString.trim().isEmpty()) {
            return cookies;
        }

        // Разделяем cookie по точке с запятой
        String[] cookiePairs = cookieString.split(";");

        for (String cookiePair : cookiePairs) {
            String trimmedPair = cookiePair.trim();

            // Находим первое вхождение знака равенства
            int equalIndex = trimmedPair.indexOf('=');

            if (equalIndex > 0) {
                String key = trimmedPair.substring(0, equalIndex).trim();
                String value = trimmedPair.substring(equalIndex + 1).trim();
                cookies.put(key, value);
            }
        }

        return cookies;
    }
}
