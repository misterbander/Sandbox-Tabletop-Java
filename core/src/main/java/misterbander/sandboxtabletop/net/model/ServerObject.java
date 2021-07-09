package misterbander.sandboxtabletop.net.model;

import java.util.UUID;

public interface ServerObject
{
	UUID getUUID();
	
	float getX();
	
	float getY();
	
	default float getRotation()
	{
		return 0;
	}
	
	void setPosition(float x, float y);
	
	default void setRotation(float rotation) {}
}
