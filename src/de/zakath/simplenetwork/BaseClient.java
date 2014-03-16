/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zakath.simplenetwork;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.*;

import de.zakath.simplenetwork.eventargs.*;
import de.zakath.simplenetwork.eventlistener.*;

/**
 * 
 * @author cw
 */
public class BaseClient
{

	private final String _host;
	private final int _port;
	private final boolean _isserver;

	private int _id;
	private boolean _locked = true;

	private Socket s;
	private ObjectOutputStream outstream;
	private ObjectInputStream instream;

	private Thread _listenthread;

	private Exception _lastexception;

	/**
	 * Creates a new BaseClient Socket wrapper
	 * 
	 * @param host
	 *            The host address the client should connect to
	 * @param port
	 *            the port the client should connect to
	 * @param ID
	 *            The client id the client sends to the server with each message
	 */
	public BaseClient(String host, int port, int ID)
	{
		_host = host;
		_port = port;
		_newmsglisteners = new ArrayList<>();
		_connectionlostlistener = new ArrayList<>();
		_id = ID;
		_isserver = false;
	}

	/**
	 * Creates a new BaseClient Socket wrapper based on an existing socket
	 * 
	 * @param s
	 *            The already connected socket
	 * @param ID
	 *            The id send to the client/server with each message
	 * @param isServer
	 *            An boolean indicating if this instance runs on a server or not
	 */
	public BaseClient(Socket s, int ID, boolean isServer)
	{
		this.s = s;
		_id = ID;
		_host = "";
		_port = s.getPort();
		_isserver = isServer;
		_newmsglisteners = new ArrayList<>();
		_connectionlostlistener = new ArrayList<>();
		try
		{
			outstream = new ObjectOutputStream(s.getOutputStream());
			instream = new ObjectInputStream(s.getInputStream());
		} catch (IOException ex)
		{
			_lastexception = ex;
			Logger.getLogger(BaseClient.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	/**
	 * Returns the assigend ID for this BaseClient instance
	 * 
	 * @return The assigned ID
	 */
	public int getID()
	{
		return _id;
	}

	/**
	 * Assigns an ID for this BaseClient instance
	 * 
	 * @param id
	 *            The ID that should be assigned
	 */
	public void setID(int id)
	{
		_id = id;
	}

	/**
	 * Returns if running on a server or not
	 * 
	 * @return The boolean representig the status
	 */
	public boolean isServer()
	{
		return _isserver;
	}

	/**
	 * Returns if this instance has succesfull completed authication with the
	 * Server
	 * 
	 * @return The boolean representing the status
	 */
	public boolean isLocked()
	{
		return _locked;
	}

	/**
	 * Sets the loocke status with indicates if the authication has succesfull
	 * be completed
	 * 
	 * @param b
	 *            The booleanvalue wich should be set
	 */
	public void setLocked(boolean b)
	{
		_locked = b;
	}

	/**
	 * Return if the client is connected or not
	 * 
	 * @return The boolean representing the status
	 */
	public boolean isConnected()
	{
		return s != null && s.isConnected();
	}

	/**
	 * Returns if the client is shutdowned or not
	 * 
	 * @return The boolean representing the status
	 */
	public boolean isShutdown()
	{
		return s == null;
	}

	/**
	 * Connect the client to the , in the constructor, specifified host and port
	 * 
	 * @return Returns whether the connection attempt was succesfull or not
	 */
	public boolean connect()
	{
		try
		{
			s = new Socket(_host, _port);
			outstream = new ObjectOutputStream(s.getOutputStream());
			instream = new ObjectInputStream(s.getInputStream());
			startListening();

			// Message m = new Message(_id, -1, Message.MessageType.Connected);
			// sendMessage(m);
			return true;

		} catch (IOException ex)
		{
			_lastexception = ex;
			Logger.getLogger(BaseClient.class.getName()).log(Level.INFO, null,
					ex);
			return false;
		}
	}

	/**
	 * Start listening to the connection
	 */
	public void startListening()
	{
		_listenthread = new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				while (true)
				{

					try
					{
						Message m = (Message) instream.readObject();
						raiseNewMessageEvent(m);
					} catch (IOException | ClassNotFoundException ex)
					{
						_lastexception = ex;
						raiseConnectionLostEvent();
						shutdown();
						return;
					}

				}
			}
		});
		_listenthread.start();
	}

	/**
	 * Stop listening to the connection
	 */
	public void stopListenting()
	{
		if (_listenthread != null)
			_listenthread.interrupt();

	}

	/**
	 * Sends a message to the remote client
	 * 
	 * @param m
	 *            The message which should be send
	 * @return A boolean indicating if the message transfer was succesfull or
	 *         not
	 */
	public boolean sendMessage(Message m)
	{
		try
		{
			outstream.writeObject(m);
			return true;
		} catch (IOException ex)
		{
			_lastexception = ex;
			Logger.getLogger(BaseClient.class.getName()).log(Level.SEVERE,
					null, ex);
			raiseConnectionLostEvent();
			shutdown();
			return false;
		}
	}

	/**
	 * Shutdown the connection and the client
	 * 
	 * @return A boolean indicating if shutdown was succesfull or not
	 */
	public boolean shutdown()
	{
		stopListenting();
		// Message m = new Message(_id, -1, Message.MessageType.Quit);
		// sendMessage(m);
		try
		{
			if (s != null)
				s.close();
			return true;
		} catch (IOException ex)
		{
			_lastexception = ex;
			return false;
		}
	}

	/**
	 * Provieds access to the latest exception thrown by the client
	 * 
	 * @return The latest exception
	 */
	public Exception getLatestException()
	{
		return _lastexception;
	}

	/**
	 * NIY
	 * 
	 * @return
	 */
	public boolean suspend()
	{
		return false;
	}

	/**
	 * NIY
	 * 
	 * @return
	 */
	public boolean resume()
	{
		return false;
	}

	private final List<NewMessageListener> _newmsglisteners;

	/**
	 * Adds a new MessageListener to the MessageListenerlist
	 * 
	 * @param listener
	 *            The listener that should be added
	 */
	public void addNewMessageListener(NewMessageListener listener)
	{
		_newmsglisteners.add(listener);
	}

	/**
	 * Removes a MessageListener from the MessageListenerlist
	 * 
	 * @param listener
	 *            The MessageListener that should be removed
	 */
	public void removeNewMessageListener(NewMessageListener listener)
	{
		_newmsglisteners.remove(listener);
	}

	protected void raiseNewMessageEvent(Message m)
	{
		for (NewMessageListener l : _newmsglisteners)
		{
			l.OnNewMessage(this, new NewMessageEventArgs(m));
		}
	}

	private final List<ConnectionLostListener> _connectionlostlistener;

	/**
	 * Adds a new ConnectionLostListener to the ConnectionLostListenerlist
	 * 
	 * @param listener
	 *            The ConnectionLostListener that should be add
	 */
	public void addConnectionLostListener(ConnectionLostListener listener)
	{
		_connectionlostlistener.add(listener);
	}

	/**
	 * Removes a ConnectionLostListener from the ConnectionLostListenerlist
	 * 
	 * @param listener
	 *            The ConnectionLostListener that should be removed
	 */
	public void removeConnectionLostListener(ConnectionLostListener listener)
	{
		_connectionlostlistener.remove(listener);
	}

	protected void raiseConnectionLostEvent()
	{
		List<ConnectionLostListener> _temp = _connectionlostlistener;
		for (ConnectionLostListener l : _temp)
		{
			l.OnConnectionLost(this, new ConnectionLostEventArgs());
		}
	}

}
