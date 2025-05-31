package ddns.client.daemon;

import ddns.client.discovery.DnsDiscoveryService;
import ddns.client.discovery.HttpDiscoveryService;
import ddns.client.discovery.StunDiscoveryService;
import ddns.client.domain.DiscoveryMethod;
import ddns.client.domain.IP;
import ddns.client.domain.Profile;
import ddns.client.email.EmailService;
import ddns.client.exception.DiscoveryException;
import ddns.client.profile.ProfileClient;
import ddns.client.profile.ProfileLogger;
import ddns.client.updater.IpUpdater;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
public class DaemonProfileTask implements Runnable {

    private final ProfileClient profileClient;
    private final StunDiscoveryService stunDiscoveryService;
    private final DnsDiscoveryService dnsDiscoveryService;
    private final HttpDiscoveryService httpDiscoveryService;
    private final EmailService emailService;

    private final String basePath;

    private final Profile profile;

    @Override
    public void run() {
        try {
            IpUpdater updater = new IpUpdater(basePath);
            IP ip = discoverIp(basePath, profile);
            update(updater, ip);
        } catch (DiscoveryException e) {
            ProfileLogger.error(basePath, profile.getName(), e.getMessage());
        }
    }

    private void update(IpUpdater updater, IP currentIp) {
        IP lastUpdateIp = profile.getLastUpdateIp();
        if (Objects.nonNull(lastUpdateIp)) {
            if (!currentIp.equals(lastUpdateIp)) {
                ProfileLogger.info(basePath, profile.getName(), "IP changed from " + lastUpdateIp + " to " + currentIp);
                boolean updated = updater.update(currentIp, profile);
                if (updated) {
                    profile.setLastUpdateIp(currentIp);
                    profileClient.updateProfile(profile, basePath);
                    sendEmail(currentIp);
                }
            }
        } else {
            boolean updated = updater.update(currentIp, profile);
//                        profile.setLastUpdateIp(currentIp);
            if (updated) {
                profile.setLastUpdateIp(currentIp);
                profileClient.updateProfile(profile, basePath);
                sendEmail(currentIp);
            }
        }
    }

    private void sendEmail(IP currentIp) {
        if (emailService.getEmailInfo(basePath).isPresent()) {
            emailService.sendEmail(emailService.getEmailInfo(basePath).get(), currentIp);
        }
    }

    private IP discoverIp(String basePath, Profile profile) throws DiscoveryException {
        DiscoveryMethod discoveryMethod = profile.getDiscoveryMethod();
        IP ip = null;
        switch (discoveryMethod) {
            case STUN:
                ip = stunDiscoveryService.discover(
                        profile.getIpVersion(),
                        profile.getStunDiscoveryInfo().getServer(),
                        profile.getStunDiscoveryInfo().getSocketTimeout()
                );
                ProfileLogger.info(basePath, profile.getName(), "STUN public IP: " + ip.getValue());
                break;
            case DNS:
                ip = dnsDiscoveryService.discover(
                        profile.getDnsDiscoveryInfo().getServer(),
                        profile.getDnsDiscoveryInfo().getSocketTimeout()
                );
                ProfileLogger.info(basePath, profile.getName(), "DNS public IP: " + ip.getValue());
                break;
            case HTTP:
                ip = httpDiscoveryService.discover(
                        profile.getHttpDiscoveryInfo().getServer(),
                        profile.getHttpDiscoveryInfo().getSocketTimeout()
                );
                ProfileLogger.info(basePath, profile.getName(), "HTTP public IP: " + ip.getValue());
                break;
        }
        return ip;
    }
}
