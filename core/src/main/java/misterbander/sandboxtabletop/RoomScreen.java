package misterbander.sandboxtabletop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
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
import misterbander.sandboxtabletop.net.Connection;
import misterbander.sandboxtabletop.net.ConnectionEventListener;
import misterbander.sandboxtabletop.net.SandboxTabletopClient;
import misterbander.sandboxtabletop.net.model.Chat;
import misterbander.sandboxtabletop.net.model.User;

public class RoomScreen extends SandboxTabletopScreen implements ConnectionEventListener
{
	private final SandboxTabletopClient client;
	
	private final User user;
	
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
		
		ImageButton menuButton = new ImageButton(game.skin, "menubuttonstyle");
		MBTextField chatTextField = new MBTextField("", game.skin, "chattextfieldstyle");
		chatTextField.setMessageText(Gdx.app.getType() == Application.ApplicationType.Android ? "Tap here to chat..." : "Press T to chat...");
		chatTextField.setMaxLength(128);
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
		table.setDebug(true, true);
		table.top();
		table.add(menuButton).top().pad(16);
		
		Table chatTable = new Table();
		chatTable.defaults().growX();
		chatTable.add(chatTextField);
		chatTable.row();
		Stack stack = new Stack();
		Table chatPopupTable = new Table();
		chatPopupTable.add(chatPopup).expand().top().left();
		stack.add(chatPopupTable);
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
	
	/**
	 * Appends a chat message to the chat history, and adds a chat label that disappears after 5 seconds.
	 * @param message the message
	 * @param color   color of the chat message
	 */
	private void addChatMessage(String message, @Null Color color)
	{
		Label chatLabel = new Label(message, game.skin, "chatlabelstyle");
		if (color != null)
			chatLabel.setColor(color.cpy());
		AlphaAction alphaAction = new AlphaAction(); // Action to fade out
		alphaAction.setAlpha(0);
		alphaAction.setDuration(1);
		RemoveActorAction removeActorAction = new RemoveActorAction(); // Action to remove label after fade out
		removeActorAction.setTarget(chatLabel);
		chatLabel.addAction(new SequenceAction(new DelayAction(10), alphaAction, removeActorAction));
		chatPopup.addActor(chatLabel);
		if (chatPopup.getChildren().size == 7) // Maximum 6 children
		{
			Actor firstChatPopup = chatPopup.removeActorAt(0, false);
			firstChatPopup.clear();
		}
		
		// Add to history
		Label chatHistoryLabel = new Label(message, game.skin, "infolabelstyle");
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
