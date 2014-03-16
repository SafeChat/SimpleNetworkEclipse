/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.zakath.simplenetwork.eventlistener;

import de.zakath.simplenetwork.eventargs.ClientConnectedEventArgs;

/**
 * 
 * @author cw
 */
public interface ClientConnectedListener
{
	void OnClientConnected(Object sender, ClientConnectedEventArgs e);
}
