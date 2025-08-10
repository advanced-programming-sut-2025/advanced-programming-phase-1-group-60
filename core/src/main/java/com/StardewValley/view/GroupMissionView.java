package com.StardewValley.view;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.models.GroupMission;
import com.StardewValley.models.User;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.StardewValley.Network.Client.ClientMain;
import com.StardewValley.Network.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupMissionView implements Screen {
    private Game game;
    private GameView gameView;
    private User player;
    private Stage stage;
    private Skin skin;

    // جدا کردن ScrollPaneها برای دو لیست
    private ScrollPane availableMissionsScrollPane;
    private ScrollPane activeMissionsScrollPane;
    private Table availableMissionsTable;
    private Table activeMissionsTable;

    private List<GroupMission> availableMissions;
    private List<GroupMission> activeMissions;

    public GroupMissionView(Game game, GameView gameView, User player, List<GroupMission> availableMissions, List<GroupMission> activeMissions) {
        this.game = game;
        this.gameView = gameView;
        this.player = player;
        this.availableMissions = availableMissions;
        this.activeMissions = activeMissions;
        this.stage = new Stage(new ScreenViewport());
        this.skin = MenuManager.getInstance().getPixthulhuSkin();
        Gdx.input.setInputProcessor(stage);
        createUI();
    }

    public void updateMissions(List<GroupMission> available, List<GroupMission> active) {
        this.availableMissions = available;
        this.activeMissions = active;
        populateAvailableMissions();
        populateActiveMissions();
    }

    private void createUI() {
        Table mainContainer = new Table();
        mainContainer.setFillParent(true);
        mainContainer.pad(20);
        stage.addActor(mainContainer);

        // Background Image
        Image background = new Image(skin.getDrawable("window"));
        mainContainer.setBackground(background.getDrawable());

        // Header with Title and Back Button
        Table headerTable = new Table();
        Label titleLabel = new Label("Group Missions", skin, "title");
        TextButton backButton = new TextButton("Back to Game", skin);

        headerTable.add(titleLabel).expandX().center();
        headerTable.add(backButton).pad(10);
        mainContainer.add(headerTable).fillX().top().row();

        // Toggle Buttons for Available/Active Missions
        TextButton showAvailableButton = new TextButton("Available Missions", skin, "toggle");
        TextButton showActiveButton = new TextButton("Active Missions", skin, "toggle");
        ButtonGroup<TextButton> buttonGroup = new ButtonGroup<>(showAvailableButton, showActiveButton);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        showAvailableButton.setChecked(true);

        Table switchButtons = new Table();
        switchButtons.add(showAvailableButton).pad(5);
        switchButtons.add(showActiveButton).pad(5);
        mainContainer.add(switchButtons).left().padTop(15).row();

        // Tables and ScrollPanes for missions
        availableMissionsTable = new Table(skin);
        availableMissionsTable.top().left();
        availableMissionsScrollPane = new ScrollPane(availableMissionsTable, skin);
        availableMissionsScrollPane.setFadeScrollBars(false);

        activeMissionsTable = new Table(skin);
        activeMissionsTable.top().left();
        activeMissionsScrollPane = new ScrollPane(activeMissionsTable, skin);
        activeMissionsScrollPane.setFadeScrollBars(false);

        // Stack to overlay the scroll panes
        Stack missionStack = new Stack(availableMissionsScrollPane, activeMissionsScrollPane);
        mainContainer.add(missionStack).expand().fill().pad(10).row();

        // Set initial visibility
        activeMissionsScrollPane.setVisible(false);

        // Listeners for toggle buttons
        showAvailableButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (showAvailableButton.isChecked()) {
                    availableMissionsScrollPane.setVisible(true);
                    activeMissionsScrollPane.setVisible(false);
                }
            }
        });

        showActiveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (showActiveButton.isChecked()) {
                    availableMissionsScrollPane.setVisible(false);
                    activeMissionsScrollPane.setVisible(true);
                }
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameView.showMapView();
            }
        });

        // Populate the lists with initial data
        populateAvailableMissions();
        populateActiveMissions();
    }

    private void populateAvailableMissions() {
        availableMissionsTable.clear();
        availableMissionsTable.top().left();
        if (availableMissions == null || availableMissions.isEmpty()) {
            availableMissionsTable.add(new Label("No available missions.", skin));
            return;
        }

        for (GroupMission mission : availableMissions) {
            Table missionCard = new Table();
            missionCard.defaults().pad(5).left();

            // استفاده از استایل پیش فرض برای جلوگیری از خطا
            Label nameLabel = new Label(mission.getName(), skin);
            missionCard.add(nameLabel).colspan(2).row();

            String capacity = "Players: " + mission.getJoinedPlayers().size() + "/" + mission.getCapacity();
            Label capacityLabel = new Label(capacity, skin);
            missionCard.add(capacityLabel).colspan(2).row();

            String required = "Required: " + mission.getRequiredAmount() + " " + mission.getRequiredItemName();
            Label requiredLabel = new Label(required, skin);
            missionCard.add(requiredLabel).colspan(2).row();

            String reward = "Reward: " + mission.getRewardPerPlayer() + " money per player";
            Label rewardLabel = new Label(reward, skin);
            missionCard.add(rewardLabel).colspan(2).row();

            TextButton joinButton = new TextButton("Join", skin);
            joinButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (activeMissions.size() >= 3) {
                        // نمایش پیغام خطا در خود UI (اختیاری ولی بهتر است)
                        Dialog dialog = new Dialog("Error", skin);
                        dialog.text("You cannot have more than 3 active missions.");
                        dialog.button("OK");
                        dialog.show(stage);
                    } else {
                        Map<String, Object> payload = new HashMap<>();
                        payload.put("missionId", mission.getId());
                        ClientMain.sendMessage(new Message(Message.ActionType.JOIN_GROUP_MISSION, payload));
                    }
                }
            });

            missionCard.add(joinButton).left();
            availableMissionsTable.add(missionCard).pad(10).row();
        }
    }

    private void populateActiveMissions() {
        activeMissionsTable.clear();
        activeMissionsTable.top().left();
        if (activeMissions == null || activeMissions.isEmpty()) {
            activeMissionsTable.add(new Label("You have no active missions.", skin));
            return;
        }

        for (GroupMission mission : activeMissions) {
            Table missionCard = new Table();
            missionCard.defaults().pad(5).left();

            // اینجا هم استایل title-plain را حذف کردیم تا از استایل پیش‌فرض استفاده شود
            Label nameLabel = new Label(mission.getName(), skin);
            missionCard.add(nameLabel).colspan(2).row();

            String required = "Required: " + mission.getRequiredAmount() + " " + mission.getRequiredItemName();
            Label requiredLabel = new Label(required, skin);
            missionCard.add(requiredLabel).colspan(2).row();

            String reward = "Reward: " + mission.getRewardPerPlayer() + " money per player";
            Label rewardLabel = new Label(reward, skin);
            missionCard.add(rewardLabel).colspan(2).row();

            String progress = "Progress: " + mission.getTotalContributions() + "/" + mission.getRequiredAmount();
            Label progressLabel = new Label(progress, skin);
            missionCard.add(progressLabel).colspan(2).row();

            // نمایش جزئیات مشارکت هر بازیکن
            Label contributionsTitle = new Label("Contributions:", skin);
            missionCard.add(contributionsTitle).colspan(2).padTop(10).row();
            for (Map.Entry<String, Integer> entry : mission.getContributions().entrySet()) {
                String contributionText = "  - " + entry.getKey() + ": " + entry.getValue();
                Label contributionLabel = new Label(contributionText, skin);
                missionCard.add(contributionLabel).colspan(2).row();
            }

            TextButton deliverButton = new TextButton("Deliver", skin);
            deliverButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    showDeliverDialog(mission);
                }
            });

            missionCard.add(deliverButton).left().padTop(10);
            activeMissionsTable.add(missionCard).pad(10).row();
        }
    }

    private void showDeliverDialog(GroupMission mission) {
        Dialog dialog = new Dialog("Deliver " + mission.getRequiredItemName(), skin);

        int maxAmount = player.getInventory().getItemQuantityByName(mission.getRequiredItemName());
        Slider slider = new Slider(1, maxAmount, 1, false, skin);
        Label amountLabel = new Label("1", skin);

        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                amountLabel.setText(String.valueOf((int)slider.getValue()));
            }
        });

        TextButton confirmButton = new TextButton("Confirm", skin);
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int amount = (int)slider.getValue();
                Map<String, Object> payload = new HashMap<>();
                payload.put("missionId", mission.getId());
                payload.put("amount", amount);
                System.out.println("[CLIENT LOG] Sending DELIVER_GROUP_MISSION_ITEM: missionId=" + mission.getId() +
                    ", itemName=" + mission.getRequiredItemName() + ", amount=" + amount);
                ClientMain.sendMessage(new Message(Message.ActionType.DELIVER_GROUP_MISSION_ITEM, payload));
                dialog.hide();
            }
        });

        dialog.text("Select amount to deliver:");
        dialog.getContentTable().row();
        dialog.getContentTable().add(slider).width(200).pad(20);
        dialog.getContentTable().add(amountLabel);
        dialog.button(confirmButton);
        dialog.button("Cancel");

        dialog.show(stage);
    }


    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
