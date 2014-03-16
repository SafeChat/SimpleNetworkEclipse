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
public class ConnectiontoClientLostEventArgs
{

	private final BaseClient _c;

	public BaseClient getClient()
	{
		return _c;
	}

	public ConnectiontoClientLostEventArgs(BaseClient c)
	{
		_c = c;
	}
}
