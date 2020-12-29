package com.masteringselenium;

import com.masteringselenium.config.DriverFactory;
import com.masteringselenium.fluent_page_objects.LoginPage;
import com.masteringselenium.listeners.ScreenshotListener;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Listeners(ScreenshotListener.class)
public class DriverBase {

    private static List<DriverFactory> webDriverThreadPool = Collections.synchronizedList(new ArrayList<DriverFactory>());
    private static ThreadLocal<DriverFactory> driverThread;

    protected LoginPage loginPage;

    @BeforeSuite(alwaysRun = true)
    public static void instantiateDriverObject() {
        driverThread = new ThreadLocal<DriverFactory>() {
            @Override
            protected DriverFactory initialValue() {
                DriverFactory webDriverThread = new DriverFactory();
                webDriverThreadPool.add(webDriverThread);
                return webDriverThread;
            }
        };
    }

    public static RemoteWebDriver getDriver() throws MalformedURLException {
        return driverThread.get().getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public static void clearCookies() {
        try {
            getDriver().manage().deleteAllCookies();
        } catch (Exception ex) {
            System.err.println("Unable to delete cookies: " + ex);
        }
    }

    @AfterSuite(alwaysRun = true)
    public static void closeDriverObjects() {
        for (DriverFactory webDriverThread : webDriverThreadPool) {
            webDriverThread.quitDriver();
        }
    }
}