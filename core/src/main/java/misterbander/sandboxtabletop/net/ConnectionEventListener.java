package misterbander.sandboxtabletop.net;

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
	 * @param e the exception that caused the connection to close
	 */
	default void connectionClosed(Connection connection, Exception e) {}
	
	/**
	 * Called when an object is received from the remote end.
	 * @param connection the connection
	 * @param object the received object
	 */
	default void objectReceived(Connection connection, Serializable object) {}
}
