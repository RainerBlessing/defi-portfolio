package org.defichain.portfolio.settings;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.defichain.portfolio.settings.properties.DefiAddress;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItems;

public class DefiAddressHandlerTest {
    private String tmpdir;

    @Test
    public void saveAddresses_loadAddresses() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        DefiAddress address1 = new DefiAddress("xyz","test address 1");
        DefiAddress address2 = new DefiAddress("abc","test address 2");
        DefiAddress address3 = new DefiAddress("","");

        DefiAddressHandler defiAddressHandler = new DefiAddressHandler(tmpdir);

        defiAddressHandler.addAddress(address1);
        defiAddressHandler.addAddress(address2);
        defiAddressHandler.addAddress(address3);

        defiAddressHandler.saveAddresses();
        defiAddressHandler.loadAddresses();

        Set<DefiAddress> addresses = defiAddressHandler.getAddresses();

        //noinspection unchecked
        assertThat(addresses, hasItems(equalTo(address1),equalTo(address2)));
        assertThat(addresses, not(hasItems(equalTo(address3))));
    }

    @Test
    public void  doNotAddEmptyAddress(){
        DefiAddress address1 = new DefiAddress("","");

        DefiAddressHandler defiAddressHandler = new DefiAddressHandler(tmpdir);

        defiAddressHandler.addAddress(address1);

        Set<DefiAddress> addresses = defiAddressHandler.getAddresses();

        assertThat(addresses.size(), is(0));

    }

    @BeforeTest
    public void setup() throws IOException {
        tmpdir = Files.createTempDirectory("defiPortfolioTempDir").toFile().getAbsolutePath();
    }

}