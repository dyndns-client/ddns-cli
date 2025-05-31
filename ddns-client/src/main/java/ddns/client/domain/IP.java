package ddns.client.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class IP {
    private String value;

    @Override
    public String toString() {
        return value;
    }
}
