package org.defichain.portfolio.settings;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PortfolioSystemTest {

    @Test(dataProvider = "provideSystemTypes")
    public void testDefiPortfolioHome(SystemType systemType, String directory) throws IOException {
        SystemWrapper systemWrapper = new TestSystemWrapper();
        PortfolioSystem portfolioSystem = new PortfolioSystem(systemWrapper, systemType);

        assertThat(portfolioSystem.getDefiPortfolioHome(), is(directory));
    }

    @DataProvider(name = "provideSystemTypes")
    public Object[][] provideData() {

        return new Object[][]{
                {SystemType.LINUX, "/home/tester/PortfolioData/"},
                {SystemType.OSX, "/home/tester/PortfolioData/"},
                {SystemType.WINDOWS, "C:/Users/tester/AppData/Roaming/defi-portfolio/"}
        };
    }
}