/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zakath.simplenetwork.misc;

import java.io.*;

/**
 *
 * @author cw
 */
public class DualByteKey implements Serializable
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -8218128605332789291L;
	private final byte[] _private;
    private final byte[] _public;

    public byte[] getPublicKey()
    {
        return _public;
    }

    public byte[] getPrivateKey()
    {
        return _private;
    }

    public DualByteKey(byte[] publickey, byte[] privatekey)
    {
        _private = privatekey;
        _public = publickey;
    }
}
