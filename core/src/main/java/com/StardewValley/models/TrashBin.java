package com.StardewValley.models;

import java.util.List;

import java.util.List;

public class TrashBin {
    private String type;
    private List<Item> items;
    private double refundRate;

    public void setRefundRate(double refundRate) {
        this.refundRate = refundRate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        setRefundRateBasedOnType();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public double getRefundRate() {
        return refundRate;
    }

    private void setRefundRateBasedOnType(){
    }


    public void addItem(Item item) {
    }


    public int calculateRefund(Item item) {
        return 0;
    }

    public void clearTrashBin() {
    }
}
