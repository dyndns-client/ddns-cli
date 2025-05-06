package ddns.client.domain.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class IPLocation {
    @Builder.Default
    private LocationType locationType = LocationType.BODY;
    private String locationName;
}
