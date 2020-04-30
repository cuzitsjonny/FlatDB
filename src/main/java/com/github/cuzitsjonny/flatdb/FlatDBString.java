package com.github.cuzitsjonny.flatdb;

import com.github.cuzitsjonny.jbitstream.BitStream;

class FlatDBString
{
    private int address;
    private String value;

    public FlatDBString(int address)
    {
        this.address = address;
    }

    public String getValue()
    {
        return value;
    }

    public void load(BitStream bitStream)
    {
        bitStream.setReadOffset(address * 8);

        value = "";

        byte character = bitStream.readByte();

        while (character != 0)
        {
            value += (char)character;

            character = bitStream.readByte();
        }
    }
}
