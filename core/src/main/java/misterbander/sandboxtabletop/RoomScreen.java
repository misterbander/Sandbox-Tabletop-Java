package misterbander.sandboxtabletop;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveActorAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;

import misterbander.gframework.scene2d.MBTextField;
import misterbander.gframework.scene2d.UnfocusListener;
import misterbander.sandboxtabletop.net.ConnectionEventListener;
import misterbander.sandboxtabletop.net.model.User;

public class RoomScreen extends SandboxTabletopScreen implements ConnectionEventListener
{
	private final User user;
	
	/** Stores recent chat popup labels that disappear after 5 seconds. */
	private final VerticalGroup chatPopupLabels = new VerticalGroup();
	
	public RoomScreen(SandboxTabletop game, User user)
	{
		super(game);
		this.user = user;
		
		// Set up UI
		
		ImageButton menuButton = new ImageButton(game.skin, "menubuttonstyle");
		
		MBTextField chatTextField = new MBTextField("", game.skin, "chattextfieldstyle");
		chatTextField.setMessageText("Tap here to chat...");
		chatTextField.addListener(new InputListener()
		{
			@Override
			public boolean keyTyped(InputEvent event, char character)
			{
				if (event.getKeyCode() == Input.Keys.ENTER && !chatTextField.getText().isEmpty())
				{
					addChatMessage("<" + user.username + "> " + chatTextField.getText());
					chatTextField.setText("");
					return true;
				}
				return false;
			}
		});
		
		Table chatTable = new Table();
		chatTable.add(chatTextField).growX();
		chatTable.row();
		chatTable.add(chatPopupLabels).left();
		chatPopupLabels.columnAlign(Align.left);
		
		Table table = new Table();
		table.setFillParent(true);
		table.setDebug(true, true);
		table.top();
		table.add(menuButton).top().pad(16);
		table.add(chatTable).pad(16).top().expandX().fillX();
		
		uiStage.addActor(table);
		uiStage.addListener(new UnfocusListener(chatTextField));
	}
	
	/**
	 * Appends a chat message to the chat history, and adds a chat label that disappears after 5 seconds.
	 * @param message the message
	 */
	public void addChatMessage(String message)
	{
		addChatMessage(message, null);
	}
	
	/**
	 * Appends a chat message to the chat history, and adds a chat label that disappears after 5 seconds.
	 * @param message the message
	 * @param color   color of the chat message
	 */
	public void addChatMessage(String message, @Null Color color)
	{
		Label chatLabel = new Label(message, game.skin, "chatlabelstyle");
		if (color != null)
			chatLabel.setColor(color.cpy());
		AlphaAction alphaAction = new AlphaAction(); // Action to fade out
		alphaAction.setAlpha(0);
		alphaAction.setDuration(1);
		RemoveActorAction removeActorAction = new RemoveActorAction(); // Action to remove label after fade out
		removeActorAction.setTarget(chatLabel);
		chatLabel.addAction(new SequenceAction(new DelayAction(5), alphaAction, removeActorAction));
		chatPopupLabels.addActor(chatLabel);
	}
}
