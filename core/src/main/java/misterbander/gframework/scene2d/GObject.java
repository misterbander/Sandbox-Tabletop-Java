package misterbander.gframework.scene2d;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import misterbander.gframework.GFramework;
import misterbander.gframework.GScreen;
import misterbander.gframework.scene2d.module.GModule;

/**
 * {@code GObject}s are special {@code Scene2D} groups that can hold child actors and can contain modules that add
 * custom behavior.
 */
public class GObject<T extends GFramework> extends Group
{
	public final GScreen<T> screen;
	
	protected final Array<GModule<T>> modules = new Array<>();
	
	public GObject(GScreen<T> screen)
	{
		this.screen = screen;
	}
	
	/**
	 * Called when this {@code GObject} is spawned to the world and added to the stage.
	 */
	public void onSpawn() {}
	
	/**
	 * "Update" method for the `GObject`. This gets called every frame. If overridden, make sure to call `super.act()`.
	 */
	@Override
	public void act(float delta)
	{
		super.act(delta);
		for (GModule<T> module : modules)
			module.update(delta);
	}
	
	/**
	 * Marks this `GObject` to be destroyed. It will be removed at the end of world time step.
	 */
	public void destroy()
	{
		screen.scheduledRemovalGObjects.add(this);
	}
}
