package ddns.webapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ddns.client.daemon.DaemonClient;
import ddns.client.profile.ProfileClient;
import ddns.client.profile.ProfileClientBaseImpl;
import ddns.webapi.handler.DashboardHandler;
import ddns.webapi.handler.EmailEditHandler;
import ddns.webapi.handler.LoginHandler;
import ddns.webapi.handler.ProfileCreateHandler;
import ddns.webapi.handler.ProfileLogsHandler;
import ddns.webapi.handler.ProfileRemoveHandler;
import ddns.webapi.handler.ProfileShowHandler;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import org.apache.hc.core5.http.impl.bootstrap.ServerBootstrap;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;

import static ddns.client.constant.Defaults.DDNS_CLI_BASE_PATH;

@RequiredArgsConstructor
public class WebApi {

    public static void main(String[] args) {
        WebApi webApi = new WebApi();
        webApi.start(8081, DDNS_CLI_BASE_PATH, new ProfileClientBaseImpl(new GsonBuilder().setPrettyPrinting().create()), null);
    }

    public void start(int port, String basePath, ProfileClient profileClient, DaemonClient daemonClient) {
        try (HttpServer server = ServerBootstrap.bootstrap()
                .setListenerPort(port)
                .setCanonicalHostName("localhost")
                .register("/login", new LoginHandler(basePath))
                .register("/profile/show", new ProfileShowHandler(profileClient, basePath))
                .register("/profile/create", new ProfileCreateHandler(profileClient, daemonClient, basePath))
                .register("/profile/remove", new ProfileRemoveHandler(profileClient))
                .register("/profile/logs", new ProfileLogsHandler(profileClient, basePath))
                .register("/dashboard", new DashboardHandler(basePath, profileClient))
                .register("/email/edit", new EmailEditHandler(basePath))
                .setSocketConfig(SocketConfig.DEFAULT)
                .create()) {
            server.start();
            server.awaitTermination(TimeValue.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
