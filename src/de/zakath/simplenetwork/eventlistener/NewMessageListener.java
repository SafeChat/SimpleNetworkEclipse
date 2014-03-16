/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zakath.simplenetwork.eventlistener;

import de.zakath.simplenetwork.eventargs.*;

/**
 *
 * @author cw
 */
public interface NewMessageListener
{

    public void OnNewMessage(Object sender, NewMessageEventArgs e);
}
