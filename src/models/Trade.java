package models;

import java.util.List;

public class Trade {
    private int id;
    private User fromUser;
    private User toUser;
    private List<Item> offeredItems;
    private List<Item> requestedItems;
    private int offeredMoney;
    private int requestedMoney;
    private boolean isAccepted;
    private String timestamp;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public List<Item> getOfferedItems() {
        return offeredItems;
    }

    public void setOfferedItems(List<Item> offeredItems) {
        this.offeredItems = offeredItems;
    }

    public List<Item> getRequestedItems() {
        return requestedItems;
    }

    public void setRequestedItems(List<Item> requestedItems) {
        this.requestedItems = requestedItems;
    }

    public int getOfferedMoney() {
        return offeredMoney;
    }

    public void setOfferedMoney(int offeredMoney) {
        this.offeredMoney = offeredMoney;
    }

    public int getRequestedMoney() {
        return requestedMoney;
    }

    public void setRequestedMoney(int requestedMoney) {
        this.requestedMoney = requestedMoney;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public void addOfferedItem(Item item) {
    }

    public void addRequestedItem(Item item) {
    }


    public void acceptTrade() {

    }


    public void rejectTrade() {
    }
}