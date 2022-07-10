package org.defichain.portfolio.settings;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.defichain.portfolio.settings.properties.DefiAddress;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;

public class DefiAddressHandlerTest {
    private String tmpdir;

    @Test
    public void saveAddresses_loadAddresses() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        DefiAddressHandler defiAddressHandler = new DefiAddressHandler(tmpdir);

        DefiAddress address1 = new DefiAddress("xyz","test address 1");
        DefiAddress address2 = new DefiAddress("abc","test address 2");

        defiAddressHandler.addAddress(address1);
        defiAddressHandler.addAddress(address2);

        defiAddressHandler.saveAddresses();
        defiAddressHandler.loadAddresses();

        Set<DefiAddress> addresses = defiAddressHandler.getAddresses();

        //noinspection unchecked
        assertThat(addresses, hasItems(equalTo(address1),equalTo(address2)));
    }

    @BeforeTest
    public void setup() throws IOException {
        tmpdir = Files.createTempDirectory("defiPortfolioTempDir").toFile().getAbsolutePath();
    }

}