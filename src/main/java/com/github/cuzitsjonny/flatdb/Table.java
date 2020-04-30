package com.github.cuzitsjonny.flatdb;

import com.github.cuzitsjonny.jbitstream.BitStream;

import java.util.*;
import java.util.function.Predicate;

public class Table
{
    private FlatDB.CacheStrategy cacheStrategy;
    private int columnHeaderAddress;
    private int rowTopHeaderAddress;
    private FlatDBString name;
    private List<Column> columns;
    private Map<String, Column> columnsByName;
    private List<Row> rows;
    private BitStream bitStream;

    public Table(FlatDB.CacheStrategy cacheStrategy, int columnHeaderAddress, int rowTopHeaderAddress)
    {
        this.cacheStrategy = cacheStrategy;
        this.columnHeaderAddress = columnHeaderAddress;
        this.rowTopHeaderAddress = rowTopHeaderAddress;
        this.columns = new ArrayList<Column>();
        this.columnsByName = new HashMap<String, Column>();
        this.rows = new ArrayList<Row>();
    }

    public String getName()
    {
        return name.getValue();
    }

    public Column getColumn(int index)
    {
        return columns.get(index);
    }

    public Column getColumn(String name)
    {
        return columnsByName.get(name);
    }

    public List<Column> getAllColumns()
    {
        return Collections.unmodifiableList(columns);
    }

    public List<Row> select(Predicate<Row> where)
    {
        List<Row> result = new ArrayList<Row>();

        if (cacheStrategy == FlatDB.CacheStrategy.ON_DEMAND)
        {
            if (rows.isEmpty())
            {
                loadRows();
            }
        }

        for (Row row : rows)
        {
            if (where.test(row))
            {
                result.add(row);
            }
        }

        return result;
    }

    public Row selectFirst(Predicate<Row> where)
    {
        Row result = null;

        if (cacheStrategy == FlatDB.CacheStrategy.ON_DEMAND)
        {
            if (rows.isEmpty())
            {
                loadRows();
            }
        }

        for (int i = 0; i < rows.size() && result == null; i++)
        {
            Row row = rows.get(i);

            if (where.test(row))
            {
                result = row;
            }
        }

        return result;
    }

    public void load(BitStream bitStream)
    {
        this.bitStream = bitStream;

        loadColumns();

        if (cacheStrategy == FlatDB.CacheStrategy.FULL)
        {
            loadRows();
        }
    }

    private void loadColumns()
    {
        bitStream.setReadOffset(columnHeaderAddress * 8);

        int amountOfColumns = bitStream.readIntLE();
        int tableNameAddress = bitStream.readIntLE();
        int columnDataAddress = bitStream.readIntLE();

        name = new FlatDBString(tableNameAddress);
        name.load(bitStream);

        bitStream.setReadOffset(columnDataAddress * 8);

        for (int i = 0; i < amountOfColumns; i++)
        {
            int columnDataTypeOrdinal = bitStream.readIntLE();
            int columnNameAddress = bitStream.readIntLE();

            FlatDBDataType columnDataType = FlatDBDataType.values()[columnDataTypeOrdinal];
            FlatDBString columnName = new FlatDBString(columnNameAddress);
            Column column = new Column(columnName, columnDataType);

            columns.add(column);
        }

        for (Column column : columns)
        {
            column.load(bitStream);
            columnsByName.put(column.getName(), column);
        }
    }

    private void loadRows()
    {
        bitStream.setReadOffset(rowTopHeaderAddress * 8);

        int amountOfRows = bitStream.readIntLE();
        int rowHeaderAddress = bitStream.readIntLE();

        if (rowHeaderAddress != -1)
        {
            int[] rowInfoAddresses = new int[amountOfRows];

            for (int i = 0; i < amountOfRows; i++)
            {
                rowInfoAddresses[i] = bitStream.readIntLE();
            }

            for (int i = 0; i < amountOfRows; i++)
            {
                int rowInfoAddress = rowInfoAddresses[i];

                if (rowInfoAddress != -1)
                {
                    bitStream.setReadOffset(rowInfoAddress * 8);

                    int rowDataHeaderAddress = bitStream.readIntLE();
                    int linkedRowInfoAddress = bitStream.readIntLE();

                    bitStream.setReadOffset(rowDataHeaderAddress * 8);

                    int amountOfValues = bitStream.readIntLE();
                    int rowDataAddress = bitStream.readIntLE();
                    Row row = new Row(this);

                    bitStream.setReadOffset(rowDataAddress * 8);

                    for (int j = 0; j < amountOfValues; j++)
                    {
                        Column column = getColumn(j);
                        boolean isNull = bitStream.readIntLE() == 0;

                        if (isNull)
                        {
                            row.setValue(column, new Value(column.getDataType()));

                            bitStream.setReadOffset(bitStream.getReadOffset() + 32);
                        }
                        else
                        {
                            switch (column.getDataType())
                            {
                                case INT:
                                case UNSIGNED_INT:
                                    row.setValue(column, new Value(column.getDataType(), bitStream.readIntLE()));
                                    break;
                                case FLOAT:
                                    row.setValue(column, new Value(column.getDataType(), bitStream.readFloatLE()));
                                    break;
                                case VARCHAR:
                                case TEXT:
                                    int stringDataAddress = bitStream.readIntLE();

                                    row.setValue(column, new Value(column.getDataType(), new FlatDBString(stringDataAddress)));
                                    break;
                                case BIT:
                                    row.setValue(column, new Value(column.getDataType(), bitStream.readIntLE() != 0));
                                    break;
                                case BIGINT:
                                case UNSIGNED_BIGINT:
                                    int longDataAddress = bitStream.readIntLE();

                                    row.setValue(column, new Value(column.getDataType(), new FlatDBLong(longDataAddress)));
                                    break;
                            }
                        }
                    }

                    for (int j = 0; j < amountOfValues; j++)
                    {
                        Column column = getColumn(j);

                        row.getValue(column).load(bitStream);
                    }

                    rows.add(row);
                }
            }
        }
    }
}
