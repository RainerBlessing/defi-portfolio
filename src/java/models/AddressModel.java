package models;

import javafx.beans.property.*;

public class AddressModel {
    private final LongProperty confirmationProperty = new SimpleLongProperty(0);
    private final StringProperty labelProperty = new SimpleStringProperty("");
    private final StringProperty typeProperty = new SimpleStringProperty("");
    private final StringProperty addressProperty = new SimpleStringProperty("");
    private final DoubleProperty amountProperty = new SimpleDoubleProperty(0.0);
    private final ObjectProperty<String[]> transactionProperty = new SimpleObjectProperty<>();

    public  AddressModel(String address, Double amount, Long confirmations, String label, String[] transactionsID) {
        this.addressProperty.set(address);
        this.confirmationProperty.set(confirmations);
        this.amountProperty.set(amount);
        this.labelProperty.set(label);
        this.transactionProperty.set(transactionsID);
    }

    public void setAddress(String owner) {
        this.addressProperty.set(owner);
    }

    public String getaddressValue() {
        return addressProperty.get();
    }

    public StringProperty getAddress() {
        return addressProperty;
    }

    public void setType(String owner) {
        this.typeProperty.set(owner);
    }

    public String getTypeValue() {
        return typeProperty.get();
    }

    public StringProperty getType() {
        return typeProperty;
    }

    public void setLabel(String label) {
        this.labelProperty.set(label);
    }

    public String getLabelValue() {
        return labelProperty.get();
    }

    public StringProperty getLabel() {
        return labelProperty;
    }

    public void setAmount(Double amount) {
        this.amountProperty.set(amount);
    }

    public Long getConfirmationsValue() {
        return confirmationProperty.get();
    }

    public LongProperty getConfirmations() {
        return confirmationProperty;
    }

    public void setConfirmations(Long confirmations) {
        this.confirmationProperty.set(confirmations);
    }

    public Double getAmountValue() {
        return amountProperty.get();
    }

    public DoubleProperty getAmount() {
        return amountProperty;
    }

    public  ObjectProperty<String[]> getTransaction() {
        return transactionProperty;
    }
    public void setTransaction(String[] transaction) {
        this.transactionProperty.set(transaction);
    }
    public String[] getTransactionValue() {
        return transactionProperty.get();
    }

}
