package com.StardewValley.view;

import java.util.Scanner;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.controller.ProfileController;
import com.StardewValley.models.Result;
import com.StardewValley.models.User;
import com.StardewValley.view.commands.ProfileCommands;
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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class ProfileView implements Screen {
    private final Game game;
    private final Stage stage;
    private final MenuManager menuManager;
    private final SpriteBatch batch;
    private final ProfileController profileController;
    private final LoginMenuController loginController;
    private TextField usernameField;
    private TextField nicknameField;
    private TextField emailField;
    private TextField newPasswordField;
    private TextField oldPasswordField;
    private Label statusLabel;

    public ProfileView(Game game, LoginMenuController loginController) {
        this.game = game;
        this.loginController = loginController;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.batch = new SpriteBatch();
        this.profileController = new ProfileController(loginController.getLoggedInUser());

        createUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(30);

        // Parse user info once to avoid multiple calls
        String[] userInfo = profileController.showUserInfo().getMessage().split("\n");

        // Title
        Label titleLabel = new Label("Profile Settings", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        // Status Label for showing results
        statusLabel = new Label("", menuManager.getPixthulhuSkin());
        statusLabel.setColor(0.2f, 0.8f, 0.2f, 1f);

        // Create fields with current user data
        usernameField = new TextField(userInfo[0].substring(10), menuManager.getPixthulhuSkin());
        nicknameField = new TextField(userInfo[1].substring(10), menuManager.getPixthulhuSkin());
        emailField = new TextField(userInfo[2].substring(7), menuManager.getPixthulhuSkin());

        // Gender display (non-editable)
        Label genderLabel = new Label("Gender:", menuManager.getPixthulhuSkin());
        Label genderValue = new Label(userInfo[3].substring(8), menuManager.getPixthulhuSkin());
        genderValue.setColor(0.95f, 0.92f, 0.82f, 1f);

        // Password fields
        newPasswordField = new TextField("", menuManager.getPixthulhuSkin());
        oldPasswordField = new TextField("", menuManager.getPixthulhuSkin());
        newPasswordField.setPasswordCharacter('*');
        newPasswordField.setPasswordMode(true);
        oldPasswordField.setPasswordCharacter('*');
        oldPasswordField.setPasswordMode(true);

        // Field Labels
        Label usernameLabel = new Label("Username:", menuManager.getPixthulhuSkin());
        Label nicknameLabel = new Label("Nickname:", menuManager.getPixthulhuSkin());
        Label emailLabel = new Label("Email:", menuManager.getPixthulhuSkin());
        Label newPasswordLabel = new Label("New Password:", menuManager.getPixthulhuSkin());
        Label oldPasswordLabel = new Label("Current Password:", menuManager.getPixthulhuSkin());

        // Buttons with consistent styling
        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

        TextButton saveUsername = new TextButton("Save", menuManager.getPixthulhuSkin());
        TextButton saveNickname = new TextButton("Save", menuManager.getPixthulhuSkin());
        TextButton saveEmail = new TextButton("Save", menuManager.getPixthulhuSkin());
        TextButton savePassword = new TextButton("Save", menuManager.getPixthulhuSkin());
        TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());

        // Apply button styling
        for (TextButton button : new TextButton[]{saveUsername, saveNickname, saveEmail, savePassword, backButton}) {
            button.setColor(buttonColor);
            button.getLabel().setColor(textColor);
        }

        // Layout
        mainTable.add(titleLabel).colspan(3).padBottom(20).row();
        mainTable.add(statusLabel).colspan(3).padBottom(20).row();

        // Username row
        mainTable.add(usernameLabel).padRight(10);
        mainTable.add(usernameField).width(300).padRight(10);
        mainTable.add(saveUsername).width(150).row();
        mainTable.row().padTop(10);

        // Nickname row
        mainTable.add(nicknameLabel).padRight(10);
        mainTable.add(nicknameField).width(300).padRight(10);
        mainTable.add(saveNickname).width(150).row();
        mainTable.row().padTop(10);

        // Email row
        mainTable.add(emailLabel).padRight(10);
        mainTable.add(emailField).width(300).padRight(10);
        mainTable.add(saveEmail).width(150).row();
        mainTable.row().padTop(10);

        // Gender row (display only)
        mainTable.add(genderLabel).padRight(10);
        mainTable.add(genderValue).width(300).padRight(10);
        mainTable.add().width(150).row();
        mainTable.row().padTop(10);

        // Password rows
        mainTable.add(newPasswordLabel).padRight(10);
        mainTable.add(newPasswordField).width(300).padRight(10);
        mainTable.add().width(150).row();
        mainTable.add(oldPasswordLabel).padRight(10);
        mainTable.add(oldPasswordField).width(300).padRight(10);
        mainTable.add(savePassword).width(150).row();
        mainTable.row().padTop(20);

        // Back button
        mainTable.add(backButton).colspan(3).width(150).height(100).padTop(30);

        // Button Listeners
        saveUsername.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Result result = profileController.changeUserName(usernameField.getText());
                statusLabel.setText(result.getMessage());
                statusLabel.setColor(result.isSuccess() ? Color.GREEN : Color.RED);
            }
        });

        saveNickname.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Result result = profileController.changeNickname(nicknameField.getText());
                statusLabel.setText(result.getMessage());
                statusLabel.setColor(result.isSuccess() ? Color.GREEN : Color.RED);
            }
        });

        saveEmail.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Result result = profileController.changeEmail(emailField.getText());
                statusLabel.setText(result.getMessage());
                statusLabel.setColor(result.isSuccess() ? Color.GREEN : Color.RED);
            }
        });

        savePassword.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Result result = profileController.changePassword(newPasswordField.getText(), oldPasswordField.getText());
                statusLabel.setText(result.getMessage());
                statusLabel.setColor(result.isSuccess() ? Color.GREEN : Color.RED);
                if (result.isSuccess()) {
                    newPasswordField.setText("");
                    oldPasswordField.setText("");
                }
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new MainView(game, loginController));
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
