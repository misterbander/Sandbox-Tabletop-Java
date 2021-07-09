package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;

import java.util.UUID;

import misterbander.sandboxtabletop.RoomScreen;
import misterbander.sandboxtabletop.net.model.FlipCardEvent;
import misterbander.sandboxtabletop.net.model.LockEvent;
import misterbander.sandboxtabletop.net.model.OwnerEvent;
import misterbander.sandboxtabletop.net.model.ServerCard;
import misterbander.sandboxtabletop.net.model.ServerObjectPosition;
import misterbander.sandboxtabletop.net.model.User;

public class Card extends SmoothMovable
{
	private final RoomScreen screen;
	
	private final Drawable faceUpDrawable, faceDownDrawable;
	public final Image cardImage;
	public @Null User lockHolder;
	public @Null User owner;
	
	private boolean isFaceUp;
	
	public Card(RoomScreen screen, UUID uuid, ServerCard.Rank rank, ServerCard.Suit suit, @Null User lockHolder, @Null User owner,
				float x, float y, float rotation, boolean isFaceUp)
	{
		this.screen = screen;
		
		faceUpDrawable = screen.game.skin.getDrawable("card" + suit.toString() + rank.toString());
		faceDownDrawable = screen.game.skin.getDrawable("cardbackred");
		
		cardImage = new Image(isFaceUp ? faceUpDrawable : faceDownDrawable);
		cardImage.setOrigin(Align.center);
		cardImage.addListener(new ClickListener()
		{
			@Override
			public void clicked(InputEvent event, float x, float y)
			{
				if (!isLocked())
					screen.client.send(new FlipCardEvent(uuid, !isFaceUp()));
			}
		});
		cardImage.addListener(new DragListener()
		{
			@Override
			public void dragStart(InputEvent event, float x, float y, int pointer)
			{
				if (!isLocked() && !isLockHolder() && (owner == null || owner.equals(screen.user)))
				{
					System.out.println("locking " + rank + " " + suit);
					screen.client.send(new LockEvent(screen.user, uuid));
				}
			}
			
			@Override
			public void drag(InputEvent event, float x, float y, int pointer)
			{
				if (isLockHolder())
				{
					setTargetPosition(getX() + x - getDragStartX(), getY() + y - getDragStartY());
					setPosition(getX() + x - getDragStartX(), getY() + y - getDragStartY());
					screen.latestServerObjectPosition = new ServerObjectPosition(uuid, getX(), getY());
					
					if (getY() < 64) // Put it in hand
					{
						if (screen.hand.addCard(Card.this))
							screen.client.send(new OwnerEvent(screen.user, uuid));
					}
					else
					{
						if (screen.hand.removeCard(Card.this))
							screen.client.send(new OwnerEvent(null, uuid));
					}
				}
			}
			
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer)
			{
				if (isLockHolder())
					screen.client.send(new LockEvent(null, uuid));
				screen.hand.arrangeCards(true);
			}
		});
		setPosition(x, y);
		setTargetPosition(x, y);
		setRotation(rotation);
		this.lockHolder = lockHolder;
		this.owner = owner;
		
		this.isFaceUp = isFaceUp;
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		cardImage.setPosition(getX(), getY());
		cardImage.setRotation(getRotation());
	}
	
	@Override
	public @Null Actor hit(float x, float y, boolean touchable)
	{
		if (!isVisible())
			return null;
		return cardImage.hit(x, y, touchable);
	}
	
	private boolean isLockHolder()
	{
		return screen.user.equals(lockHolder);
	}
	
	public boolean isLocked()
	{
		return lockHolder != null;
	}
	
	private boolean isFaceUp()
	{
		return isFaceUp;
	}
	
	public void setFaceUp(boolean isFaceUp)
	{
		this.isFaceUp = isFaceUp;
		cardImage.setDrawable(isFaceUp ? faceUpDrawable : faceDownDrawable);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		cardImage.draw(batch, parentAlpha);
	}
}
