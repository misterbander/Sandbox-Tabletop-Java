package misterbander.sandboxtabletop.net.model;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable
{
	public final String username;
	public final UUID uuid;
	
	public User(String username, UUID uuid)
	{
		this.username = username;
		this.uuid = uuid;
	}
	
	@Override
	public String toString()
	{
		return username + " (uuid: " + uuid + ")";
	}
}
