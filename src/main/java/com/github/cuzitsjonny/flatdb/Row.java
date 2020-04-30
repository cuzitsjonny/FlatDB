package com.github.cuzitsjonny.flatdb;

import java.util.HashMap;
import java.util.Map;

public class Row
{
    private Table table;
    private Map<Column, Value> valuesByColumn;

    public Row(Table table)
    {
        this.table = table;
        this.valuesByColumn = new HashMap<Column, Value>();
    }

    public void setValue(Column column, Value value)
    {
        valuesByColumn.put(column, value);
    }

    public Value getValue(Column column)
    {
        return valuesByColumn.get(column);
    }

    public Value getValue(String columnName)
    {
        Column column = table.getColumn(columnName);

        return getValue(column);
    }

    public Value getValue(int columnIndex)
    {
        Column column = table.getColumn(columnIndex);

        return getValue(column);
    }
}
