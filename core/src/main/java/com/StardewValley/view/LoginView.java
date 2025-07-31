package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LoginMenuController;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class LoginView implements Screen {
    private final Game game;
    private Stage stage;
    private SpriteBatch batch;
    private MenuManager menuManager;
    private LoginMenuController loginMenuController;
    private TextButton getQuestionButton;
    private TextButton checkAnswerButton;
    // Login form fields
    private TextField usernameField, passwordField;
    private CheckBox stayLoggedInCheck;
    private Label errorLabel;
    private Table loginTable;

    // Forgot password fields
    private Table forgotPasswordTable;
    private TextField forgotUsernameField, answerField;
    private TextField newPasswordField;
    private Label forgotErrorLabel;
    private Label questionLabel;
    private TextButton generatePasswordButton;
    private TextButton setNewPasswordButton;

    private String clientId;

    public LoginView(Game game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.menuManager = MenuManager.getInstance();
        this.loginMenuController = new LoginMenuController(null); // Scanner not needed for UI
        try {
            this.clientId = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            this.clientId = UUID.randomUUID().toString();
        }
        createLoginForm();
        createForgotPasswordForm();
    }

    private void createLoginForm() {
        loginTable = new Table();
        loginTable.setFillParent(true);
        loginTable.top().padTop(50);

        Label titleLabel = new Label("Login", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        usernameField = new TextField("", menuManager.getPixthulhuSkin());
        usernameField.setMessageText("Username");

        passwordField = new TextField("", menuManager.getPixthulhuSkin());
        passwordField.setMessageText("Password");
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);

        stayLoggedInCheck = new CheckBox(" Stay Logged In", menuManager.getPixthulhuSkin());
        stayLoggedInCheck.getLabel().setColor(0.95f, 0.92f, 0.82f, 1f);

        errorLabel = new Label("", menuManager.getPixthulhuSkin());
        errorLabel.setColor(1, 0, 0, 1);
        errorLabel.setAlignment(Align.center);
        errorLabel.setWrap(true);

        TextButton loginButton = new TextButton("Login", menuManager.getPixthulhuSkin());
        TextButton forgotPasswordButton = new TextButton("Forgot Password", menuManager.getPixthulhuSkin());
        TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());

        // Button colors
        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

        loginButton.setColor(buttonColor);
        forgotPasswordButton.setColor(buttonColor);
        backButton.setColor(buttonColor);

        loginButton.getLabel().setColor(textColor);
        forgotPasswordButton.getLabel().setColor(textColor);
        backButton.getLabel().setColor(textColor);

        // Dimensions
        float fieldWidth = 300;
        float buttonWidth = 300;
        float buttonHeight = 90;
        float verticalSpacing = 15;

        loginTable.add(titleLabel).padBottom(30).row();
        loginTable.add(new Label("Username:", menuManager.getPixthulhuSkin())).align(Align.left).padBottom(10).row();
        loginTable.add(usernameField).width(fieldWidth).row();
        loginTable.add().height(verticalSpacing).row();

        loginTable.add(new Label("Password:", menuManager.getPixthulhuSkin())).align(Align.left).padBottom(10).row();
        loginTable.add(passwordField).width(fieldWidth).row();
        loginTable.add().height(verticalSpacing).row();

        loginTable.add(stayLoggedInCheck).row();
        loginTable.add().height(verticalSpacing).row();

        loginTable.add(errorLabel).width(400).padBottom(20).row();

        // Stack buttons vertically
        loginTable.add(loginButton).width(buttonWidth).height(buttonHeight).padBottom(20).row();
        loginTable.add(forgotPasswordButton).width(450).height(buttonHeight).padBottom(20).row();
        loginTable.add(backButton).width(buttonWidth).height(buttonHeight).row();

        // Button listeners remain the same
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleLogin();
            }
        });

        forgotPasswordButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loginTable.setVisible(false);
                forgotPasswordTable.setVisible(true);
                forgotErrorLabel.setText("");
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new PreMenuView(game));
            }
        });

        stage.addActor(loginTable);
    }

    private void createForgotPasswordForm() {
        forgotPasswordTable = new Table();
        forgotPasswordTable.setFillParent(true);
        forgotPasswordTable.top().padTop(50);

        // Title
        Label titleLabel = new Label("Forgot Password", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

        // Components
        forgotUsernameField = new TextField("", menuManager.getPixthulhuSkin());
        forgotUsernameField.setMessageText("Username");

        getQuestionButton = new TextButton("Get Security Question", menuManager.getPixthulhuSkin());
        questionLabel = new Label("", menuManager.getPixthulhuSkin());
        questionLabel.setWrap(true);
        questionLabel.setAlignment(Align.center);

        answerField = new TextField("", menuManager.getPixthulhuSkin());
        answerField.setMessageText("Answer");

        checkAnswerButton = new TextButton("Submit Answer", menuManager.getPixthulhuSkin());
        TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());

        forgotErrorLabel = new Label("", menuManager.getPixthulhuSkin());
        forgotErrorLabel.setWrap(true);
        forgotErrorLabel.setAlignment(Align.center);

        // Initially hide components
        answerField.setVisible(false);
        checkAnswerButton.setVisible(false);
        questionLabel.setVisible(false);

        // Button styling
        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

        getQuestionButton.setColor(buttonColor);
        checkAnswerButton.setColor(buttonColor);
        backButton.setColor(buttonColor);

        getQuestionButton.getLabel().setColor(textColor);
        checkAnswerButton.getLabel().setColor(textColor);
        backButton.getLabel().setColor(textColor);

        // Layout with proper spacing
        float fieldWidth = 400;
        float buttonWidth = 300;
        float buttonHeight = 100;

        forgotPasswordTable.add(titleLabel).padBottom(50).row();
        forgotPasswordTable.add(forgotUsernameField).width(fieldWidth).padBottom(20).row();
        forgotPasswordTable.add(getQuestionButton).width(600).height(buttonHeight).padBottom(20).row();
        forgotPasswordTable.add(questionLabel).width(fieldWidth).padBottom(20).row();
        forgotPasswordTable.add(answerField).width(fieldWidth).padBottom(20).row();
        forgotPasswordTable.add(checkAnswerButton).width(400).height(buttonHeight).padBottom(20).row();
        forgotPasswordTable.add(forgotErrorLabel).width(fieldWidth).padBottom(20).row();
        forgotPasswordTable.add(backButton).width(buttonWidth).height(buttonHeight).row();

        // Get Question Button Listener
        getQuestionButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String username = forgotUsernameField.getText();
                Result result = loginMenuController.forgetPassword(username);

                if (result.isSuccess()) {
                    questionLabel.setText(result.getMessage());
                    questionLabel.setVisible(true);
                    answerField.setVisible(true);
                    checkAnswerButton.setVisible(true);
                    forgotErrorLabel.setText("");
                } else {
                    forgotErrorLabel.setText(result.getMessage());
                    forgotErrorLabel.setColor(1, 0, 0, 1);
                }
            }
        });

        // Submit Answer Button Listener
        checkAnswerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String username = forgotUsernameField.getText();
                String answer = answerField.getText();
                Result result = loginMenuController.checkSecurityAnswer(username, answer);

                if (result.isSuccess()) {
                    // Clear current form
                    forgotPasswordTable.clearChildren();
                    forgotPasswordTable.top().padTop(50);

                    // Create new password components
                    Label titleLabel = new Label("Reset Password", menuManager.getPixthulhuSkin(), "title");
                    titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);

                    newPasswordField = new TextField("", menuManager.getPixthulhuSkin());
                    newPasswordField.setMessageText("New Password");
                    newPasswordField.setPasswordMode(true);

                    generatePasswordButton = new TextButton("Generate Random Password", menuManager.getPixthulhuSkin());
                    setNewPasswordButton = new TextButton("Set New Password", menuManager.getPixthulhuSkin());
                    TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());

                    // Add error label for password reset section
                    Label resetErrorLabel = new Label("", menuManager.getPixthulhuSkin());
                    resetErrorLabel.setWrap(true);
                    resetErrorLabel.setAlignment(Align.center);

                    // Styling
                    Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
                    Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);

                    generatePasswordButton.setColor(buttonColor);
                    setNewPasswordButton.setColor(buttonColor);
                    backButton.setColor(buttonColor);

                    generatePasswordButton.getLabel().setColor(textColor);
                    setNewPasswordButton.getLabel().setColor(textColor);
                    backButton.getLabel().setColor(textColor);

                    // Layout
                    forgotPasswordTable.add(titleLabel).padBottom(30).row();
                    forgotPasswordTable.add(newPasswordField).width(400).padBottom(20).row();
                    forgotPasswordTable.add(generatePasswordButton).width(800).height(100).padBottom(20).row();
                    forgotPasswordTable.add(setNewPasswordButton).width(550).height(100).padBottom(20).row();
                    forgotPasswordTable.add(resetErrorLabel).width(400).padBottom(20).row();
                    forgotPasswordTable.add(backButton).width(300).height(100).row();

                    // Listeners
                    generatePasswordButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            String randomPass = loginMenuController.generateRandomPassword();
                            newPasswordField.setText(randomPass);
                            resetErrorLabel.setText("Random password generated!");
                            resetErrorLabel.setColor(0, 1, 0, 1);
                        }
                    });

                    setNewPasswordButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            String newPassword = newPasswordField.getText();
                            if (newPassword.isEmpty()) {
                                resetErrorLabel.setText("Password cannot be empty!");
                                resetErrorLabel.setColor(1, 0, 0, 1);
                                return;
                            }

                            Result passwordResult = loginMenuController.setNewPassword(username, newPassword);
                            if (passwordResult.isSuccess()) {
                                resetErrorLabel.setText("Password successfully changed!");
                                resetErrorLabel.setColor(0, 1, 0, 1);
                                Timer.schedule(new Timer.Task() {
                                    @Override
                                    public void run() {
                                        Gdx.app.postRunnable(() -> {
                                            forgotPasswordTable.setVisible(false);
                                            loginTable.setVisible(true);
                                        });
                                    }
                                }, 1);
                            } else {
                                resetErrorLabel.setText(passwordResult.getMessage());
                                resetErrorLabel.setColor(1, 0, 0, 1);
                            }
                        }
                    });

                    backButton.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            forgotPasswordTable.setVisible(false);
                            loginTable.setVisible(true);
                        }
                    });
                } else {
                    forgotErrorLabel.setText(result.getMessage());
                    forgotErrorLabel.setColor(1, 0, 0, 1);
                }
            }
        });

        // Back Button Listener
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                forgotPasswordTable.setVisible(false);
                loginTable.setVisible(true);
                // Reset fields
                forgotUsernameField.setText("");
                answerField.setText("");
                questionLabel.setText("");
                forgotErrorLabel.setText("");
            }
        });

        forgotPasswordTable.setVisible(false);
        stage.addActor(forgotPasswordTable);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean stayLoggedIn = stayLoggedInCheck.isChecked();

        Result result = loginMenuController.Login(username, password, stayLoggedIn, clientId);
        if (result.isSuccess()) {
            dispose();
            game.setScreen(new MainView(game, loginMenuController));
        } else {
            errorLabel.setText(result.getMessage());
            errorLabel.setColor(1, 0, 0, 1);
        }
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
    public void show() {
        Gdx.input.setInputProcessor(stage);
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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
