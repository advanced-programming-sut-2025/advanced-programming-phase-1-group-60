package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.controller.LobbyController;
import com.StardewValley.models.Lobby;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LobbyChoiceView implements Screen {
    private final Game game;
    private final LobbyController lobbyController;
    private final Stage stage;
    private final MenuManager menuManager;
    private final SpriteBatch batch;

    public LobbyChoiceView(Game game, LobbyController lobbyController) {
        this.game = game;
        this.lobbyController = lobbyController;
        this.stage = new Stage(new ScreenViewport());
        this.menuManager = MenuManager.getInstance();
        this.batch = new SpriteBatch();

        createUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void createUI() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Label titleLabel = new Label("Lobby", menuManager.getPixthulhuSkin(), "title");

        TextButton createLobbyBtn = new TextButton("Create Lobby", menuManager.getPixthulhuSkin());
        TextButton joinLobbyBtn = new TextButton("Join Lobby", menuManager.getPixthulhuSkin());
        TextButton backBtn = new TextButton("Back", menuManager.getPixthulhuSkin());

        createLobbyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showCreateLobbyDialog();
            }
        });

        joinLobbyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LobbyListView(game, lobbyController));
            }
        });

        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                lobbyController.onBack();
            }
        });

        root.add(titleLabel).padBottom(40).row();
        root.add(createLobbyBtn).width(350).height(90).padBottom(20).row();
        root.add(joinLobbyBtn).width(350).height(90).padBottom(20).row();
        root.add(backBtn).width(350).height(90).row();

        stage.addActor(root);
    }

    private void showCreateLobbyDialog() {
        Dialog dialog = new Dialog("Create Lobby", menuManager.getPixthulhuSkin());

        TextField nameField = new TextField("", menuManager.getPixthulhuSkin());
        nameField.setMessageText("Lobby Name");

        // REMOVED: Capacity field is removed

        CheckBox publicBox = new CheckBox(" Public", menuManager.getPixthulhuSkin());
        publicBox.setChecked(true);

        TextField passwordField = new TextField("", menuManager.getPixthulhuSkin());
        passwordField.setMessageText("Password (if private)");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setDisabled(true);

        CheckBox visibleBox = new CheckBox(" Visible in List", menuManager.getPixthulhuSkin());
        visibleBox.setChecked(true);

        publicBox.addListener(event -> {
            passwordField.setDisabled(publicBox.isChecked());
            return false;
        });

        dialog.getContentTable().add(nameField).width(300).pad(5).row();
        // REMOVED: capacityField row
        dialog.getContentTable().add(publicBox).pad(5).row();
        dialog.getContentTable().add(passwordField).width(300).pad(5).row();
        dialog.getContentTable().add(visibleBox).pad(5).row();

        TextButton createBtn = new TextButton("Create", menuManager.getPixthulhuSkin());
        dialog.button(createBtn);
        dialog.button("Cancel");

        createBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String name = nameField.getText();
                if (name.trim().isEmpty()) {
                    // You can add a label to the dialog to show this error
                    return;
                }

                // FIX: Capacity is now hardcoded to 4
                int capacity = 4;
                boolean isPublic = publicBox.isChecked();
                String password = isPublic ? null : passwordField.getText();
                boolean isVisible = visibleBox.isChecked();

                // فقط درخواست ساخت لابی را به سرور ارسال می کنیم
                // سرور پس از ساخت، ما را به صفحه بعد هدایت خواهد کرد
                lobbyController.createLobby(name, isPublic, password, isVisible, capacity);
                dialog.hide();

                // این بخش حذف می شود چون دیگر کلاینت مسئول تغییر صفحه نیست
                // Lobby createdLobby = lobbyController.getLobbyForUser(lobbyController.getCurrentUsername());
                // if (createdLobby != null) {
                //     game.setScreen(new InLobbyView(game, lobbyController, createdLobby));
                // }
            }
        });

        dialog.show(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // FIX: Add background rendering
        menuManager.updateBackgroundAnimation(delta);
        float[] positions = menuManager.getBackgroundPositions();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        batch.begin();
        for (float position : positions) {
            batch.draw(menuManager.getBackgroundLayer(), position, 0, width, height);
        }
        batch.draw(menuManager.getMiddlegroundLayer(), 0, 0, width, height);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() { Gdx.input.setInputProcessor(stage); }
    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }
}
