package models;

import java.util.*;

public class Path {
    private final List<Tile> steps;

    public Path(List<Tile> s) {
        this.steps = s;
    }

    public List<Tile> getSteps() {
        return steps;
    }

    public int getDistance() {
        return steps.size() - 1;
    }

    public int getTurns() {
        if (steps.size() < 3) return 0;
        int turns = 0;
        int dx0 = steps.get(1).getPositionX() - steps.get(0).getPositionX();
        int dy0 = steps.get(1).getPositionY() - steps.get(0).getPositionY();
        for (int i = 2; i < steps.size(); i++) {
            int dx = steps.get(i).getPositionX() - steps.get(i - 1).getPositionX();
            int dy = steps.get(i).getPositionY() - steps.get(i - 1).getPositionY();
            if (dx != dx0 || dy != dy0) {
                turns++;
                dx0 = dx;
                dy0 = dy;
            }
        }
        return turns;
    }
}