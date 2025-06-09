package ddns.client.utils;

import ddns.client.domain.DiscoveryMethod;
import ddns.client.domain.Profile;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProfileUtils {

    public static void validate(Profile profile) {
        // check discovery interval value (n / 10)
    }

    public static int getDelay(Profile profile) {
        DiscoveryMethod discoveryMethod = profile.getDiscoveryMethod();
        return switch (discoveryMethod) {
            case STUN -> profile.getStunDiscoveryInfo().getDiscoveryInterval();
            case DNS -> profile.getDnsDiscoveryInfo().getDiscoveryInterval();
            case HTTP -> profile.getHttpDiscoveryInfo().getDiscoveryInterval();
        };
    }
}
