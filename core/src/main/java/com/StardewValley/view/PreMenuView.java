package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PreMenuView implements Screen {
    private final Game game;
    private Stage stage;
    private SpriteBatch batch;
    private MenuManager menuManager;

    public PreMenuView(Game game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.menuManager = MenuManager.getInstance();
        createUI();
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Title setup
        Label titleLabel = new Label("Stardew Valley", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        // Create buttons
        TextButton loginButton = new TextButton("Login", menuManager.getPixthulhuSkin());
        TextButton registerButton = new TextButton("Register", menuManager.getPixthulhuSkin());
        TextButton exitButton = new TextButton("Exit", menuManager.getPixthulhuSkin());

        // Button colors
        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

        // Apply colors
        loginButton.setColor(buttonColor);
        registerButton.setColor(buttonColor);
        exitButton.setColor(buttonColor);

        loginButton.getLabel().setColor(textColor);
        registerButton.getLabel().setColor(textColor);
        exitButton.getLabel().setColor(textColor);

        // Layout dimensions
        float buttonWidth = 300;
        float buttonHeight = 100;
        float buttonPadding = 15;
        float titlePadding = 80;

        // Layout setup
        mainTable.top();
        mainTable.padTop(50);
        mainTable.add(titleLabel).padBottom(titlePadding).row();

        // Button table
        Table buttonTable = new Table();
        buttonTable.defaults().pad(buttonPadding);
        buttonTable.add(loginButton).width(buttonWidth).height(buttonHeight).row();
        buttonTable.add(registerButton).width(buttonWidth).height(buttonHeight).row();
        buttonTable.add(exitButton).width(buttonWidth).height(buttonHeight);

        mainTable.add(buttonTable).expand().top();

        // Button listeners
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose(); // Clean up current screen resources
                LoginView loginView = new LoginView(game);
                game.setScreen(loginView);
            }
        });

        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose(); // Clean up current screen resources
                RegisterView registerView = new RegisterView(game);
                game.setScreen(registerView);
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
    public void dispose() {
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
