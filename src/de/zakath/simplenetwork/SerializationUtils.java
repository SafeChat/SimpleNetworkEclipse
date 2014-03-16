/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zakath.simplenetwork;

import java.io.*;

/**
 *
 * @author cw
 */
public class SerializationUtils
{

    public static byte[] serialize(Object obj)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        serialize(obj, bos);
        return bos.toByteArray();
    }

    public static <T> T deserialize(byte[] input)
    {
        return deserialize(new ByteArrayInputStream(input));
    }

    @SuppressWarnings("unchecked")
	public static <T> T deserialize(InputStream instream)
    {
        try
        {
            ObjectInputStream ois = new ObjectInputStream(instream);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException ex)
        {
            return null;
        }
    }

    public static boolean serialize(Object obj, OutputStream outstream)
    {
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(outstream);
            oos.writeObject(obj);
            return true;
        } catch (IOException ex)
        {
            return false;
        }
    }

}
