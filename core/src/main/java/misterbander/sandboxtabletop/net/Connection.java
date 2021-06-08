package misterbander.sandboxtabletop.net;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This acts as the application level gateway for the connection between the local machine and the remote machine.
 * The client and server each have their own {@code Connection} objects and all incoming objects will be received through
 * this {@code Connection}.
 */
public class Connection extends Thread
{
	public final InetAddress localAddress;
	public final InetAddress remoteAddress;
	private final ObjectOutputStream outgoing;
	private final ObjectInputStream incoming;
	private final ConnectionEventListener listener;
	
	private volatile boolean isConnected = true;
	
	public Connection(ConnectionEventListener listener, Socket socket) throws IOException
	{
		localAddress = socket.getLocalAddress();
		remoteAddress = socket.getInetAddress();
		outgoing = new ObjectOutputStream(socket.getOutputStream());
		incoming = new ObjectInputStream(socket.getInputStream());
		this.listener = listener;
	}
	
	@Override
	public void run()
	{
		while (isConnected) // Infinite loop to handle receiving objects from remote end when they arrive
		{
			try
			{
				Serializable incomingObject = (Serializable)incoming.readObject();
				listener.objectReceived(this, incomingObject);
			}
			catch (Exception e)
			{
				listener.exceptionOccurred(this, e);
			}
		}
	}
	
	/**
	 * Sends an object to the remote end.
	 * @param object the object to be sent
	 */
	public void send(Serializable object)
	{
		try
		{
			outgoing.reset();
			outgoing.writeObject(object);
			outgoing.flush();
		}
		catch (Exception e)
		{
			listener.exceptionOccurred(this, e);
		}
	}
	
	public boolean isConnected()
	{
		return isConnected;
	}
	
	/**
	 * Closes the connection.
	 */
	public void close()
	{
		listener.connectionClosed(this);
		try
		{
			isConnected = false;
			outgoing.close();
			incoming.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
