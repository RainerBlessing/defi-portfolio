package models;

import javafx.beans.property.*;

public class PoolPairModel {

    private final StringProperty blockTimeProperty = new SimpleStringProperty("");
    private final DoubleProperty fiatValueProperty = new SimpleDoubleProperty(0.0);
    private final DoubleProperty cryptoValue1Property = new SimpleDoubleProperty(0.0);
    private final DoubleProperty cryptoValueFiat1Property = new SimpleDoubleProperty(0.0);
    private final DoubleProperty cryptoValueFiat2Property = new SimpleDoubleProperty(0.0);
    private final DoubleProperty cryptoValue2Property = new SimpleDoubleProperty(0.0);
    private final DoubleProperty cryptoCommission1OverviewProperty = new SimpleDoubleProperty(0.0);
    private final DoubleProperty cryptoCommission1OverviewFiatProperty = new SimpleDoubleProperty(0.0);
    private final DoubleProperty cryptoCommission2OverviewProperty = new SimpleDoubleProperty(0.0);
    private final DoubleProperty cryptoCommission2OverviewFiatProperty = new SimpleDoubleProperty(0.0);
    private final StringProperty cryptoPoolPair = new SimpleStringProperty("");
    private final StringProperty balanceFiatProperty = new SimpleStringProperty("");

    public PoolPairModel(String blockTime, double fiatValue, double cryptoValue1, double cryptoValue2, String poolPair,double cryptoValueFiat1, double cryptoValueFiat2,double cryptoCommission2Overview,double cryptoCommission2FiatOverview,String balanceFiat){
        setBlockTime(blockTime);
        setFiat(fiatValue);
        setCrypto1(cryptoValue1);
        setCrypto2(cryptoValue2);
        setCryptoFiat1(cryptoValueFiat1);
        setCryptoFiat2(cryptoValueFiat2);
        setPoolPair(poolPair);
        setcryptoCommission2Overview(cryptoCommission2Overview);
        setcryptoCommission2FiatOverview(cryptoCommission2FiatOverview);
        setBalanceFiat(balanceFiat);
    }

    public void setBlockTime(String blockTime) {
        this.blockTimeProperty.set(blockTime);
    }

    public StringProperty getBlockTime() {
        return blockTimeProperty;
    }

    public String getBlockTimeValue() {
        return blockTimeProperty.getValue();
    }

    public void setBalanceFiat(String balanceFiat) {
        this.balanceFiatProperty.set(balanceFiat);
    }

    public StringProperty getBalanceFiat() {
        return balanceFiatProperty;
    }

    public String getBalanceFiatValue() {
        return balanceFiatProperty.getValue();
    }

    public Double getCryptoValueValue1() {
        return cryptoValue1Property.getValue();
    }

    public DoubleProperty getCryptoValue1() {
        return cryptoValue1Property;
    }

    public void setCrypto1(Double value) {
        this.cryptoValue1Property.set(value);
    }

    public Double getCryptoValueValue2() {
        return cryptoValue2Property.getValue();
    }

    public DoubleProperty getCryptoValue2() {
        return cryptoValue2Property;
    }

    public void setCrypto2(Double value) {
        this.cryptoValue2Property.set(value);
    }

    public Double getCryptoValueFiatValue1() {
        return cryptoValueFiat1Property.getValue();
    }

    public DoubleProperty getCryptoFiatValue1() {
        return cryptoValueFiat1Property;
    }

    public void setCryptoFiat1(Double value) {
        this.cryptoValueFiat1Property.set(value);
    }

    public Double getCryptoValueFiatValue2() {
        return cryptoValueFiat2Property.getValue();
    }

    public DoubleProperty getCryptoFiatValue2() {
        return cryptoValueFiat2Property;
    }

    public void setCryptoFiat2(Double value) {
        this.cryptoValueFiat2Property.set(value);
    }

    public Double getFiatValueValue() {
        return fiatValueProperty.getValue();
    }

    public DoubleProperty getFiatValue() {
        return fiatValueProperty;
    }

    public void setFiat(Double value) {
        this.fiatValueProperty.set(value);
    }


    public Double getcryptoCommission1FiatOverviewvalue() { return cryptoCommission1OverviewFiatProperty.getValue(); }

    public DoubleProperty getcryptoCommission1FiatOverview() {
        return cryptoCommission1OverviewFiatProperty;
    }

    public void setcryptoCommission1FiatOverview(Double value) { this.cryptoCommission1OverviewFiatProperty.set(value);}


    public Double getcryptoCommission1Overviewvalue() { return cryptoCommission1OverviewProperty.getValue(); }

    public DoubleProperty getcryptoCommission1Overview() {
        return cryptoCommission1OverviewProperty;
    }

    public void setcryptoCommission1Overview(Double value) { this.cryptoCommission1OverviewProperty.set(value);}


    public Double getcryptoCommission2Overviewvalue() {
        return cryptoCommission2OverviewProperty.getValue();
    }

    public DoubleProperty getcryptoCommission2Overview() {
        return cryptoCommission2OverviewProperty;
    }

    public void setcryptoCommission2Overview(Double value) {
        this.cryptoCommission2OverviewProperty.set(value);
    }



    public Double getcryptoCommission2FiatOverviewvalue() {
        return cryptoCommission2OverviewFiatProperty.getValue();
    }

    public DoubleProperty getcryptoCommission2FiaOtverview() {
        return cryptoCommission2OverviewFiatProperty;
    }

    public void setcryptoCommission2FiatOverview(Double value) { this.cryptoCommission2OverviewFiatProperty.set(value);}




    public void setPoolPair(String pair) {
        this.cryptoPoolPair.set(pair);
    }

    public StringProperty getPoolPair() {
        return cryptoPoolPair;
    }

    public String getPoolPairValue() {
        return cryptoPoolPair.getValue();
    }


}