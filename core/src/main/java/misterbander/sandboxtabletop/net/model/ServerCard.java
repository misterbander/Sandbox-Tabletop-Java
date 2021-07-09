package misterbander.sandboxtabletop.net.model;

import com.badlogic.gdx.utils.Null;

import java.io.Serializable;
import java.util.Locale;
import java.util.UUID;

public class ServerCard implements Serializable, ServerObject
{
	private final UUID uuid;
	private float x, y;
	private float rotation;
	public final Rank rank;
	public final Suit suit;
	public boolean isFaceUp;
	
	public @Null User lockHolder;
	public @Null User owner;
	
	public ServerCard(UUID uuid, Rank rank, Suit suit)
	{
		this.uuid = uuid;
		this.rank = rank;
		this.suit = suit;
	}
	
	@Override
	public UUID getUUID()
	{
		return uuid;
	}
	
	@Override
	public float getX()
	{
		return x;
	}
	
	@Override
	public float getY()
	{
		return y;
	}
	
	@Override
	public void setPosition(float x, float y)
	{
		this.x = x; this.y = y;
	}
	
	@Override
	public float getRotation()
	{
		return rotation;
	}
	
	@Override
	public void setRotation(float rotation)
	{
		this.rotation = rotation;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ServerCard && ((ServerCard)obj).uuid.equals(uuid);
	}
	
	@Override
	public int hashCode()
	{
		return uuid.hashCode();
	}
	
	public enum Rank
	{
		NO_RANK, ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING;
		
		public String toString()
		{
			if (this == NO_RANK)
				return "";
			return this == ACE || this == JACK || this == QUEEN || this == KING ? super.toString().toLowerCase(Locale.ROOT) : String.valueOf(ordinal());
		}
	}
	
	public enum Suit
	{
		DIAMONDS, CLUBS, HEARTS, SPADES, JOKER;
		
		@Override
		public String toString()
		{
			return super.toString().toLowerCase(Locale.ROOT);
		}
	}
}
