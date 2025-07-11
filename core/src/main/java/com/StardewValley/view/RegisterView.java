package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.RegisterController;
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

public class RegisterView implements Screen {
    private final Game game;
    private Stage stage;
    private SpriteBatch batch;
    private MenuManager menuManager;
    private RegisterController registerController;

    // Registration form fields
    private TextField usernameField, passwordField, confirmPasswordField,
        nicknameField, emailField;
    private SelectBox<String> genderSelect;
    private Label errorLabel;
    private Label securityErrorLabel;
    // Security form fields
    private Table securityTable;
    private SelectBox<String> securityQuestionSelect;
    private TextField securityAnswerField;
    private TextField confirmAnswerField;

    public RegisterView(Game game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.menuManager = MenuManager.getInstance();
        this.registerController = new RegisterController();
        createRegistrationForm();
        createSecurityQuestionForm();
    }

    private void createRegistrationForm() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(50);

        // Title
        Label titleLabel = new Label("Register New Account", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);
        mainTable.add(titleLabel).colspan(2).padBottom(30).row();
        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);
        // Initialize form fields
        usernameField = new TextField("", menuManager.getPixthulhuSkin());
        usernameField.setMessageText("Username");

        passwordField = new TextField("", menuManager.getPixthulhuSkin());
        passwordField.setMessageText("Password");
        passwordField.setPasswordCharacter('*');
        passwordField.setPasswordMode(true);

        confirmPasswordField = new TextField("", menuManager.getPixthulhuSkin());
        confirmPasswordField.setMessageText("Confirm Password");
        confirmPasswordField.setPasswordCharacter('*');
        confirmPasswordField.setPasswordMode(true);

        nicknameField = new TextField("", menuManager.getPixthulhuSkin());
        nicknameField.setMessageText("Nickname");

        emailField = new TextField("", menuManager.getPixthulhuSkin());
        emailField.setMessageText("Email");

        genderSelect = new SelectBox<>(menuManager.getPixthulhuSkin());
        genderSelect.setItems("Male", "Female");

        // Buttons
        TextButton registerButton = new TextButton("Next", menuManager.getPixthulhuSkin());
        TextButton generatePasswordButton = new TextButton("Generate Password", menuManager.getPixthulhuSkin());
        TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());
        registerButton.setColor(buttonColor);
        generatePasswordButton.setColor(buttonColor);
        backButton.setColor(buttonColor);
        registerButton.getLabel().setColor(textColor);
        generatePasswordButton.getLabel().setColor(textColor);
        backButton.getLabel().setColor(textColor);
        errorLabel = new Label("", menuManager.getPixthulhuSkin());
        errorLabel.setColor(1, 0, 0, 1);
        errorLabel.setAlignment(Align.center);
        errorLabel.setWrap(true);

        // Layout dimensions
        float fieldWidth = 300;
        float labelWidth = 200;
        float verticalSpacing = 15;

        // Add form fields with spacing
        mainTable.add(new Label("Username:", menuManager.getPixthulhuSkin())).width(labelWidth).right().padRight(30);
        mainTable.add(usernameField).width(fieldWidth).left().row();
        mainTable.add().height(verticalSpacing).row();

        mainTable.add(new Label("Password:", menuManager.getPixthulhuSkin())).width(labelWidth).right().padRight(30);
        mainTable.add(passwordField).width(fieldWidth).left().row();
        mainTable.add().height(verticalSpacing).row();

        mainTable.add(new Label("Confirm Pass:", menuManager.getPixthulhuSkin())).width(labelWidth).right().padRight(30);
        mainTable.add(confirmPasswordField).width(fieldWidth).left().row();
        mainTable.add().height(verticalSpacing).row();

        mainTable.add(new Label("Nickname:", menuManager.getPixthulhuSkin())).width(labelWidth).right().padRight(30);
        mainTable.add(nicknameField).width(fieldWidth).left().row();
        mainTable.add().height(verticalSpacing).row();

        mainTable.add(new Label("Email:", menuManager.getPixthulhuSkin())).width(labelWidth).right().padRight(30);
        mainTable.add(emailField).width(fieldWidth).left().row();
        mainTable.add().height(verticalSpacing).row();

        mainTable.add(new Label("Gender:", menuManager.getPixthulhuSkin())).width(labelWidth).right().padRight(30);
        mainTable.add(genderSelect).width(fieldWidth).left().row();
        mainTable.add().height(verticalSpacing).row();

        mainTable.add(generatePasswordButton).colspan(2).padTop(15).width(500).height(90).row();
        mainTable.add().height(verticalSpacing).row();

        mainTable.add(errorLabel).colspan(2).padTop(5).height(60).width(500).row();
        mainTable.add().height(verticalSpacing).row();

        Table buttonTable = new Table();
        buttonTable.add(registerButton).width(150).height(90).padRight(40);
        buttonTable.add(backButton).width(150).height(90);
        mainTable.add(buttonTable).colspan(2).padTop(5);
        // Button listeners
        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleRegistration();
            }
        });

        generatePasswordButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String generatedPassword = registerController.generateRandomPassword();
                passwordField.setText(generatedPassword);
                confirmPasswordField.setText(generatedPassword);
                errorLabel.setText("Generated password: " + generatedPassword);
                errorLabel.setColor(0, 1, 0, 1);
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new PreMenuView(game));
            }
        });

        stage.addActor(mainTable);
    }

    private void createSecurityQuestionForm() {
        securityTable = new Table();
        securityTable.setFillParent(true);
        securityTable.setVisible(false);

        Label titleLabel = new Label("Security Question", menuManager.getPixthulhuSkin(), "title");
        titleLabel.setColor(0.9f, 0.95f, 0.7f, 1f);
        Color buttonColor = new Color(0.38f, 0.55f, 0.27f, 1f);
        Color textColor = new Color(0.95f, 0.92f, 0.82f, 1f);
        securityQuestionSelect = new SelectBox<>(menuManager.getPixthulhuSkin());
        securityQuestionSelect.setItems(registerController.getSecurityQuestions().values().toArray(new String[0]));

        securityAnswerField = new TextField("", menuManager.getPixthulhuSkin());
        securityAnswerField.setMessageText("Answer");

        confirmAnswerField = new TextField("", menuManager.getPixthulhuSkin());
        confirmAnswerField.setMessageText("Confirm Answer");

        TextButton completeButton = new TextButton("Complete Registration", menuManager.getPixthulhuSkin());
        TextButton backButton = new TextButton("Back", menuManager.getPixthulhuSkin());
        completeButton.setColor(buttonColor);
        backButton.setColor(buttonColor);

        completeButton.getLabel().setColor(textColor);
        backButton.getLabel().setColor(textColor);
        // Add error label
        securityErrorLabel = new Label("", menuManager.getPixthulhuSkin());
        securityErrorLabel.setColor(1, 0, 0, 1);
        securityErrorLabel.setAlignment(Align.center);
        securityErrorLabel.setWrap(true);
        // Updated dimensions
        float fieldWidth = 500;  // Wider for questions
        float buttonHeight = 100;
        float verticalSpacing = 15;

        securityTable.top().pad(50);
        securityTable.add(titleLabel).padBottom(30).row();

        // Question select with full width
        securityTable.add(new Label("Select Question:", menuManager.getPixthulhuSkin())).padBottom(10).row();
        securityTable.add(securityQuestionSelect).width(800).padBottom(verticalSpacing).row();

        // Answer field
        securityTable.add(new Label("Answer:", menuManager.getPixthulhuSkin())).padBottom(10).row();
        securityTable.add(securityAnswerField).width(fieldWidth).padBottom(verticalSpacing).row();

        // Confirm answer field
        securityTable.add(new Label("Confirm Answer:", menuManager.getPixthulhuSkin())).padBottom(10).row();
        securityTable.add(confirmAnswerField).width(fieldWidth).padBottom(verticalSpacing).row();

        // Buttons in separate rows
        securityTable.add(securityErrorLabel).width(500).padBottom(verticalSpacing).row();
        securityTable.add(completeButton).width(600).height(buttonHeight).padBottom(verticalSpacing).row();


        completeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleSecurityQuestion();
            }
        });

        stage.addActor(securityTable);
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPass = confirmPasswordField.getText();
        String nickname = nicknameField.getText();
        String email = emailField.getText();
        String gender = genderSelect.getSelected();

        Result result = registerController.register(username, password, confirmPass, nickname, email, gender);

        if (result.isSuccess()) {
            stage.getActors().first().setVisible(false);
            securityTable.setVisible(true);
            errorLabel.setText("");
        } else {
            errorLabel.setText(result.getMessage());
            errorLabel.setColor(1, 0, 0, 1);
        }
    }

    private void handleSecurityQuestion() {
        int questionIndex = securityQuestionSelect.getSelectedIndex() + 1;
        String answer = securityAnswerField.getText();
        String confirmAnswer = confirmAnswerField.getText();

        Result result = registerController.pickQuestion(questionIndex, answer, confirmAnswer);

        if (result.isSuccess()) {
            securityErrorLabel.setColor(0, 1, 0, 1);
            securityErrorLabel.setText("Account Created Successfully: " + usernameField.getText() + "!");

            // Create a timer to delay the transition
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    Gdx.app.postRunnable(() -> {
                        dispose();
                        game.setScreen(new PreMenuView(game));
                    });
                }
            }, 2); // 2 second delay
        } else {
            securityErrorLabel.setText(result.getMessage());
            securityErrorLabel.setColor(1, 0, 0, 1);
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
