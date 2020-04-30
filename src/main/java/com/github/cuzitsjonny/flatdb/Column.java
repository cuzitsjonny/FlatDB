package com.github.cuzitsjonny.flatdb;

import com.github.cuzitsjonny.jbitstream.BitStream;

public class Column
{
    private FlatDBString name;
    private FlatDBDataType dataType;

    public Column(FlatDBString name, FlatDBDataType dataType)
    {
        this.name = name;
        this.dataType = dataType;
    }

    public String getName()
    {
        return name.getValue();
    }

    public FlatDBDataType getDataType()
    {
        return dataType;
    }

    public void load(BitStream bitStream)
    {
        name.load(bitStream);
    }
}
