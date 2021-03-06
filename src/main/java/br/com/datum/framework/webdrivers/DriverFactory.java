package br.com.datum.framework.webdrivers;

import br.com.datum.framework.utils.Property;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.service.ExtentTestManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverFactory {
    public static WebDriver createInstance() {
        String browser = System.getProperty("browser");
        String type = System.getProperty("type");

        if (StringUtils.isEmpty(browser)){
            browser = Property.get("browser");
            System.setProperty("browser", browser);
        }

        if (StringUtils.isEmpty(type)){
            type = Property.get("type");
            System.setProperty("type", type);
        }

        try {
            switch (browser) {
                case "firefox":
                    if (type.equals("remote")) {
                        WebDriverManager.firefoxdriver().setup();
                        return new FirefoxDriver();
                    } else if(type.equals("headless")) {
                        WebDriverManager.firefoxdriver().setup();
                        FirefoxOptions firefoxLocalOptions = new FirefoxOptions();
                        firefoxLocalOptions.setHeadless(true);
                        return new FirefoxDriver(firefoxLocalOptions);
                    } else {
                        WebDriverManager.firefoxdriver().setup();
                        return new FirefoxDriver();
                    }
                case "chrome":
                    if (type.equals("remote")) {
                        return DriverRemote.connect(new ChromeOptions());
                    } else if(type.equals("headless")) {
                        WebDriverManager.chromedriver().setup();
                        ChromeOptions chromeLocalOptions = new ChromeOptions();
                        chromeLocalOptions.addArguments("--headless");
                        chromeLocalOptions.addArguments("--disable-gpu");
                        chromeLocalOptions.addArguments("--no-sandbox");
                        chromeLocalOptions.addArguments("--disable-dev-shm-usage");
                        return new ChromeDriver(chromeLocalOptions);
                    } else {
                        WebDriverManager.chromedriver().setup();
                        return new ChromeDriver();
                    }
                default:
                    String message = "DriverFactory.getInstance() recebeu um argumento inv??lido.";
                    ExtentTestManager.getTest().log(Status.FATAL, message);
                    throw new IllegalArgumentException(message);
            }
        } catch (SessionNotCreatedException e) {
            String message = "Sess??o n??o criada, vers??o do driver n??o suportada.";
            ExtentTestManager.getTest().log(Status.FATAL, message);
            throw new SessionNotCreatedException(message, e);
        } catch (WebDriverException e) {
            String message = "N??o foi poss??vel encontrar o bin??rio do driver.";
            ExtentTestManager.getTest().log(Status.FATAL, message);
            throw new WebDriverException(message, e);
        }
    }
}