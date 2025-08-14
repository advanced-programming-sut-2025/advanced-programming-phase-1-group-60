package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LoginMenuController;
import com.StardewValley.controller.ProfileController;
import com.StardewValley.models.Result;
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

    // New stats labels
    private Label gamesPlayedLabel;
    private Label goldLabel;

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

    private void refreshStats() {
        int gamesPlayed = profileController.getUser().getGames() != null
            ? profileController.getUser().getGames().size() : 0;
        int gold = profileController.getUser().getMoney();
        gamesPlayedLabel.setText("Games Played: " + gamesPlayed);
        goldLabel.setText("Gold: " + gold);
    }

    private void createUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().left().padTop(30).padLeft(30);

        String[] userInfo = profileController.showUserInfo().getMessage().split("\n");

        Label titleLabel = new Label("Profile Settings", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        statusLabel = new Label("", menuManager.getPixthulhuSkin());
        statusLabel.setColor(0.2f, 0.8f, 0.2f, 1f);

        usernameField = new TextField(userInfo[0].substring(10), menuManager.getPixthulhuSkin());
        nicknameField = new TextField(userInfo[1].substring(10), menuManager.getPixthulhuSkin());
        emailField = new TextField(userInfo[2].substring(7), menuManager.getPixthulhuSkin());

        Label genderLabel = new Label("Gender:", menuManager.getPixthulhuSkin());
        Label genderValue = new Label(userInfo[3].substring(8), menuManager.getPixthulhuSkin());
        genderValue.setColor(0.95f, 0.92f, 0.82f, 1f);

        newPasswordField = new TextField("", menuManager.getPixthulhuSkin());
        oldPasswordField = new TextField("", menuManager.getPixthulhuSkin());
        newPasswordField.setPasswordCharacter('*');
        newPasswordField.setPasswordMode(true);
        oldPasswordField.setPasswordCharacter('*');
        oldPasswordField.setPasswordMode(true);

        Label usernameLabel = new Label("Username:", menuManager.getPixthulhuSkin());
        Label nicknameLabel = new Label("Nickname:", menuManager.getPixthulhuSkin());
        Label emailLabel = new Label("Email:", menuManager.getPixthulhuSkin());
        Label newPasswordLabel = new Label("New Password:", menuManager.getPixthulhuSkin());
        Label oldPasswordLabel = new Label("Current Password:", menuManager.getPixthulhuSkin());

        // Stats labels (new)
        gamesPlayedLabel = new Label("", menuManager.getPixthulhuSkin());
        goldLabel = new Label("", menuManager.getPixthulhuSkin());
        gamesPlayedLabel.setColor(0.8f, 0.85f, 1f, 1f);
        goldLabel.setColor(0.9f, 0.85f, 0.4f, 1f);
        refreshStats();

        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

        TextButton saveUsername = new TextButton("Save", menuManager.getPixthulhuSkin());
        TextButton saveNickname = new TextButton("Save", menuManager.getPixthulhuSkin());
        TextButton saveEmail = new TextButton("Save", menuManager.getPixthulhuSkin());
        TextButton savePassword = new TextButton("Save", menuManager.getPixthulhuSkin());
        TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());
        TextButton randomPasswordButton = new TextButton("Random", menuManager.getPixthulhuSkin()); // NEW

        for (TextButton b : new TextButton[]{saveUsername, saveNickname, saveEmail, savePassword, backButton, randomPasswordButton}) {
            b.setColor(buttonColor);
            b.getLabel().setColor(textColor);
        }

        // Layout
        mainTable.add(titleLabel).colspan(4).padBottom(15).left().row();
        mainTable.add(gamesPlayedLabel).left().padBottom(5).colspan(2);
        mainTable.row();
        mainTable.add(goldLabel).left().padBottom(20).colspan(2);
        mainTable.row();
        mainTable.add(statusLabel).colspan(4).padBottom(15).left().row();

        // Username row
        mainTable.add(usernameLabel).padRight(10).left();
        mainTable.add(usernameField).width(300).padRight(10).left();
        mainTable.add(saveUsername).width(120).left();
        mainTable.add().expandX().row();

        // Nickname row
        mainTable.add(nicknameLabel).padRight(10).left();
        mainTable.add(nicknameField).width(300).padRight(10).left();
        mainTable.add(saveNickname).width(120).left();
        mainTable.add().row();

        // Email row
        mainTable.add(emailLabel).padRight(10).left();
        mainTable.add(emailField).width(300).padRight(10).left();
        mainTable.add(saveEmail).width(120).left();
        mainTable.add().row();

        // Gender display row
        mainTable.add(genderLabel).padRight(10).left();
        mainTable.add(genderValue).width(300).padRight(10).left();
        mainTable.add().width(120);
        mainTable.add().row();

        // New password row (with Random button)
        mainTable.add(newPasswordLabel).padRight(10).left();
        mainTable.add(newPasswordField).width(300).padRight(10).left();
        mainTable.add(randomPasswordButton).width(120).left();
        mainTable.add().row();

        // Old password + Save row
        mainTable.add(oldPasswordLabel).padRight(10).left();
        mainTable.add(oldPasswordField).width(300).padRight(10).left();
        mainTable.add(savePassword).width(120).left();
        mainTable.add().row();

        // Back button row
        mainTable.add(backButton).colspan(3).width(160).height(70).padTop(30).left();

        // Listeners
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

        randomPasswordButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String pwd = profileController.generateRandomPassword();
                newPasswordField.setPasswordMode(false); // show generated password
                newPasswordField.setText(pwd);
                statusLabel.setText("Random password generated. Remember to SAVE it.");
                statusLabel.setColor(Color.YELLOW);
            }
        });

        savePassword.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Result result = profileController.changePassword(newPasswordField.getText(), oldPasswordField.getText());
                statusLabel.setText(result.getMessage());
                statusLabel.setColor(result.isSuccess() ? Color.GREEN : Color.RED);
                if (result.isSuccess()) {
                    // Re-mask and clear for security
                    newPasswordField.setPasswordMode(true);
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
            batch.draw(menuManager.getBackgroundLayer(), position, 0, width, height);
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
        refreshStats(); // refresh when shown
    }

    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }
}
