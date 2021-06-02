package misterbander.sandboxtabletop;

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
		System.out.println("hello");
	}
}
