package misterbander.sandboxtabletop.scene2d;

import com.badlogic.gdx.utils.Array;

import misterbander.sandboxtabletop.RoomScreen;

public class Hand
{
	private final RoomScreen screen;
	
	private final Array<Card> cards = new Array<>();
	
	public Hand(RoomScreen screen)
	{
		this.screen = screen;
	}
	
	public boolean addCard(Card card)
	{
		if (cards.contains(card, false))
			return false;
		cards.add(card);
		arrangeCards(false);
		return true;
	}
	
	public boolean removeCard(Card card)
	{
		if (cards.removeValue(card, true))
		{
			arrangeCards(false);
			return true;
		}
		return false;
	}
	
	public void arrangeCards(boolean includeLocked)
	{
		System.out.println(cards.size);
		if (cards.isEmpty())
			return;
		float cardSeparation = 45;
		float leftMostCardX = 720 - cards.get(0).cardImage.getPrefWidth() - cardSeparation/2*(cards.size - 1);
		for (int i = 0; i < cards.size; i++)
		{
			Card card = cards.get(i);
			card.setZIndex(screen.uuidActorMap.size - cards.size + i + 1);
			if (card.isLocked() && !includeLocked)
				continue;
			card.setTargetPosition(leftMostCardX + i*cardSeparation, 10 - Math.abs(i - (float)cards.size/2));
//			card.setTargetRotation((-i + (float)cards.size/2)*2);
		}
	}
}
