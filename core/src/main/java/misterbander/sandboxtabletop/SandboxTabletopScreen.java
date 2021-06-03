package misterbander.sandboxtabletop;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Null;

import misterbander.gframework.GScreen;

public abstract class SandboxTabletopScreen extends GScreen<SandboxTabletop>
{
	public final Sound click = game.getAssetManager().get("sounds/click.wav");
	
	public SandboxTabletopScreen(SandboxTabletop game)
	{
		super(game);
	}
	
	public class ChangeListener extends com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
	{
		private final Runnable changeAction;
		private final @Null Runnable soundAction;
		
		public ChangeListener(Runnable changeAction)
		{
			this(changeAction, click::play);
		}
		
		public ChangeListener(Runnable changeAction, @Null Runnable soundAction)
		{
			this.changeAction = changeAction;
			this.soundAction = soundAction;
		}
		
		@Override
		public void changed(ChangeEvent event, Actor actor)
		{
			if (soundAction != null)
				soundAction.run();
			changeAction.run();
		}
	}
}
