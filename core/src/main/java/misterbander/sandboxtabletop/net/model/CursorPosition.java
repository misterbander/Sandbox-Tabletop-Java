package misterbander.sandboxtabletop.net.model;

import java.io.Serializable;
import java.util.UUID;

public class CursorPosition implements Serializable
{
	public final UUID uuid;
	private int x, y;
	
	public CursorPosition(UUID uuid, int x, int y)
	{
		this.uuid = uuid;
		this.x = x;
		this.y = y;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public boolean set(float x, float y)
	{
		int newX = (int)x, newY = (int)y;
		boolean changed = false;
		if (this.x != newX)
		{
			this.x = newX; changed = true;
		}
		if (this.y != newY)
		{
			this.y = newY; changed = true;
		}
		return changed;
	}
}
