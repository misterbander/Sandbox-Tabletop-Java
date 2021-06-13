package misterbander.sandboxtabletop.net.model;

import java.io.Serializable;

public class UserList implements Serializable
{
	public final User[] users;
	
	public UserList(User[] users)
	{
		this.users = users;
	}
}
