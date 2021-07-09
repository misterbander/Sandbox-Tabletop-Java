package misterbander.sandboxtabletop.net.model;

import java.io.Serializable;

public class ServerObjectList implements Serializable
{
	public final ServerObject[] objects;
	
	public ServerObjectList(ServerObject[] objects)
	{
		this.objects = objects;
	}
}
