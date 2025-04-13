package models;

import java.util.ArrayList;
import java.util.List;

public class Quest {
    private List<Item> requests = new ArrayList<>();
    private Reward rewards;

    public List<Item> getRequests() {
        return requests;
    }

    public void setRequests(List<Item> requests) {
        this.requests = requests;
    }

    public Reward getRewards() {
        return rewards;
    }

    public void setRewards(Reward rewards) {
        this.rewards = rewards;
    }
}
