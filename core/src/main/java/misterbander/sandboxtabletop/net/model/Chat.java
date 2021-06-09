package misterbander.sandboxtabletop.net.model;

import java.io.Serializable;

public class Chat implements Serializable
{
	public final User user;
	public final String message;
	public final boolean isSystemMessage;
	
	public Chat(User user, String message, boolean isSystemMessage)
	{
		this.user = user;
		this.message = message;
		this.isSystemMessage = isSystemMessage;
	}
}
