package models;

import javafx.beans.property.SimpleStringProperty;

public class Addresses {
        private SimpleStringProperty address;
        public Addresses(String address){
            this.address = new SimpleStringProperty(address);
        }
        public String getAddress()
        {
            return this.address.get();
        }
        public void setAddress(String ad){
            this.address=new SimpleStringProperty(ad);
        }
    }
