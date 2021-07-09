package misterbander.sandboxtabletop.net.model;

import com.badlogic.gdx.utils.Null;

import java.io.Serializable;
import java.util.UUID;

public class LockEvent implements Serializable
{
	public final @Null User lockHolder;
	public final UUID lockedUuid;
	
	public LockEvent(@Null User lockHolder, UUID lockedUuid)
	{
		this.lockHolder = lockHolder;
		this.lockedUuid = lockedUuid;
	}
}
