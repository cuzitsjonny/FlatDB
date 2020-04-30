package com.github.cuzitsjonny.flatdb;

import com.github.cuzitsjonny.jbitstream.BitStream;

class FlatDBLong
{
    private int address;
    private long value;

    public FlatDBLong(int address)
    {
        this.address = address;
    }

    public long getValue()
    {
        return value;
    }

    public void load(BitStream bitStream)
    {
        bitStream.setReadOffset(address * 8);

        value = bitStream.readLongLE();
    }
}
