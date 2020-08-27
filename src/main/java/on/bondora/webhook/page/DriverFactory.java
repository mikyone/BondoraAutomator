package on.bondora.webhook.page;

import lombok.SneakyThrows;
import on.bondora.webhook.SimpleTest;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class DriverFactory {
    private RemoteWebDriver remoteWebDriver = null;
    private URL url = null;
    private boolean useIosDriverNativeEvents = true;
    private boolean isStandaloneRun = false;
    private boolean maximizeFirefox = false;


    private static final ThreadLocal<DriverFactory> DRIVER_FACTORY_STORAGE = new ThreadLocal<DriverFactory>() {
        protected DriverFactory initialValue() {
            return new DriverFactory();
        }
    };

    private DriverFactory() {

    }

    public static DriverFactory getDriverFactory() {
        return DRIVER_FACTORY_STORAGE.get();
    }

    @SneakyThrows
    public RemoteWebDriver getRemoteWebDriver() {
        synchronized (this) {
            if (this.remoteWebDriver == null) {
                String path = getPath();

                System.setProperty("webdriver.chrome.driver", path);
                this.remoteWebDriver = new ChromeDriver();
                this.remoteWebDriver.manage().timeouts().setScriptTimeout(120, TimeUnit.SECONDS);
            }
        }
        return remoteWebDriver;

    }

    private String getPath() {
        String envDriver = System.getenv().get("WEB_DRIVER");
        String extension = System.getProperty("os.name").toLowerCase().contains("win") ? ".exe" : "";
        String chromeDriver = "chromedriver" + extension;
        if(envDriver != null){
            System.out.println("find in var " + envDriver);
            return envDriver + chromeDriver;
        }else {
            System.out.println("find in path ");
            return Paths.get("./drivers/" + chromeDriver).toAbsolutePath().toString();
        }
    }

    private void setUrl(URL u) {
        synchronized (this) {
            url = u;
        }
    }

    public URL getURL() {
        synchronized (this) {
            return url;
        }
    }

}
