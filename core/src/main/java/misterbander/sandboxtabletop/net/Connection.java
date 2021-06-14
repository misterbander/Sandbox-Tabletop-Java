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
	private volatile ConnectionEventListener listener;
	
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
		try
		{
			while (true) // Infinite loop to handle receiving objects from remote end when they arrive
			{
				Serializable incomingObject = (Serializable)incoming.readObject();
				listener.objectReceived(this, incomingObject);
			}
		}
		catch (IOException | ClassNotFoundException e) // An error occured, or the connection got disconnected
		{
			e.printStackTrace();
			listener.connectionClosed(this, e);
		}
	}
	
	/**
	 * Sends an object to the remote end.
	 * @param object the object to be sent
	 */
	public synchronized void send(Serializable object)
	{
		try
		{
			outgoing.reset();
			outgoing.writeObject(object);
			outgoing.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setConnectionEventListener(ConnectionEventListener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * Closes the connection.
	 */
	public void close()
	{
		try
		{
			outgoing.close();
			incoming.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
