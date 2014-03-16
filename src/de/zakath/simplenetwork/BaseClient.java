/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zakath.simplenetwork;

import de.zakath.simplenetwork.eventargs.*;
import de.zakath.simplenetwork.eventlistener.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

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

	public BaseClient(String host, int port, int ID)
	{
		_host = host;
		_port = port;
		_newmsglisteners = new ArrayList<>();
		_connectionlostlistener = new ArrayList<>();
		_id = ID;
		_isserver = false;
	}

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

	public int getID()
	{
		return _id;
	}

	public void setID(int id)
	{
		_id = id;
	}

	public boolean isServer()
	{
		return _isserver;
	}

	public boolean isLocked()
	{
		return _locked;
	}

	public void setLocked(boolean b)
	{
		_locked = b;
	}

	public boolean isConnected()
	{
		return s != null && s.isConnected();
	}

	public boolean isShutdown()
	{
		return s == null;
	}
	
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

	public void stopListenting()
	{
		if (_listenthread != null)
			_listenthread.interrupt();

	}

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

	public Exception getLatestException()
	{
		return _lastexception;
	}

	public boolean suspend()
	{
		return false;
	}

	public boolean resume()
	{
		return false;
	}

	private final List<NewMessageListener> _newmsglisteners;

	public void addNewMessageListener(NewMessageListener listener)
	{
		_newmsglisteners.add(listener);
	}

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

	public void addConnectionLostListener(ConnectionLostListener listener)
	{
		_connectionlostlistener.add(listener);
	}

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
