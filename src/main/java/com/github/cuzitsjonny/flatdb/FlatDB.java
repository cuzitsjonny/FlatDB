package com.github.cuzitsjonny.flatdb;

import com.github.cuzitsjonny.jbitstream.BitStream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class FlatDB
{
    private CacheStrategy cacheStrategy;
    private List<Table> tables;
    private Map<String, Table> tablesByName;

    public enum CacheStrategy
    {
        FULL,
        ON_DEMAND
    }

    public FlatDB(CacheStrategy cacheStrategy)
    {
        this.cacheStrategy = cacheStrategy;
        this.tables = new ArrayList<Table>();
        this.tablesByName = new HashMap<String, Table>();
    }

    public CacheStrategy getCacheStrategy()
    {
        return cacheStrategy;
    }

    public Table getTable(int index)
    {
        return tables.get(index);
    }

    public Table getTable(String name)
    {
        return tablesByName.get(name);
    }

    public List<Table> getAllTables()
    {
        return Collections.unmodifiableList(tables);
    }

    public void load(File file) throws IOException
    {
        if (!tables.isEmpty())
        {
            tables.clear();
            tablesByName.clear();
        }

        byte[] data = Files.readAllBytes(file.toPath());
        BitStream bitStream = new BitStream(data, false);

        int amountOfTables = bitStream.readIntLE();
        int tableHeaderAddress = bitStream.readIntLE();

        bitStream.setReadOffset(tableHeaderAddress * 8);

        for (int i = 0; i < amountOfTables; i++)
        {
            int columnHeaderAddress = bitStream.readIntLE();
            int rowTopHeaderAddress = bitStream.readIntLE();

            Table table = new Table(cacheStrategy, columnHeaderAddress, rowTopHeaderAddress);

            tables.add(table);
        }

        for (Table table : tables)
        {
            table.load(bitStream);
            tablesByName.put(table.getName(), table);
        }
    }
}
