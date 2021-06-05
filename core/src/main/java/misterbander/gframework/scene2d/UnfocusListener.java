package misterbander.gframework.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

/**
 * Utility listener for windows and stages that unfocuses text fields, making it possible to
 * 'unselect' a text field.
 */
public class UnfocusListener extends InputListener
{
	private final Actor actor;
	
	public UnfocusListener(Actor actor)
	{
		this.actor = actor;
	}
	
	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button)
	{
		if (!(event.getTarget() instanceof MBTextField))
		{
			if (actor.getStage() != null)
				actor.getStage().setKeyboardFocus(null);
			Gdx.input.setOnscreenKeyboardVisible(false);
		}
		return true;
	}
}
