package com.StardewValley;

import com.StardewValley.controller.RegisterController;
import com.StardewValley.Network.Client.ClientMain;
import com.StardewValley.repository.UserRepository;
import com.StardewValley.view.PreMenuView;
import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        UserRepository.getInstance().loadUsers();
        RegisterController registerController = new RegisterController();
        registerController.register("kian","Kiangh84@","Kiangh84@","kgh","kk@kk.com","male");
        registerController.register("reza","Rezagh84@","Rezagh84@","rez","kk@kk.com","male");
        setScreen(new PreMenuView(this));
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
