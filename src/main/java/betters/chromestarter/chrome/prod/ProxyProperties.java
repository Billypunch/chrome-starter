package betters.chromestarter.chrome.prod;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Data
@NoArgsConstructor
@Slf4j
@ConditionalOnProperty(prefix = "chrome.proxy", name = "enabled",havingValue = "true")
public class ProxyProperties {
    private String host;
    private int port;
    private String login;
    private String password;
}
