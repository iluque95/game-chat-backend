package com.world.raisen;

import java.nio.*;

import static java.lang.System.arraycopy;

public  class Packet
{
    ByteBuffer packet;

    Packet(byte[] buffer, int len)
    {
        packet = ByteBuffer.allocate(len);

        packet.order(ByteOrder.LITTLE_ENDIAN);

        packet.put(buffer, 0, len);

        packet.position(0);
    }

    public byte[] read()
    {
        return packet.array();
    }

    public int size()
    {
        return packet.limit();
    }


    public short readShort()
    {
        /*
        int i = idx;

        idx += Short.BYTES;

        return (short)((packet[i+1] << 8) | packet[i]);

         */

        return packet.getShort();
    }

    public int readInt()
    {
       /* int i = idx;

        idx += Integer.BYTES;

        return (packet[i+3] << 24)&0xff000000|
                (packet[i+2] << 16)&0x00ff0000|
                (packet[i+1] << 8)&0x0000ff00|
                (packet[i] << 0)&0x000000ff;

        */

       return packet.getInt();
    }

    public void skip(int n)
    {
        packet.position(packet.position()+n);
    }

    public void begin()
    {
        packet.position(0);
    }
}
