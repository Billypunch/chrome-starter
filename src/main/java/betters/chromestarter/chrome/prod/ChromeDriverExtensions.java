package betters.chromestarter.chrome.prod;

import lombok.SneakyThrows;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
public class ChromeDriverExtensions {

    private static final String backgroundJsTemplate =
            """
                    var config = {
                        mode: "fixed_servers",
                        rules: {
                            singleProxy: {
                                scheme: "http",
                                host: "%s",
                                port: parseInt(%d)
                            },
                            bypassList: []
                        }
                    };

                    chrome.proxy.settings.set({ value: config, scope: "regular" }, function() { });

                    function callbackFn(details) {
                        return {
                            authCredentials: {
                                username: "%s",
                                password: "%s"
                            }
                        };
                    }

                    chrome.webRequest.onAuthRequired.addListener(
                        callbackFn,
                        { urls:["<all_urls>"] },
                        ['blocking']
                    );""";

    private static final String manifestJson =
            """
                    {
                        "version": "1.0.0",
                        "manifest_version": 2,
                        "name": "Chrome Proxy",
                        "permissions": [
                            "proxy",
                            "tabs",
                            "unlimitedStorage",
                            "storage",
                            "<all_urls>",
                            "webRequest",
                            "webRequestBlocking"
                        ],
                        "background": {
                            "scripts": ["background.js"]
                        },
                        "minimum_chrome_version":"22.0.0"
                    }""";
    @SneakyThrows
    public static void AddHttpProxy(ChromeOptions options, ProxyProperties proxyProperties) {
        String host = proxyProperties.getHost();
        int port = proxyProperties.getPort();
        String userName = proxyProperties.getLogin();
        String password = proxyProperties.getPassword();
        setProxyParameters(options, host, port, userName, password);
    }
    @SneakyThrows
    private static void setProxyParameters(ChromeOptions options, String host, int port, String userName, String password){
        String backgroundJs = String.format(backgroundJsTemplate, host, port, userName, password);

        Path pluginDir = Files.createTempDirectory("plugin");

        FileWriter fileWriter = new FileWriter(pluginDir.resolve("background.js").toFile());
        fileWriter.write(backgroundJs);
        fileWriter.close();

        fileWriter = new FileWriter(pluginDir.resolve("manifest.json").toFile());
        fileWriter.write(manifestJson);
        fileWriter.close();

        Path zipPath = Files.createTempFile("proxy_auth_plugin", ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            ZipEntry e = new ZipEntry("background.js");
            zos.putNextEntry(e);
            zos.write(backgroundJs.getBytes());
            zos.closeEntry();

            e = new ZipEntry("manifest.json");
            zos.putNextEntry(e);
            zos.write(manifestJson.getBytes());
            zos.closeEntry();
        }

        options.addExtensions(new File(zipPath.toString()));
    }
}
