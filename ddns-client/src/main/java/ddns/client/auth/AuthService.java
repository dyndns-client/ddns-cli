package ddns.client.auth;

import ddns.client.domain.Auth;

import java.util.Optional;

public interface AuthService {

    void saveAuth(Auth auth, String basePath);

    Optional<Auth> getAuth(String basePath);

    boolean clear(String basePath);
}
