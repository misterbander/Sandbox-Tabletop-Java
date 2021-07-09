package misterbander.sandboxtabletop.net.model;

import java.io.Serializable;
import java.util.UUID;

public class ServerObjectPosition implements Serializable
{
	public final UUID uuid;
	public final float x, y;
	
	public ServerObjectPosition(UUID uuid, float x, float y)
	{
		this.uuid = uuid;
		this.x = x;
		this.y = y;
	}
}
