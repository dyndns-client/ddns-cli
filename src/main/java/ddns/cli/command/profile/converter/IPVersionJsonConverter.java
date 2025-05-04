package ddns.cli.command.profile.converter;

import com.google.gson.Gson;
import ddns.client.domain.IPVersion;
import picocli.CommandLine;

import java.util.Objects;

public class IPVersionJsonConverter implements CommandLine.ITypeConverter<IPVersion> {

    private final Gson gson = new Gson();

    @Override
    public IPVersion convert(String value) throws Exception {
        if (Objects.nonNull(value) && "4".equals(value)) {
            return IPVersion.IPV4;
        }

        if (Objects.nonNull(value) && "6".equals(value)) {
            return IPVersion.IPV6;
        }

        return null;
    }
}
