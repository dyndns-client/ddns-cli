package ddns.client.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AvailableProvider {
    NOIP("noip"),
    DuckDNS("duckdns"),
    FreeDNS("freedns"),
    DYNU("dynu");

    private final String name;
}
