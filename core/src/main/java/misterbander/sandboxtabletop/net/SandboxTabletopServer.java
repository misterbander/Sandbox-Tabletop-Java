package misterbander.sandboxtabletop.net;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import misterbander.sandboxtabletop.net.model.Chat;
import misterbander.sandboxtabletop.net.model.User;

/**
 * This is the server that can be run separately.
 */
public class SandboxTabletopServer extends Thread implements ConnectionEventListener
{
	private final ServerSocket serverSocket;
	private final Array<Connection> connections = new Array<>();
	private final ObjectMap<Connection, User> connectionUserMap = new ObjectMap<>();
	
	public static void main(String[] args) throws IOException
	{
		new SandboxTabletopServer(11530).start();
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
	}
	
	@Override
	public void run()
	{
		System.out.println("[SandboxTabletopServer | INFO] Ready to accept connections");
		while (true) // Repeat forever to accept incoming connections
		{
			try
			{
				Socket socket = serverSocket.accept();
				Connection connection = new Connection(this, socket);
				connection.start();
				connectionOpened(connection);
				connections.add(connection);
			}
			catch (Exception e)
			{
				exceptionOccurred(null, e);
			}
		}
	}
	
	public void broadcast(Serializable object)
	{
		for (Connection connection : connections)
			connection.send(object);
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
	public void connectionClosed(Connection connection)
	{
		System.out.println("[SandboxTabletopServer | INFO] " + connection.remoteAddress + " disconnected");
		connections.removeValue(connection, true);
		User user = connectionUserMap.remove(connection);
		if (user != null)
			System.out.println("[SandboxTabletopServer | INFO] " + user + " left the game");
	}
	
	@Override
	public void objectReceived(Connection connection, Serializable object)
	{
		if (object instanceof User)
		{
			User user = (User)object;
			System.out.println("[SandboxTabletopServer | INFO] " + user + " joined the game");
			connectionUserMap.put(connection, user);
			
			broadcast(new Chat(user, user.username + " joined the game", true));
		}
		else if (object instanceof Chat)
		{
			Chat chat = (Chat)object;
			System.out.println(chat.message);
			broadcast(chat);
		}
	}
}
