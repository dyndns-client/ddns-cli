package ddns.cli.command.profile.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ddns.cli.command.dto.NameValuePair;
import picocli.CommandLine;

import java.util.List;

public class NameValuePairJsonConverter implements CommandLine.ITypeConverter<List<NameValuePair>> {

    private final Gson gson = new Gson();

    @Override
    public List<NameValuePair> convert(String value) {
        try {
            NameValuePair pair = gson.fromJson(value, NameValuePair.class);
            return List.of(pair);
        } catch (Exception e) {
            return gson.fromJson(value, new TypeToken<>() {});
        }
    }
}
