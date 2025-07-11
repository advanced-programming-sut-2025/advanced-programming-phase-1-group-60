package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.GameController;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.controller.MainController;
import com.StardewValley.controller.ProfileController;
import com.StardewValley.models.Result;
import com.StardewValley.repository.UserRepository;
import com.StardewValley.view.commands.MainCommands;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Scanner;

public class MainView implements Screen {
    private final Game game;
    private final Stage stage;
    private final MenuManager menuManager;
    private final SpriteBatch batch;
    private final LoginMenuController loginController;

    public MainView(Game game, LoginMenuController loginController) {
        this.game = game;
        this.loginController = loginController;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.batch = new SpriteBatch();

        createUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(50);

        // Title
        Label titleLabel = new Label("Main Menu", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        // Welcome message with username
        Label userLabel = new Label("Welcome, " + loginController.getLoggedInUser().getUsername(),
            menuManager.getPixthulhuSkin());
        userLabel.setColor(0.95f, 0.92f, 0.82f, 1f);

        // Buttons
        TextButton gameButton = new TextButton("Start Game", menuManager.getPixthulhuSkin());
        TextButton profileButton = new TextButton("Profile", menuManager.getPixthulhuSkin());
        TextButton logoutButton = new TextButton("Logout", menuManager.getPixthulhuSkin());

        // Button styling
        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

        gameButton.setColor(buttonColor);
        profileButton.setColor(buttonColor);
        logoutButton.setColor(buttonColor);

        gameButton.getLabel().setColor(textColor);
        profileButton.getLabel().setColor(textColor);
        logoutButton.getLabel().setColor(textColor);

        // Layout
        mainTable.add(titleLabel).padBottom(30).row();
        mainTable.add(userLabel).padBottom(50).row();
        mainTable.add(gameButton).width(300).height(100).padBottom(20).row();
        mainTable.add(profileButton).width(300).height(100).padBottom(20).row();
        mainTable.add(logoutButton).width(300).height(100).row();

        // Button listeners
        gameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                //game.setScreen(new GameView(game));
            }
        });

        profileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new ProfileView(game, loginController));
            }
        });

        logoutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new PreMenuView(game));
            }
        });

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        menuManager.updateBackgroundAnimation(delta);
        float[] positions = menuManager.getBackgroundPositions();

        batch.begin();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        for (float position : positions) {
            batch.draw(menuManager.getBackgroundLayer(),
                position, 0,
                width, height);
        }

        batch.draw(menuManager.getMiddlegroundLayer(), 0, 0, width, height);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }
}
