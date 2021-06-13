package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class SmoothMovable extends Actor
{
	public int targetX, targetY;
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		setPosition(getX() + (targetX - getX())/1.7F, getY() + (targetY - getY())/1.7F);
	}
	
	public void setTargetPosition(int x, int y)
	{
		targetX = x;
		targetY = y;
	}
}
