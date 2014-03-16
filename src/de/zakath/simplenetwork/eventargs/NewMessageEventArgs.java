/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zakath.simplenetwork.eventargs;

import de.zakath.simplenetwork.Message;

/**
 * 
 * @author cw
 */
public class NewMessageEventArgs
{

	private final Message _m;

	public Message getMessage()
	{
		return _m;
	}

	public NewMessageEventArgs(Message m)
	{
		_m = m;
	}

}
