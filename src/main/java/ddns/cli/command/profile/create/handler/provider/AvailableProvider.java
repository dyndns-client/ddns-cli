package ddns.cli.command.profile.create.handler.provider;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AvailableProvider {
    NOIP("www.noip.com"),
    DuckDNS("www.duckdns.org"),
    FreeDNS("freedns.afraid.org"),
    DYNU("www.dynu.com");

    private final String link;
}
