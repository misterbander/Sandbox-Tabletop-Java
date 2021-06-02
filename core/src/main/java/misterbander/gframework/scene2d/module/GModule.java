package misterbander.gframework.scene2d.module;

import misterbander.gframework.GFramework;
import misterbander.gframework.scene2d.GObject;

/**
 * Modules can be added to GObjects to add custom behavior while running.
 */
public abstract class GModule<T extends GFramework>
{
	protected final GObject<T> parent;
	
	/**
	 * @param parent the parent GObject this module is attached to
	 */
	public GModule(GObject<T> parent)
	{
		this.parent = parent;
	}
	
	/**
	 * Gets called every frame.
	 * @param delta time after the last frame
	 */
	public void update(float delta) {}
}
