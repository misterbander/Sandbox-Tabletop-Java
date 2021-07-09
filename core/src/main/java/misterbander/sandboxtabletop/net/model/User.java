package misterbander.sandboxtabletop.net.model;

import com.badlogic.gdx.graphics.Color;

import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

import misterbander.sandboxtabletop.scene2d.Cursor;

public class User implements Serializable
{
	public final String username;
	public final UUID uuid;
	public transient Cursor cursor;
	
	public User(String username, UUID uuid)
	{
		this.username = username;
		this.uuid = uuid;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof User && ((User)obj).uuid.equals(uuid);
	}
	
	@Override
	public int hashCode()
	{
		return uuid.hashCode();
	}
	
	@Override
	public String toString()
	{
		return username + " (uuid: " + uuid + ")";
	}
	
	public static Color getUserColor(User user)
	{
		Random random = new Random(user.uuid.hashCode());
		return Color.WHITE.cpy().fromHsv(random.nextFloat()*360, 0.8F, 0.8F);
	}
}
