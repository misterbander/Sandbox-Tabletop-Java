package misterbander.sandboxtabletop.net.model;

import java.io.Serializable;

public abstract class UserEvent implements Serializable
{
	public final User user;
	
	private UserEvent(User user)
	{
		this.user = user;
	}
	
	public static class UserJoinEvent extends UserEvent
	{
		public UserJoinEvent(User user)
		{
			super(user);
		}
	}
	
	public static class UserLeaveEvent extends UserEvent
	{
		public UserLeaveEvent(User user)
		{
			super(user);
		}
	}
}
