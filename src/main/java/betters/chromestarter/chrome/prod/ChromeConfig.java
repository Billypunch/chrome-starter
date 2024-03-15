package betters.chromestarter.chrome.prod;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "chrome", name = "env",havingValue = "prod")
public class ChromeConfig {
    @Value("${urls.selenium-url}")
    private String seleniumHubUrl;


    @Bean
    @Primary
    public WebDriverWait ourChromeWaitSettings(RemoteWebDriver chromeDriver) {
        return new WebDriverWait(chromeDriver, Duration.of(20, ChronoUnit.SECONDS));
    }

    @SneakyThrows
    @Bean
    public RemoteWebDriver prodChromeDriver(ProxyProperties proxyProperties) {
      //  System.setProperty("webdriver.chrome.driver", "C:\\Users\\Admin\\Desktop\\chromedriver-win64\\chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        ChromeDriverExtensions.AddHttpProxy(options, proxyProperties);
        // return new ChromeDriver();
         return new RemoteWebDriver(new URL(seleniumHubUrl), options);
    }
    @Bean
    public ProxyProperties proxyProperties() {
        return new ProxyProperties();
    }
}