/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zakath.simplenetwork;

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 *
 * @author cw
 */
public class Message implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4051020163695518817L;
	private int sender;
    private int target;
    private MessageType type;

    private final HashMap<String, String> _headerfields;
    private byte[] _payload;
    private boolean _haspayload;

    private Date _date;

    public Message(int senderID, int targetID, MessageType type)
    {
        this(senderID, targetID, type, null);
    }

    public Message(int senderID, int targetID, MessageType type, byte[] payload)
    {
        _headerfields = new HashMap<>();
        sender = senderID;
        target = targetID;
        this.type = type;
        setPayload(payload);
        _date = new Date();
    }

    public void setField(String name, String value)
    {
        _headerfields.put(name.toLowerCase(), value);
    }

    public String getField(String name)
    {
        return _headerfields.containsKey(name.toLowerCase()) ? _headerfields.get(name.toLowerCase()) : null;
    }

    public boolean isFieldSet(String name)
    {
        return _headerfields.containsKey(name.toLowerCase());
    }

    public void setPayload(byte[] payload)
    {
        _payload = payload;
        _haspayload = payload != null && payload.length > 0;
    }

    public byte[] getPayload()
    {
        return _payload;
    }

    public boolean hasPlayload()
    {
        return _haspayload;
    }

    public int getSenderID()
    {
        return sender;
    }

    public void setSenderID(int id)
    {
        sender = id;
    }

    public int getTargetID()
    {
        return target;
    }

    public MessageType getType()
    {
        return type;
    }

    public Date getDate()
    {
        return _date;
    }

    public byte[] toByteArray()
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            return bos.toByteArray();
        } catch (IOException ex)
        {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Message fromByteArray(byte[] b)
    {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b));
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException ex)
        {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public enum MessageType implements Serializable
    {

        Quit,
        Authinit,
        Auth,
        Authack,
        Message,
        GroupMessage,
        KeyRequest,
        KeyResponse,
        MessagePoll,
        NewAccountRequest,
        NewAccountResponse,
        Error;
    }
}
