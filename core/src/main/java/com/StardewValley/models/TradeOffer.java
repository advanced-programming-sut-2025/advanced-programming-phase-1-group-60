package com.StardewValley.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TradeOffer implements Serializable {
    private String requester;
    private String receiver;
    private List<Item> offeredItems = new ArrayList<>();
    private List<Item> requestedItems = new ArrayList<>();
    private int offeredMoney = 0;
    private int requestedMoney = 0;
    private transient boolean submitted = false;
    private transient boolean finalized = false;

    public TradeOffer(String requester, String receiver) {
        this.requester = requester;
        this.receiver = receiver;
    }

    // Getters
    public String getRequester() { return requester; }
    public String getReceiver() { return receiver; }
    public List<Item> getOfferedItems() { return offeredItems; }
    public List<Item> getRequestedItems() { return requestedItems; }
    public int getOfferedMoney() { return offeredMoney; }
    public int getRequestedMoney() { return requestedMoney; }
    public boolean isSubmitted() { return submitted; }
    public boolean isFinalized() { return finalized; }

    // Setters
    public void setOfferedItems(List<Item> offeredItems) { this.offeredItems = offeredItems; }
    public void setRequestedItems(List<Item> requestedItems) { this.requestedItems = requestedItems; }
    public void setOfferedMoney(int offeredMoney) { this.offeredMoney = offeredMoney; }
    public void setRequestedMoney(int requestedMoney) { this.requestedMoney = requestedMoney; }
    public void setSubmitted(boolean submitted) { this.submitted = submitted; }
    public void setFinalized(boolean finalized) { this.finalized = finalized; }
}
