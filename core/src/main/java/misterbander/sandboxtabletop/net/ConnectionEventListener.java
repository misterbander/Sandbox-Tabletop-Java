package misterbander.sandboxtabletop.net;

import com.badlogic.gdx.utils.Null;

import java.io.Serializable;

/**
 * Used to listen for connection events.
 */
public interface ConnectionEventListener
{
	/**
	 * Called when a connection with the remote end has been established.
	 * @param connection the connection
	 */
	default void connectionOpened(Connection connection) {}
	
	/**
	 * Called when the connection is closed either due to manual disconnection from either side or from errors.
	 * @param connection the connection
	 */
	default void connectionClosed(Connection connection) {}
	
	/**
	 * Called when an object is received from the remote end.
	 * @param connection the connection
	 * @param object the received object
	 */
	default void objectReceived(Connection connection, Serializable object) {}
	
	/**
	 * Called when an exception occurs. Default implementation is to print the stacktrace and close the connection.
	 * @param connection the connection, if null, that means the exception occured before connection is established
	 * @param e the exception that occurred.
	 */
	default void exceptionOccurred(@Null Connection connection, Exception e)
	{
		e.printStackTrace();
		if (connection != null)
			connection.close();
	}
}
