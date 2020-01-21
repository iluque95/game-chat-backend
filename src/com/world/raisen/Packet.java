package com.world.raisen;

import static java.lang.System.arraycopy;

public class Packet
{
    private byte[] packet;
    int idx;

    Packet(byte[] buffer, int len)
    {
        this.idx = 0;
        this.packet = new byte[len];

        arraycopy(buffer, 0, packet, 0, len);
    }

    public byte[] read()
    {
        return packet;
    }

    public int size()
    {
        return packet.length;
    }


    public short readShort()
    {
        int i = idx;

        idx += Short.BYTES;

        return (short)((packet[i+1] << 8) | packet[i]);
    }

    public int readInt()
    {
        int i = idx;

        idx += Integer.BYTES;

        return (packet[i+3] << 24)&0xff000000|
                (packet[i+2] << 16)&0x00ff0000|
                (packet[i+1] << 8)&0x0000ff00|
                (packet[i] << 0)&0x000000ff;
    }

    public void skip(int n)
    {
        idx += n;
    }

    public void begin()
    {
        idx = 0;
    }
}
