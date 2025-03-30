package ddns.client.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AvailableProvider {
    NOIP(1),
    DuckDNS(2),
    FreeDNS(3),
    DYNU(4);

    private final int position;
}
