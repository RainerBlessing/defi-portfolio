package models;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BalanceModel{

    public BalanceModel(String tokenName1, double fiatValue1, double cryptoValue1,String tokenName2, double fiatValue2, double cryptoValue2,Double shareValue){
        setToken1Name(tokenName1);
        setFiat1(fiatValue1);
        setCrypto1(cryptoValue1);
        setToken2Name(tokenName2);
        setFiat2(fiatValue2);
        setCrypto2(cryptoValue2);
        setShare(shareValue);
    }
    private final StringProperty tokenName1Property = new SimpleStringProperty("");
    private final DoubleProperty fiat1Property = new SimpleDoubleProperty(0.0);
    private final DoubleProperty crypto1Property = new SimpleDoubleProperty(0.0);
    private final StringProperty tokenName2Property = new SimpleStringProperty("");
    private final DoubleProperty fiat2Property = new SimpleDoubleProperty(0.0);
    private final DoubleProperty crypto2Property = new SimpleDoubleProperty(0.0);
    private final DoubleProperty shareProperty = new SimpleDoubleProperty(0.0);

    public void setToken1Name(String blockTime) {
        this.tokenName1Property.set(blockTime);
    }

    public StringProperty getToken1Name() {
        return tokenName1Property;
    }

    public String getToken1NameValue() {
        return tokenName1Property.getValue();
    }

    public Double getCrypto1Value() {
        return crypto1Property.getValue();
    }

    public DoubleProperty getCrypto1() {
        return crypto1Property;
    }

    public void setCrypto1(Double value) {
        this.crypto1Property.set(value);
    }

    public Double getFiat1Value() {
        return fiat1Property.getValue();
    }

    public DoubleProperty getFiat1() {
        return fiat1Property;
    }

    public void setFiat1(Double value) {
        this.fiat1Property.set(value);
    }

    public void setToken2Name(String blockTime) {
        this.tokenName2Property.set(blockTime);
    }

    public StringProperty getToken2Name() {
        return tokenName2Property;
    }

    public String getToken2NameValue() {
        return tokenName2Property.getValue();
    }

    public Double getCrypto2Value() {
        return crypto2Property.getValue();
    }

    public DoubleProperty getCrypto2() {
        return crypto2Property;
    }

    public void setCrypto2(Double value) {
        this.crypto2Property.set(value);
    }

    public Double getFiat2Value() {
        return fiat2Property.getValue();
    }

    public DoubleProperty getFiat2() {
        return fiat2Property;
    }

    public void setFiat2(Double value) {
        this.fiat2Property.set(value);
    }

    public Double getShareValue() {
        return shareProperty.getValue();
    }

    public DoubleProperty getShare() {
        return shareProperty;
    }

    public void setShare(Double value) {
        this.shareProperty.set(value);
    }
}
