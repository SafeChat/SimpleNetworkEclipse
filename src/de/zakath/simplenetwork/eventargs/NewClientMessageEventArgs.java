/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zakath.simplenetwork.eventargs;

import de.zakath.simplenetwork.*;

/**
 *
 * @author cw
 */
public class NewClientMessageEventArgs
{

    private Message _m;
    private BaseClient _c;

    public Message getMessage()
    {
        return _m;
    }

    public BaseClient getClient()
    {
        return _c;
    }

    public NewClientMessageEventArgs(BaseClient c, Message m)
    {
        _m = m;
        _c = c;
    }

}
