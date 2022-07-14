package org.defichain.portfolio.settings;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.lang3.StringUtils;
import org.defichain.portfolio.settings.properties.DefiAddress;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefiAddressHandler {
    public static final String DEFI_ADDRESSES_CSV = "defi-addresses.csv";
    private final String defiPortFolioAddressCsv;
    private Set<DefiAddress> addresses = new HashSet<>();

    public DefiAddressHandler(String defiPortFolioHome) {
        this.defiPortFolioAddressCsv = defiPortFolioHome + "/" + DEFI_ADDRESSES_CSV;
    }

    public void saveAddresses() throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {

        Writer writer = new FileWriter(defiPortFolioAddressCsv);
        StatefulBeanToCsv<DefiAddress> beanToCsv = new StatefulBeanToCsvBuilder<DefiAddress>(writer).build();
        beanToCsv.write(List.copyOf(addresses).stream().filter(DefiAddress::isNotBlank));
        writer.close();
    }

    public void loadAddresses() throws FileNotFoundException {
        this.addresses.clear();
        addresses = new HashSet<>(new CsvToBeanBuilder<DefiAddress>(new FileReader(defiPortFolioAddressCsv)).withType(DefiAddress.class).build().parse());
    }

    public void addAddress(String address, String comment) {
        this.addresses.add(new DefiAddress(address, comment));
    }

    public Set<DefiAddress> getAddresses() {
        return addresses;
    }

    public void addAddress(DefiAddress address) {
         this.addresses.add(address);
    }
}
