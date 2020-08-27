package on.bondora.webhook;

import com.github.rholder.retry.*;
import on.bondora.webhook.page.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public class BondoraAutomator {


    public static void main(String[] args) throws Exception {
        if (args.length<2){
            throw new IllegalArgumentException("need user / password");
        }
        try {
            runTest(args[0], args[1]);
            System.out.println("ALL GOOD");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error" + e.getMessage());
        }finally {
            DriverFactory.getDriverFactory().getRemoteWebDriver().quit();
        }
    }

    private static void runTest(String user, String password) throws ExecutionException, RetryException {
        DriverFactory.getDriverFactory().getRemoteWebDriver().get("https://api.bondora.com/login");
        findElement(By.id("email")).clear();
        findElement(By.id("email")).sendKeys(user);
        findElement(By.name("password")).clear();
        findElement(By.name("password")).sendKeys(password);

        findElement(By.xpath("//button[@type='submit']")).click();
        findElement(By.linkText("Applications")).click();
        findElement(By.linkText("Your applications")).click();
        findElement(By.linkText("Webhooks")).click();
        findElement(By.xpath("//button[@type='submit']")).click();
    }


    private static WebElement findElement(By by) throws ExecutionException, RetryException {
        RemoteWebDriver remoteWebDriver = DriverFactory.getDriverFactory().getRemoteWebDriver();
        Callable<WebElement> callable = () -> remoteWebDriver.findElement(by);
        return retryOnCondition(e -> !e.isDisplayed() ||!e.isEnabled(), callable, 300, 10);
    }


    public static <T> T retryOnCondition(Predicate<T> p, Callable<T> callable, long timeMs, int attempt) throws ExecutionException, RetryException {
        AtomicReference<T> atomicReference = new AtomicReference();
        Retryer<Boolean> build = RetryerBuilder.<Boolean>newBuilder()
                .withWaitStrategy(WaitStrategies.fixedWait(timeMs, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(attempt))
                .retryIfResult(isRetry -> isRetry)
                .build();
        build.call(() -> {
            atomicReference.set(callable.call());
            return p.test(atomicReference.get());
        });
        return atomicReference.get();
    }
}
