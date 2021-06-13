package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class SmoothMovable extends Actor
{
	public float smoothingFactor;
	public int targetX, targetY;
	
	public SmoothMovable()
	{
		this(2.5F);
	}
	
	public SmoothMovable(float smoothingFactor)
	{
		this.smoothingFactor = smoothingFactor;
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		setPosition(getX() + (targetX - getX())/smoothingFactor, getY() + (targetY - getY())/smoothingFactor);
	}
	
	public void setTargetPosition(int x, int y)
	{
		targetX = x;
		targetY = y;
	}
}
