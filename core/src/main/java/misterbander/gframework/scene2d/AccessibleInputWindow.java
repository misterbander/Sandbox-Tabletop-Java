package misterbander.gframework.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;

import misterbander.gframework.GScreen;

/**
 * Makes {@link Window}s that contain text fields accessible on mobile such that while editing them, the window gets shifted
 * upwards so it doesn't get covered by the on-screen keyboard
 *
 * For this to work, a layout size listener should be attached in Android that calls {@code GFramework::notifySizeChange()},
 * and the window must be added to the {@link GScreen}'s accessible window list.
 */
public abstract class AccessibleInputWindow extends Window
{
	private final Vector2 prevWindowPos = new Vector2();
	private final Vector2 windowScreenPos = new Vector2();
	private final Vector2 textFieldScreenPos = new Vector2();
	private int screenHeight = Gdx.graphics.getHeight();
	private boolean shouldShift = false;
	
	public AccessibleInputWindow(String title, Skin skin, String styleName)
	{
		super(title, skin, styleName);
	}
	
	public void addFocusListener(MBTextField textField)
	{
		textField.addListener(new FocusListener()
		{
			@Override
			public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused)
			{
				if (focused)
					adjustPosition(screenHeight);
			}
		});
	}
	
	public void attemptAdjustPositionOnLayoutSizeChange(int screenHeight)
	{
		Stage stage = getStage();
		if (stage == null || stage.getKeyboardFocus() == null || !(stage.getKeyboardFocus() instanceof MBTextField))
			return;
		this.screenHeight = screenHeight;
		MBTextField focusedTextField = (MBTextField)stage.getKeyboardFocus();
		float x = getX(), y = getY();
		stage.stageToScreenCoordinates(windowScreenPos.set(x, y));
		localToScreenCoordinates(textFieldScreenPos.set(focusedTextField.getX(), focusedTextField.getY()));
		
		if (screenHeight < Gdx.graphics.getHeight() - 160) // Keyboard up, 160 is an arbitrary keyboard height
		{
			prevWindowPos.set(x, y);
			if (adjustPosition(screenHeight))
				shouldShift = true;
		}
		else if (shouldShift)
		{
			setPosition(x, prevWindowPos.y);
			shouldShift = false;
		}
		Gdx.graphics.requestRendering();
	}
	
	private boolean adjustPosition(int screenHeight)
	{
		Stage stage = getStage();
		if (stage == null || stage.getKeyboardFocus() == null || !(stage.getKeyboardFocus() instanceof MBTextField))
			return false;
		MBTextField focusedTextField = (MBTextField)stage.getKeyboardFocus();
		float x = getX(), y = getY();
		stage.stageToScreenCoordinates(windowScreenPos.set(x, y));
		localToScreenCoordinates(textFieldScreenPos.set(focusedTextField.getX(), focusedTextField.getY()));
		
		if (textFieldScreenPos.y > screenHeight) // TextField is off screen
		{
			float diff = textFieldScreenPos.y - screenHeight;
			windowScreenPos.y -= diff;
			stage.screenToStageCoordinates(windowScreenPos);
			setPosition(windowScreenPos.x, windowScreenPos.y);
			return true;
		}
		return false;
	}
}
