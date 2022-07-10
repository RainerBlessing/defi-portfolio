package org.defichain.portfolio.settings.properties;

import com.opencsv.bean.CsvBindByName;

import java.util.Objects;

public class DefiAddress {
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @CsvBindByName(column = "Address")
    private String address;
    @CsvBindByName(column = "Comment")
    private String comment;

    public DefiAddress() {
    }

    public DefiAddress(String address, String comment) {
        this.address = address;
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefiAddress that = (DefiAddress) o;
        return Objects.equals(address, that.address) && Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, comment);
    }
}
