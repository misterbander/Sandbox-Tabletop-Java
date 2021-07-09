package misterbander.sandboxtabletop.net.model;

import java.io.Serializable;
import java.util.UUID;

public class FlipCardEvent implements Serializable
{
	public final UUID uuid;
	public final boolean isFaceUp;
	
	public FlipCardEvent(UUID uuid, boolean isFaceUp)
	{
		this.uuid = uuid;
		this.isFaceUp = isFaceUp;
	}
}
