package com.StardewValley.models;

import java.util.*;

public class PathFinder {
    public static Optional<Path> findShortest(Tile[][] map, Tile start, Tile goal) {
        int H = map.length, W = map[0].length;
        boolean[][] vis = new boolean[H][W];
        Tile[][] par = new Tile[H][W];
        Queue<Tile> q = new ArrayDeque<>();
        q.add(start);
        vis[start.getPositionY()][start.getPositionX()] = true;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!q.isEmpty()) {
            var t = q.poll();
            if (t == goal) break;
            for (var d : dirs) {
                int nx = t.getPositionX() + d[0], ny = t.getPositionY() + d[1];
                if (nx >= 0 && ny >= 0 && nx < W && ny < H && !vis[ny][nx] && map[ny][nx].isPassable()) {
                    vis[ny][nx] = true;
                    par[ny][nx] = t;
                    q.add(map[ny][nx]);
                }
            }
        }
        if (!vis[goal.getPositionY()][goal.getPositionX()]) return Optional.empty();
        List<Tile> rev = new ArrayList<>();
        Tile cur = goal;
        while (cur != null) {
            rev.add(cur);
            cur = par[cur.getPositionY()][cur.getPositionX()];
        }
        Collections.reverse(rev);
        return Optional.of(new Path(rev));
    }
}
