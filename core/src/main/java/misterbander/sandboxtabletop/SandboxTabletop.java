package misterbander.sandboxtabletop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import misterbander.gframework.GFramework;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class SandboxTabletop extends GFramework
{
	@Override
	public void create()
	{
		super.create();
		Gdx.graphics.setContinuousRendering(false);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		setScreen(new MenuScreen(this));
	}
}
