package models;

import javafx.beans.property.*;

public class PortfolioModel {

    private final StringProperty dateProperty = new SimpleStringProperty("");
    private final DoubleProperty coinRewardsProperty = new SimpleDoubleProperty(0.0);
    private final DoubleProperty coinCommissions1Property = new SimpleDoubleProperty(0.0);
    private final DoubleProperty coinCommissions2Property = new SimpleDoubleProperty(0.0);

    private final DoubleProperty fiatRewardsProperty = new SimpleDoubleProperty(0.0);
    private final DoubleProperty fiatCommissions1Property = new SimpleDoubleProperty(0.0);
    private final DoubleProperty fiatCommissions2Property = new SimpleDoubleProperty(0.0);
    private final StringProperty poolPairPorperty = new SimpleStringProperty("");
    private String intervall;

    public PortfolioModel(String date, double fiatRewards, double fiatCommissions1, double fiatCommissions2,double coinRewards, double coinCommissions1, double coinCommissions2, String poolPair,String intervall){
        this.intervall = intervall;
        setDate(date);
        setFiatRewards(fiatRewards);
        setFiatCommissions1(fiatCommissions1);
        setFiatCommissions2(fiatCommissions2);
        setCoinRewards(coinRewards);
        setCoinCommissions1(coinCommissions1);
        setCoinCommissions2(coinCommissions2);
        setPoolPair(poolPair);
    }

    public void setDate(String blockTime) {
        this.dateProperty.set(blockTime);
    }

    public StringProperty getDate() {
        return dateProperty;
    }

    public String getDateValue() {
        return dateProperty.getValue();
    }

    public Double getFiatRewards1Value() {
        return fiatRewardsProperty.getValue();
    }

    public DoubleProperty getFiatRewards() {
        return fiatRewardsProperty;
    }

    public void setFiatRewards(Double value) {
        this.fiatRewardsProperty.set(value);
    }

    public Double getFiatCommissions1Value() {
        return fiatCommissions1Property.getValue();
    }

    public DoubleProperty getFiatCommissions1() {
        return fiatCommissions1Property;
    }

    public void setFiatCommissions1(Double value) {
        this.fiatCommissions1Property.set(value);
    }

    public Double getFiatCommissions2Value() {
        return fiatCommissions2Property.getValue();
    }

    public DoubleProperty getFiatCommissions2() {
        return fiatCommissions2Property;
    }

    public void setFiatCommissions2(Double value) {
        this.fiatCommissions2Property.set(value);
    }

    public Double getCoinRewards1Value() {
        return coinRewardsProperty.getValue();
    }

    public DoubleProperty getCoinRewards() {
        return coinRewardsProperty;
    }

    public void setCoinRewards(Double value) {
        this.coinRewardsProperty.set(value);
    }

    public Double getCoinCommissions1Value() {
        return coinCommissions1Property.getValue();
    }

    public DoubleProperty getCoinCommissions1() {
        return coinCommissions1Property;
    }

    public void setCoinCommissions1(Double value) {
        this.coinCommissions1Property.set(value);
    }

    public Double getCoinCommissions2Value() {
        return coinCommissions2Property.getValue();
    }

    public DoubleProperty getCoinCommissions2() {
        return coinCommissions2Property;
    }

    public void setCoinCommissions2(Double value) {
        this.coinCommissions2Property.set(value);
    }

    public void setPoolPair(String value) {
        this.poolPairPorperty.set(value);
    }

    public StringProperty getPoolPair() {
        return poolPairPorperty;
    }

    public String getPoolPairValue() {
        return poolPairPorperty.getValue();
    }

    public String getIntervall(){
        return intervall;
    }
    public void setIntervall(String intervall){
        this.intervall =intervall;
    }
}