package betters.chromestarter.chrome.test;

import betters.chromestarter.chrome.prod.ChromeDriverExtensions;
import betters.chromestarter.chrome.prod.ProxyProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "chrome", name = "env",havingValue = "test")
public class TestChromeConfig {
    @Value("chrome.driver-path")
    private String driverPath; //"C:\\Users\\Admin\\Desktop\\chromedriver-win64\\chromedriver.exe"
    @SneakyThrows
    @Bean
    public RemoteWebDriver prodChromeDriver(ProxyProperties proxyProperties) {
        System.setProperty("webdriver.chrome.driver", driverPath);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        ChromeDriverExtensions.AddHttpProxy(options, proxyProperties);
        return new ChromeDriver();

    }
}
