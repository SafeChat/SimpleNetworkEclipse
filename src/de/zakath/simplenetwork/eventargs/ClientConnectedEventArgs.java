/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zakath.simplenetwork.eventargs;

import de.zakath.simplenetwork.BaseClient;

/**
 * 
 * @author cw
 */
public class ClientConnectedEventArgs
{

	private BaseClient _c;

	public BaseClient getClient()
	{
		return _c;
	}

	public ClientConnectedEventArgs(BaseClient c)
	{
		_c = c;
	}
}
