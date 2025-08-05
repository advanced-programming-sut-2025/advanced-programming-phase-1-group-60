package com.StardewValley.view;

import com.StardewValley.AssetsManager.EmojiManager;
import com.StardewValley.Network.Client.ClientMain;
import com.StardewValley.models.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

public class EmojiReactionDialog extends Dialog {
    private static final int EMOJI_SIZE = 48;
    private static final int MAX_TEXT_LENGTH = 10;

    private EmojiManager emojiManager;
    private String username;
    private Table quickEmojiTable;
    private TextField customTextField;
    private int selectedEmojiId = -1;
    private List<DialogListener> listeners = new ArrayList<>();

    // Add a listener interface for reactions
    public interface DialogListener {
        void onReactionSent(int emojiId, String text);
    }

    public EmojiReactionDialog(Skin skin) {
        super("Reactions", skin);
        emojiManager = EmojiManager.getInstance();
        username = Game.getInstance().getCurrentPlayer().getUsername();

        createUI();
    }

    public void addDialogListener(DialogListener listener) {
        listeners.add(listener);
    }

    private void createUI() {
        // Main container
        Table mainTable = getContentTable();
        mainTable.pad(10);

        // Quick emoji section
        Label quickLabel = new Label("Quick Reactions", getSkin(), "default");
        mainTable.add(quickLabel).colspan(5).align(Align.left).padBottom(5).row();

        quickEmojiTable = new Table();
        refreshQuickEmojis();
        mainTable.add(quickEmojiTable).colspan(5).padBottom(10).row();

        // Custom text field
        Label textLabel = new Label("Custom Message (10 chars max):", getSkin());
        mainTable.add(textLabel).colspan(5).align(Align.left).padBottom(5).row();

        customTextField = new TextField("", getSkin());
        customTextField.setMaxLength(MAX_TEXT_LENGTH);
        mainTable.add(customTextField).colspan(5).fillX().padBottom(10).row();

        // Button to access all emojis
        TextButton allEmojisButton = new TextButton("Change Quick Emojis", getSkin());
        allEmojisButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showAllEmojisDialog();
            }
        });
        mainTable.add(allEmojisButton).colspan(5).padBottom(10).row();

        // Send and cancel buttons
        TextButton sendButton = new TextButton("Send", getSkin());
        sendButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedEmojiId >= 0) {
                    String text = customTextField.getText();

                    // Notify listeners
                    for (DialogListener listener : listeners) {
                        listener.onReactionSent(selectedEmojiId, text);
                    }

                    hide();
                } else {
                    // Show error if no emoji selected
                    Dialog errorDialog = new Dialog("Error", getSkin());
                    errorDialog.text("Please select an emoji first");
                    errorDialog.button("OK");
                    errorDialog.show(getStage());
                }
            }
        });

        TextButton cancelButton = new TextButton("Cancel", getSkin());
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                hide();
            }
        });

        getButtonTable().pad(10);
        getButtonTable().add(sendButton).padRight(10);
        getButtonTable().add(cancelButton);
    }

    private void refreshQuickEmojis() {
        quickEmojiTable.clear();
        List<Integer> quickEmojis = emojiManager.getUserQuickEmojis(username);

        for (int i = 0; i < quickEmojis.size(); i++) {
            final int emojiId = quickEmojis.get(i);
            Texture emojiTexture = emojiManager.getEmojiTexture(emojiId);

            if (emojiTexture != null) {
                Image emojiImage = new Image(emojiTexture);
                emojiImage.setSize(EMOJI_SIZE, EMOJI_SIZE);

                // Create a container for the emoji with background
                Table container = new Table(getSkin());

                // Use a simple colored background instead of a drawable
                if (selectedEmojiId == emojiId) {
                    container.setBackground(new Image(getSkin().getDrawable("window")).getDrawable());
                    // If window drawable doesn't exist, try these alternatives:
                    // container.setBackground(getSkin().getDrawable("default-round"));
                    // container.setBackground(getSkin().getDrawable("button"));

                    // Last resort fallback - set the color directly
                    container.setColor(0.3f, 0.8f, 1f, 0.5f);
                }

                container.add(emojiImage).size(EMOJI_SIZE, EMOJI_SIZE).pad(4);
                quickEmojiTable.add(container).size(EMOJI_SIZE + 10, EMOJI_SIZE + 10).pad(5);

                // Add click listener
                container.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selectedEmojiId = emojiId;
                        refreshQuickEmojis(); // Refresh to show selection
                    }
                });
            }

            if ((i + 1) % 5 == 0) {
                quickEmojiTable.row();
            }
        }
    }

    private void showAllEmojisDialog() {
        final Dialog allEmojisDialog = new Dialog("All Emojis", getSkin());
        ScrollPane scrollPane;

        Table emojiGrid = new Table();
        emojiGrid.pad(10);

        List<Integer> allEmojiIds = emojiManager.getAllEmojiIds();
        int cols = 5;

        for (int i = 0; i < allEmojiIds.size(); i++) {
            final int emojiId = allEmojiIds.get(i);
            Texture emojiTexture = emojiManager.getEmojiTexture(emojiId);

            if (emojiTexture != null) {
                final Image emojiImage = new Image(emojiTexture);
                emojiImage.setSize(EMOJI_SIZE, EMOJI_SIZE);

                emojiGrid.add(emojiImage).size(EMOJI_SIZE, EMOJI_SIZE).pad(5);

                // Add click listener to select emoji for quick list
                emojiImage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selectEmojiForQuickList(emojiId);
                        allEmojisDialog.hide();
                    }
                });

                if ((i + 1) % cols == 0) {
                    emojiGrid.row();
                }
            }
        }

        scrollPane = new ScrollPane(emojiGrid, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        allEmojisDialog.getContentTable().add(scrollPane).width(EMOJI_SIZE * cols + 40).height(300);

        TextButton closeButton = new TextButton("Close", getSkin());
        closeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                allEmojisDialog.hide();
            }
        });
        allEmojisDialog.getButtonTable().pad(10).add(closeButton);

        allEmojisDialog.show(getStage());
    }

    private void selectEmojiForQuickList(int emojiId) {
        final Dialog slotDialog = new Dialog("Select Quick Slot", getSkin());

        Label instructions = new Label("Select which quick slot to replace (1-5):", getSkin());
        slotDialog.getContentTable().add(instructions).pad(10).row();

        Table buttonTable = new Table();

        for (int i = 0; i < 5; i++) {
            final int slotIndex = i;
            TextButton slotButton = new TextButton("Slot " + (i + 1), getSkin());
            slotButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    emojiManager.setUserQuickEmoji(username, slotIndex, emojiId);
                    refreshQuickEmojis();
                    slotDialog.hide();
                }
            });
            buttonTable.add(slotButton).pad(5);
        }

        slotDialog.getContentTable().add(buttonTable);

        TextButton cancelButton = new TextButton("Cancel", getSkin());
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                slotDialog.hide();
            }
        });
        slotDialog.getButtonTable().pad(10).add(cancelButton);

        slotDialog.show(getStage());
    }
}
