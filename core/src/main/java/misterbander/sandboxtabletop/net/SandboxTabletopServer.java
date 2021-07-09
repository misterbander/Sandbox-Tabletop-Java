package misterbander.sandboxtabletop.net;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import misterbander.sandboxtabletop.net.model.Chat;
import misterbander.sandboxtabletop.net.model.CursorPosition;
import misterbander.sandboxtabletop.net.model.FlipCardEvent;
import misterbander.sandboxtabletop.net.model.LockEvent;
import misterbander.sandboxtabletop.net.model.OwnerEvent;
import misterbander.sandboxtabletop.net.model.ServerCard;
import misterbander.sandboxtabletop.net.model.ServerObject;
import misterbander.sandboxtabletop.net.model.ServerObjectList;
import misterbander.sandboxtabletop.net.model.ServerObjectPosition;
import misterbander.sandboxtabletop.net.model.User;
import misterbander.sandboxtabletop.net.model.UserEvent;
import misterbander.sandboxtabletop.net.model.UserList;

/**
 * This is the server that can be run separately.
 */
public class SandboxTabletopServer extends Thread implements ConnectionEventListener
{
	private final ServerSocket serverSocket;
	private final Array<Connection> connections = new Array<>();
	private final ObjectMap<Connection, User> connectionUserMap = new ObjectMap<>();
	
	private final ObjectMap<UUID, ServerObject> uuidObjectMap = new ObjectMap<>();
	private final Array<ServerObject> objects = new Array<>();
	
	public static void main(String[] args) throws IOException
	{
		int port = 11530;
		if (args.length > 0)
			port = Integer.parseInt(args[0]);
		new SandboxTabletopServer(port).start();
	}
	
	/**
	 * Creates a {@code SandboxTabletopServer} that will listen and accept incoming connections forever on the specified port.
	 * The server will be run on a separate thread.
	 * @param port port for the server to listen to
	 * @throws IOException if an I/O error occurs when opening the socket.
	 */
	public SandboxTabletopServer(int port) throws IOException
	{
		this.serverSocket = new ServerSocket(port);
		System.out.println("[SandboxTabletopServer | INFO] Created server at port " + port);
		
		for (int i = 0; i < 4; i++)
		{
			ServerCard.Suit suit = ServerCard.Suit.values()[i];
			for (int j = 1; j <= 13; j++)
			{
				ServerCard.Rank rank = ServerCard.Rank.values()[j];
				addCard(rank, suit);
			}
		}
		addCard(ServerCard.Rank.NO_RANK, ServerCard.Suit.JOKER);
		addCard(ServerCard.Rank.NO_RANK, ServerCard.Suit.JOKER);
		objects.shuffle();
		for (int i = 0; i < objects.size; i++)
		{
			ServerCard card = (ServerCard)objects.get(i);
			card.setPosition(640 - i, 260 + i);
		}
	}
	
	public void addCard(ServerCard.Rank rank, ServerCard.Suit suit)
	{
		UUID uuid;
		do
		{
			uuid = UUID.randomUUID(); // Generate a random UUID that is not already used
		}
		while (uuidObjectMap.containsKey(uuid));
		ServerCard serverCard = new ServerCard(uuid, rank, suit);
		uuidObjectMap.put(serverCard.getUUID(), serverCard);
		objects.add(serverCard);
	}
	
	@Override
	public void run()
	{
		System.out.println("[SandboxTabletopServer | INFO] Ready to accept connections");
		try
		{
			while (true) // Repeat forever to accept incoming connections
			{
				Socket socket = serverSocket.accept();
				Connection connection = new Connection(this, socket);
				connection.start();
				connectionOpened(connection);
				connections.add(connection);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void broadcast(Serializable object)
	{
		for (int i = 0; i < connections.size; i++)
			connections.get(i).send(object);
	}
	
	/**
	 * Shuts down the server. This will close all connections and invoke {@code connectionClosed()} on all
	 * {@code ConnectionEventListener}s.
	 */
	public void shutdown() throws IOException
	{
		while (!connections.isEmpty())
			connections.first().close();
		serverSocket.close();
	}
	
	@Override
	public void connectionOpened(Connection connection)
	{
		System.out.println("[SandboxTabletopServer | INFO] Connection from " + connection.remoteAddress);
	}
	
	@Override
	public void connectionClosed(Connection connection, Exception e)
	{
		System.out.println("[SandboxTabletopServer | INFO] " + connection.remoteAddress + " disconnected");
		connections.removeValue(connection, true);
		User user = connectionUserMap.remove(connection);
		if (user != null)
		{
			System.out.println("[SandboxTabletopServer | INFO] " + user + " left the game");
			broadcast(new UserEvent.UserLeaveEvent(user));
		}
	}
	
	@Override
	public void objectReceived(Connection connection, Serializable object)
	{
		if (object instanceof User)
		{
			User user = (User)object;
			System.out.println("[SandboxTabletopServer | INFO] " + user + " joined the game");
			connectionUserMap.put(connection, user);
			
			// Client state synchronization
			connection.send(new UserList(connectionUserMap.values().toArray().toArray(User.class)));
			connection.send(new ServerObjectList(objects.toArray(ServerObject.class)));
			
			broadcast(new UserEvent.UserJoinEvent(user));
		}
		else if (object instanceof Chat)
		{
			Chat chat = (Chat)object;
			System.out.println(chat.message);
			broadcast(chat);
		}
		else if (object instanceof CursorPosition)
			broadcast(object);
		else if (object instanceof LockEvent)
		{
			LockEvent event = (LockEvent)object;
			ServerObject ownedObject = uuidObjectMap.get(event.lockedUuid);
			if (ownedObject instanceof ServerCard)
			{
				ServerCard card = (ServerCard)ownedObject;
				System.out.println("[SandboxTabletopServer | INFO] " + event.lockHolder + " trying to lock " + card.rank + " of " + card.suit);
				if (card.lockHolder == null || event.lockHolder == null)
				{
					if (card.lockHolder == null)
						moveObjectToTop(card);
					card.lockHolder = event.lockHolder;
					broadcast(event);
				}
			}
		}
		else if (object instanceof OwnerEvent)
		{
			OwnerEvent event = (OwnerEvent)object;
			ServerObject ownedObject = uuidObjectMap.get(event.ownedUuid);
			if (ownedObject instanceof ServerCard)
			{
				ServerCard card = (ServerCard)ownedObject;
				System.out.println("[SandboxTabletopServer | INFO] " + event.owner + " trying to keep " + card.rank + " of " + card.suit);
				card.owner = event.owner;
				broadcast(event);
			}
		}
		else if (object instanceof ServerObjectPosition)
		{
			ServerObjectPosition serverObjectPosition = (ServerObjectPosition)object;
			ServerObject serverObject = uuidObjectMap.get(serverObjectPosition.uuid);
			assert serverObject != null;
			serverObject.setPosition(serverObjectPosition.x, serverObjectPosition.y);
			broadcast(object);
		}
		else if (object instanceof FlipCardEvent)
		{
			FlipCardEvent event = (FlipCardEvent)object;
			ServerObject serverObject = uuidObjectMap.get(event.uuid);
			if (serverObject instanceof ServerCard)
			{
				ServerCard card = (ServerCard)serverObject;
				card.isFaceUp = event.isFaceUp;
				moveObjectToTop(card);
			}
			broadcast(object);
		}
	}
	
	private void moveObjectToTop(ServerObject object)
	{
		objects.removeValue(object, false);
		objects.add(object);
	}
}
