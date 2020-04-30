package com.github.cuzitsjonny.flatdb;

import com.github.cuzitsjonny.jbitstream.BitStream;

public class Value
{
    private FlatDBDataType dataType;
    private Integer intData;
    private Float floatData;
    private FlatDBString stringData;
    private Boolean booleanData;
    private FlatDBLong longData;

    public Value(FlatDBDataType dataType, int data)
    {
        this.dataType = dataType;
        this.intData = data;
    }

    public Value(FlatDBDataType dataType, float data)
    {
        this.dataType = dataType;
        this.floatData = data;
    }

    public Value(FlatDBDataType dataType, FlatDBString data)
    {
        this.dataType = dataType;
        this.stringData = data;
    }

    public Value(FlatDBDataType dataType, boolean data)
    {
        this.dataType = dataType;
        this.booleanData = data;
    }

    public Value(FlatDBDataType dataType, FlatDBLong data)
    {
        this.dataType = dataType;
        this.longData = data;
    }

    public FlatDBDataType getDataType()
    {
        return dataType;
    }

    public int getIntData()
    {
        return intData;
    }

    public float getFloatData()
    {
        return floatData;
    }

    public String getStringData()
    {
        return stringData.getValue();
    }

    public boolean getBooleanData()
    {
        return booleanData;
    }

    public long getLongData()
    {
        return longData.getValue();
    }

    public void load(BitStream bitStream)
    {
        if (stringData != null)
        {
            stringData.load(bitStream);
        }
        else if (longData != null)
        {
            longData.load(bitStream);
        }
    }
}
