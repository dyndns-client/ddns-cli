package ddns.cli.command.profile.converter;

import com.google.gson.Gson;
import ddns.client.domain.DiscoveryMethod;
import picocli.CommandLine;

public class DiscoveryMethodJsonConverter implements CommandLine.ITypeConverter<DiscoveryMethod> {

    private final Gson gson = new Gson();

    @Override
    public DiscoveryMethod convert(String value) {
        return gson.fromJson(value.toUpperCase(), DiscoveryMethod.class);
    }
}
