package misterbander.sandboxtabletop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;

import java.io.Serializable;

import misterbander.gframework.scene2d.MBTextField;
import misterbander.gframework.scene2d.UnfocusListener;
import misterbander.gframework.util.TextUtils;
import misterbander.sandboxtabletop.net.Connection;
import misterbander.sandboxtabletop.net.ConnectionEventListener;
import misterbander.sandboxtabletop.net.SandboxTabletopClient;
import misterbander.sandboxtabletop.net.model.Chat;
import misterbander.sandboxtabletop.net.model.User;

public class RoomScreen extends SandboxTabletopScreen implements ConnectionEventListener
{
	private final SandboxTabletopClient client;
	
	private final User user;
	
	private final ImageButton menuButton = new ImageButton(game.skin, "menubuttonstyle");
	/** Stores chat history */
	private final VerticalGroup chatHistory = new VerticalGroup();
	private final ScrollPane chatHistoryScrollPane = new ScrollPane(chatHistory, game.skin, "scrollpanestyle");
	/** Stores recent chat popup labels that disappear after 5 seconds. */
	private final VerticalGroup chatPopup = new VerticalGroup();
	
	public RoomScreen(SandboxTabletop game, SandboxTabletopClient client, User user)
	{
		super(game);
		this.client = client;
		
		this.user = user;
		
		// Set up UI
		
		MBTextField chatTextField = new MBTextField("", game.skin, "chattextfieldstyle");
		chatTextField.setMessageText(Gdx.app.getType() == Application.ApplicationType.Android ? "Tap here to chat..." : "Press T to chat...");
		chatTextField.setMaxLength(256);
		// Add a listener so that we can send chat on enter key
		chatTextField.addListener(new InputListener()
		{
			@Override
			public boolean keyTyped(InputEvent event, char character)
			{
				if (event.getKeyCode() == Input.Keys.ENTER && !chatTextField.getText().isEmpty())
				{
					client.send(new Chat(user, "<" + user.username + "> " + chatTextField.getText(), false));
					chatTextField.setText("");
					uiStage.setKeyboardFocus(null);
					return true;
				}
				return false;
			}
		});
		// Add another listener so that we can toggle between full chat history view or recent popup chat history view
		chatTextField.addListener(new FocusListener()
		{
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused)
			{
				chatPopup.setVisible(!focused);
				chatHistoryScrollPane.setVisible(focused);
			}
		});
		chatPopup.columnAlign(Align.left);
		chatHistory.grow();
		chatHistoryScrollPane.setVisible(false);
		uiStage.setScrollFocus(chatHistoryScrollPane);
		
		Table table = new Table();
		table.setFillParent(true);
		table.top();
		table.add(menuButton).top().pad(16);
		
		Table chatTable = new Table();
		chatTable.defaults().growX();
		chatTable.add(chatTextField);
		chatTable.row();
		Stack stack = new Stack();
		Container<VerticalGroup> chatPopupContainer = new Container<>(chatPopup);
		chatPopupContainer.top().left();
		stack.add(chatPopupContainer);
		stack.add(chatHistoryScrollPane);
		chatTable.add(stack).left();
		
		table.add(chatTable).pad(16).top().expandX().fillX().maxHeight(312);
		
		uiStage.addActor(table);
		uiStage.addListener(new UnfocusListener(chatTextField));
		uiStage.addListener(new InputListener()
		{
			@Override
			public boolean keyDown(InputEvent event, int keycode)
			{
				if (event.getKeyCode() == Input.Keys.T && !chatTextField.hasKeyboardFocus())
				{
					Gdx.app.postRunnable(() -> uiStage.setKeyboardFocus(chatTextField));
					return true;
				}
				else if (event.getKeyCode() == Input.Keys.ESCAPE)
				{
					uiStage.setKeyboardFocus(null);
					return true;
				}
				return false;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
		for (Actor actor : chatPopup.getChildren())
		{
			Container<Label> chatLabelContainer = (Container<Label>)actor;
			Label label = chatLabelContainer.getActor();
			assert label != null;
			chatLabelContainer.width(getChatTextWidth(label.getStyle().font, label.getText().toString()));
			chatLabelContainer.invalidateHierarchy();
		}
	}
	
	private int getChatTextWidth(BitmapFont font, String message)
	{
		return (int)Math.min(TextUtils.textSize(font, message).x + 32, uiViewport.getWorldWidth() - menuButton.getWidth() - 64);
	}
	
	/**
	 * Appends a chat message to the chat history, and adds a chat label that disappears after 5 seconds.
	 * @param message the message
	 * @param color   color of the chat message
	 */
	private void addChatMessage(String message, @Null Color color)
	{
		Label chatLabel = new Label(message, game.skin, "chatlabelstyle");
		Container<Label> chatLabelContainer = new Container<>(chatLabel);
		chatLabelContainer.width(getChatTextWidth(chatLabel.getStyle().font, message));
		chatLabel.setWrap(true);
		if (color != null)
			chatLabel.setColor(color.cpy());
		AlphaAction alphaAction = new AlphaAction(); // Action to fade out
		alphaAction.setAlpha(0);
		alphaAction.setDuration(1);
		RemoveActorAction removeActorAction = new RemoveActorAction(); // Action to remove label after fade out
		removeActorAction.setTarget(chatLabelContainer);
		chatLabelContainer.addAction(new SequenceAction(new DelayAction(10), alphaAction, removeActorAction));
		chatPopup.addActor(chatLabelContainer);
		if (chatPopup.getChildren().size == 7) // Maximum 6 children
		{
			Actor firstChatPopup = chatPopup.removeActorAt(0, false);
			firstChatPopup.clear();
		}
		
		// Add to history
		Label chatHistoryLabel = new Label(message, game.skin, "infolabelstyle");
		chatHistoryLabel.setWrap(true);
		if (color != null)
			chatHistoryLabel.setColor(color.cpy());
		chatHistory.pad(4, 16, 4, 16).space(8);
		chatHistory.addActor(chatHistoryLabel);
		chatHistoryScrollPane.layout();
		chatHistoryScrollPane.setScrollPercentY(100);
	}
	
	@Override
	public void objectReceived(Connection connection, Serializable object)
	{
		if (object instanceof Chat)
		{
			Chat chat = (Chat)object;
			Gdx.app.log("SandboxTabletopClient | CHAT", chat.message);
			addChatMessage(chat.message, chat.isSystemMessage ? Color.YELLOW : null);
		}
	}
}
