package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class SmoothMovable extends Actor
{
	public float smoothingFactor;
	public float targetX, targetY;
	public float targetRotation;
	
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
		setPosition(getX() + (targetX - getX())/smoothingFactor*delta*60, getY() + (targetY - getY())/smoothingFactor*delta*60);
		float angle = getRotation()*MathUtils.radDeg;
		if (Math.abs(targetRotation - angle) > 1)
			setRotation(getRotation() + (targetRotation - angle)/smoothingFactor*delta*60);
		else
			setRotation(targetRotation);
	}
	
	public void setTargetPosition(float x, float y)
	{
		targetX = x;
		targetY = y;
	}
	
	public void setTargetRotation(float rotation)
	{
		targetRotation = rotation;
	}
}
