package misterbander.sandboxtabletop.net.model;

import com.badlogic.gdx.utils.Null;

import java.io.Serializable;
import java.util.UUID;

public class OwnerEvent implements Serializable
{
	public final @Null User owner;
	public final UUID ownedUuid;
	
	public OwnerEvent(@Null User owner, UUID ownedUuid)
	{
		this.owner = owner;
		this.ownedUuid = ownedUuid;
	}
}
