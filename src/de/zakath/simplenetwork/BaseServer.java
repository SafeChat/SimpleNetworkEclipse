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
public class BaseServer
{

    private final int _port;

    private ServerSocket _server;
    private Thread _listentthread;

    private final List<BaseClient> _clients;

    public BaseServer(int port)
    {
        _port = port;
        _clientconnectedlistener = new ArrayList<>();
        _newmessagelistener = new ArrayList<>();
        _connectionloslistener = new ArrayList<>();
        _clients = new ArrayList<>();
    }

    public boolean listen(boolean async)
    {
        try
        {
            _server = new ServerSocket(_port);
            System.out.println("Server is running on: " + Integer.toString(_port));
            Runnable r = new Runnable()
            {

                @Override
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            Socket s = _server.accept();
                            BaseClient bc = new BaseClient(s, -1, true);
                            _clients.add(bc);
                            raiseClientConnected(bc);
                            bc.startListening();
                            bc.addNewMessageListener(new NewMessageListener()
                            {

                                @Override
                                public void OnNewMessage(Object sender, NewMessageEventArgs e)
                                {
                                    handleClientMessages(sender, e);
                                }
                            });

                            bc.addConnectionLostListener(new ConnectionLostListener()
                            {

                                @Override
                                public void OnConnectionLost(Object sender, ConnectionLostEventArgs e)
                                {
                                    handleConnectionLost(sender, e);
                                }
                            });

                        } catch (IOException ex)
                        {
                            Logger.getLogger(BaseServer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
            };
            if (async)
            {
                _listentthread = new Thread(r);
                _listentthread.start();
            } else
            {
                r.run();
            }

            return true;
        } catch (IOException ex)
        {
            Logger.getLogger(BaseServer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    protected void handleClientMessages(Object sender, NewMessageEventArgs e)
    {
        raiseNewClientMessage((BaseClient) sender, e.getMessage());
    }

    protected void handleConnectionLost(Object sender, ConnectionLostEventArgs e)
    {
        raiseConnectionLost(new ConnectiontoClientLostEventArgs((BaseClient) sender));
        _clients.remove((BaseClient) sender);
    }

    public void sendToAllClients(Message m)
    {
        for (BaseClient c : _clients)
        {
            c.sendMessage(m);
        }
    }

    public boolean sendToClient(int ID, Message m)
    {
        BaseClient cl = null;
        for (BaseClient c : _clients)
        {
            if (c.getID() == ID)
            {
                cl = c;
                break;
            }
        }
        if (cl != null)
        {
            sendToClient(cl, m);
            return true;
        }
        return false;
    }

    public void sendToClient(BaseClient c, Message m)
    {
        c.sendMessage(m);
    }

    private final List<ClientConnectedListener> _clientconnectedlistener;

    public void addClientConnectedListener(ClientConnectedListener listener)
    {
        _clientconnectedlistener.add(listener);
    }

    public void removeClientConnectedListener(ClientConnectedListener listener)
    {
        _clientconnectedlistener.remove(listener);
    }

    protected void raiseClientConnected(BaseClient bc)
    {
        for (ClientConnectedListener l : _clientconnectedlistener)
        {
            l.OnClientConnected(this, new ClientConnectedEventArgs(bc));
        }
    }

    private final List<NewClientMessageListener> _newmessagelistener;

    public void addNewClientMessageListener(NewClientMessageListener listener)
    {
        _newmessagelistener.add(listener);
    }

    public void removeNewClientMessageListener(NewClientMessageListener listener)
    {
        _newmessagelistener.remove(listener);
    }

    protected void raiseNewClientMessage(BaseClient c, Message m)
    {
        for (NewClientMessageListener l : _newmessagelistener)
        {
            l.OnNewClientMessage(this, new NewClientMessageEventArgs(c, m));
        }
    }

    private final List<ConnectiontoClientLostListener> _connectionloslistener;

    public void addConnectiontoClientLostListener(ConnectiontoClientLostListener listener)
    {
        _connectionloslistener.add(listener);
    }

    public void removeConnectionLostListener(ConnectiontoClientLostListener listener)
    {
        _connectionloslistener.remove(listener);
    }

    protected void raiseConnectionLost(ConnectiontoClientLostEventArgs e)
    {
        for (ConnectiontoClientLostListener l : _connectionloslistener)
        {
            l.OnConnectiontoClientLost(this, e);
        }
    }
}
