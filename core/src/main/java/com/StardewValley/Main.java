package com.StardewValley;

import com.StardewValley.view.PreMenuView;
import com.badlogic.gdx.Game;


public class Main extends Game {
    @Override
    public void create() {
        setScreen(new PreMenuView(this));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
