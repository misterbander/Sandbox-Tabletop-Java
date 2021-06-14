package misterbander.sandboxtabletop.net;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Null;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

import misterbander.sandboxtabletop.MenuScreen;

/**
 * This is the client class that will handle connecting to the server, as well as sending and receiving objects to and
 * from the server.
 */
public class SandboxTabletopClient extends Thread implements ConnectionEventListener
{
	private ConnectionEventListener listener;
	private final Socket socket;
	private final String hostAddress;
	private final int port;
	private @Null Connection connection;
	private volatile boolean isConnectionCancelled;
	private volatile boolean isDisconnectIntentional;
	
	/**
	 * Creates a client that connects to {@code hostAddress} on {@code port}. Creating the client does not immediately
	 * connect to the server, to do that you must call {@code start()}, which will connect to the server on a separate
	 * thread.
	 * @param listener the listener to listen for connection events
	 * @param hostAddress the IP address of the server to connect to
	 * @param port the port number to listen on
	 * @throws IOException if the socket fails to initialize
	 */
	public SandboxTabletopClient(ConnectionEventListener listener, String hostAddress, int port) throws IOException
	{
		this.listener = listener;
		socket = new Socket();
		this.hostAddress = hostAddress;
		this.port = port;
	}
	
	@Override
	public synchronized void start()
	{
		setDaemon(true);
		super.start();
	}
	
	@Override
	public void run()
	{
		try
		{
			Gdx.app.log("SandboxTabletopClient | INFO", "Connecting to " + hostAddress + " at port " + port + "...");
			socket.connect(new InetSocketAddress(hostAddress, port));
			connection = new Connection(this, socket);
			connection.start();
			connectionOpened(connection);
		}
		catch (IOException e)
		{
			if (!isConnectionCancelled) // We are in menu screen and we failed to connect to server
				Gdx.app.postRunnable(() -> ((MenuScreen)listener).connectionFailed(e));
		}
	}
	
	public ConnectionEventListener getConnectionEventListener()
	{
		return listener;
	}
	
	public void setConnectionEventListener(ConnectionEventListener listener)
	{
		this.listener = listener;
	}
	
	public boolean isDisconnectIntentional()
	{
		return isDisconnectIntentional;
	}
	
	public void send(Serializable object)
	{
		if (connection == null)
			throw new IllegalStateException("Connection not yet established");
		connection.send(object);
	}
	
	public void disconnect()
	{
		if (connection == null)
		{
			Gdx.app.log("SandboxTabletopClient | INFO", "Cancelled connection to " + hostAddress);
			isConnectionCancelled = true;
		}
		else
		{
			isDisconnectIntentional = true;
			connection.close();
		}
	}
	
	@Override
	public void connectionOpened(Connection connection)
	{
		Gdx.app.postRunnable(() -> listener.connectionOpened(connection));
	}
	
	@Override
	public void connectionClosed(Connection connection, Exception e)
	{
		Gdx.app.postRunnable(() -> listener.connectionClosed(connection, e));
	}
	
	@Override
	public void objectReceived(Connection connection, Serializable object)
	{
		Gdx.app.postRunnable(() -> listener.objectReceived(connection, object));
	}
}
