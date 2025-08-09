package com.StardewValley;

import com.StardewValley.controller.RegisterController;
import com.StardewValley.view.PreMenuView;
import com.badlogic.gdx.Game;


public class Main extends Game {
    @Override
    public void create() {
        RegisterController registerController = new RegisterController();
        registerController.register("kian","Kiangh84@","Kiangh84@","kgh","kk@kk.com","male");
        registerController.register("reza","Rezagh84@","Rezagh84@","rez","kk@kk.com","male");
        registerController.register("Amirreza","Test123@","Test123@","amir","amir@gmail.com","male");
        registerController.register("Mehrshad","Test123@","Test123@","mehrshad","mehrshad@gmail.com","male");
        setScreen(new PreMenuView(this));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
