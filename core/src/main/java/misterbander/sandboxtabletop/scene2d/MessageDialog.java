package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Null;

import misterbander.gframework.util.TextUtils;
import misterbander.sandboxtabletop.SandboxTabletopScreen;

/**
 * Represents pop up message dialogs. Contains a message and a button. Used to display error messages and status messages.
 */
public class MessageDialog extends SandboxTabletopWindow
{
	private final Label messageLabel = new Label("", game.skin, "infolabelstyle");
	private final TextButton textButton = new TextButton("", game.skin, "textbuttonstyle");
	private @Null Runnable closeAction;
	private @Null Runnable buttonAction;
	
	public MessageDialog(SandboxTabletopScreen screen, String title, boolean isModal)
	{
		super(screen, title, isModal);
		defaults().pad(16);
		
		textButton.addListener(screen.new ChangeListener(() ->
		{
			setVisible(false);
			if (buttonAction != null)
				buttonAction.run();
		}));
		textButton.getLabelCell().prefWidth(192);
		
		Table table = new Table();
		table.defaults().pad(16).space(16);
		table.add(messageLabel);
		table.row();
		table.add(textButton).center();
		add(table);
	}
	
	/**
	 * Displays this message dialog.
	 * @param title title of the dialog window
	 * @param message message of the dialog window
	 * @param buttonText text of the button
	 * @param closeAction what to execute when both the x and the button is pressed
	 */
	public void show(String title, String message, String buttonText, @Null Runnable closeAction)
	{
		show(title, message, buttonText, closeAction, closeAction);
	}
	
	/**
	 * Displays this message dialog.
	 * @param title title of the dialog window
	 * @param message message of the dialog window
	 * @param buttonText text of the button
	 * @param closeAction what to execute when the x is pressed
	 * @param buttonAction what to execute when the button is pressed
	 */
	public void show(String title, String message, String buttonText, @Null Runnable closeAction, @Null Runnable buttonAction)
	{
		setVisible(true);
		getTitleLabel().setText(title);
		messageLabel.setText(TextUtils.wrap(messageLabel.getStyle().font, message, 800));
		textButton.setText(buttonText);
		this.closeAction = closeAction;
		this.buttonAction = buttonAction;
		pack();
		centerPosition();
	}
	
	@Override
	protected void close()
	{
		super.close();
		if (closeAction != null)
			closeAction.run();
	}
}
