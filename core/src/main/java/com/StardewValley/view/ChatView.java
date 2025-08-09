package com.StardewValley.view;

import com.StardewValley.Network.Client.ClientMain;
import com.StardewValley.Network.Message;
import com.StardewValley.controller.LobbyController;
import com.StardewValley.models.User;
import com.StardewValley.AssetsManager.MenuManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatView implements Screen {
    private com.badlogic.gdx.Game game;
    private LobbyController lobbyController;
    private Stage stage;
    private Skin skin;

    private TextField messageInputField;
    private TextButton sendButton;
    private Table chatHistoryTable;
    private ScrollPane scrollPane;

    private TextButton publicChatButton;
    private TextButton privateChatButton;
    private TextField privateRecipientField;

    private String currentChatType = "PUBLIC";
    private String privateRecipient = "";

    private TextButton backButton;

    public interface ChatViewListener {
        void onChatClosed();
    }

    private List<ChatViewListener> listeners = new ArrayList<>();

    public void addChatViewListener(ChatViewListener listener) {
        listeners.add(listener);
    }

    private void notifyChatClosed() {
        for (ChatViewListener listener : listeners) {
            listener.onChatClosed();
        }
    }

    public ChatView(com.badlogic.gdx.Game game, LobbyController lobbyController) {
        this.game = game;
        this.lobbyController = lobbyController;
        this.skin = MenuManager.getInstance().getPixthulhuSkin();
        this.stage = new Stage();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        setupUI();
        // Request lobby state update to get the latest chat history
        if (lobbyController.getCurrentLobby() != null) {
            lobbyController.requestLobbyStateUpdate(lobbyController.getCurrentLobby().getId());
            System.out.println("CHAT_VIEW_DEBUG: Requested lobby state update for chat history.");
        }
        scrollPane.scrollTo(0, 0, 0, 0); // Scroll to bottom after loading messages
    }

    private void setupUI() {
        stage.clear();

        Table rootTable = new Table(skin);
        rootTable.setFillParent(true);
        rootTable.pad(10);

        backButton = new TextButton("Back to Map", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                notifyChatClosed();
            }
        });
        rootTable.add(backButton).expandX().align(Align.left).padBottom(10).row();

        Table chatTypeTable = new Table(skin);
        chatTypeTable.defaults().pad(5);

        publicChatButton = new TextButton("Public Chat", skin, "toggle");
        publicChatButton.setChecked(true);
        publicChatButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentChatType = "PUBLIC";
                privateRecipientField.setVisible(false);
                publicChatButton.setChecked(true);
                privateChatButton.setChecked(false);
                System.out.println("CHAT_VIEW_DEBUG: Switched to Public Chat");
            }
        });
        chatTypeTable.add(publicChatButton).width(150);

        privateChatButton = new TextButton("Private Chat", skin, "toggle");
        privateChatButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentChatType = "PRIVATE";
                privateRecipientField.setVisible(true);
                privateChatButton.setChecked(true);
                publicChatButton.setChecked(false);
                System.out.println("CHAT_VIEW_DEBUG: Switched to Private Chat");
            }
        });
        chatTypeTable.add(privateChatButton).width(150);

        privateRecipientField = new TextField("", skin);
        privateRecipientField.setMessageText("Recipient Username (Private)");
        privateRecipientField.setVisible(false);
        chatTypeTable.add(privateRecipientField).expandX().fillX();

        rootTable.add(chatTypeTable).expandX().fillX().padBottom(10).row();

        chatHistoryTable = new Table(skin);
        chatHistoryTable.align(Align.bottomLeft);

        scrollPane = new ScrollPane(chatHistoryTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsVisible(true);
        scrollPane.setForceScroll(false, true);
        scrollPane.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getTarget() == scrollPane) {
                    event.stop(); // Prevent event from bubbling up to parents
                }
            }
        });

        rootTable.add(scrollPane).expand().fill().padBottom(10).row();

        Table inputTable = new Table(skin);
        inputTable.defaults().pad(5);

        messageInputField = new TextField("", skin);
        messageInputField.setMessageText("Type your message...");
        messageInputField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (c == '\n' || c == '\r') {
                    sendMessage();
                }
            }
        });
        inputTable.add(messageInputField).expandX().fillX();

        sendButton = new TextButton("Send", skin);
        sendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sendMessage();
            }
        });
        inputTable.add(sendButton).width(80);

        rootTable.add(inputTable).expandX().fillX().row();

        stage.addActor(rootTable);
    }

    private void sendMessage() {
        String messageText = messageInputField.getText().trim();
        if (messageText.isEmpty()) {
            return;
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("message", messageText);
        payload.put("sender", lobbyController.getLoginController().getLoggedInUser().getUsername());

        Message chatMessage;
        if ("PUBLIC".equalsIgnoreCase(currentChatType)) {
            chatMessage = new Message(Message.ActionType.CHAT_MESSAGE_PUBLIC, payload);
        } else { // PRIVATE
            privateRecipient = privateRecipientField.getText().trim();
            if (privateRecipient.isEmpty()) {
                addMessageToHistory("[System]: Please enter a recipient for private message.");
                return;
            }
            if (privateRecipient.equalsIgnoreCase(lobbyController.getLoginController().getLoggedInUser().getUsername())) {
                addMessageToHistory("[System]: Cannot send private message to yourself.");
                return;
            }
            payload.put("recipient", privateRecipient); // Ensure recipient is in payload for server
            chatMessage = new Message(Message.ActionType.CHAT_MESSAGE_PRIVATE, payload);
            // REMOVED: addMessageToHistory("You (to " + privateRecipient + "): " + messageText); // Do NOT add locally for private chat
        }

        ClientMain.sendMessage(chatMessage);
        System.out.println("CHAT_VIEW_DEBUG: Sent message: " + messageText + " as " + currentChatType + " to " + privateRecipient);
        messageInputField.setText("");
        scrollPane.scrollTo(0, 0, 0, 0); // Scroll to bottom after sending
    }

    /**
     * Adds a single message to the chat history display.
     * @param fullMessage The formatted message string to display.
     */
    public void addMessageToHistory(final String fullMessage) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Label messageLabel = new Label(fullMessage, skin);
                messageLabel.setWrap(true);
                chatHistoryTable.add(messageLabel).expandX().fillX().align(Align.left).padBottom(2).row();
                scrollPane.scrollTo(0, 0, 0, 0);
                System.out.println("CHAT_VIEW_DEBUG: Added message to history: " + fullMessage);
            }
        });
    }

    /**
     * Sets the entire chat history for the display. Clears existing messages.
     * @param history The list of historical messages to display.
     */
    public void setChatHistory(final List<String> history) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                chatHistoryTable.clear(); // Clear existing messages
                if (history != null) {
                    for (String msg : history) {
                        Label messageLabel = new Label(msg, skin);
                        messageLabel.setWrap(true);
                        chatHistoryTable.add(messageLabel).expandX().fillX().align(Align.left).padBottom(2).row();
                    }
                }
                scrollPane.scrollTo(0, 0, 0, 0); // Scroll to bottom after loading
                System.out.println("CHAT_VIEW_DEBUG: Chat history set with " + (history != null ? history.size() : 0) + " messages.");
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getActors().get(0).setSize(width, height);
        ((Table) stage.getActors().get(0)).invalidateHierarchy();
        System.out.println("CHAT_VIEW_DEBUG: Resized to " + width + "x" + height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
