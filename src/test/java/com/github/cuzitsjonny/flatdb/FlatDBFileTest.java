package com.github.cuzitsjonny.flatdb;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FlatDBFileTest
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
        String path = "E:\\luclient\\LCDR Unpacked\\res\\cdclient.fdb";
        FlatDB flatDB = new FlatDB(FlatDB.CacheStrategy.ON_DEMAND);

        flatDB.load(new File(path));

        System.out.println("Start loading");
        long startTime = System.currentTimeMillis();

        long endTime = System.currentTimeMillis();
        System.out.println("End loading");

        System.out.println("MS passed: " + (endTime - startTime));

        System.out.println("Start query");
        startTime = System.currentTimeMillis();

        List<Row> rows = flatDB.getTable("Objects").select(row -> { return row.getValue("id").getIntData() == 1; });

        System.out.println("Rows: " + rows.size());

        endTime = System.currentTimeMillis();
        System.out.println("End query");

        System.out.println("MS passed: " + (endTime - startTime));

        System.out.println("Start query");
        startTime = System.currentTimeMillis();

        rows = flatDB.getTable("Objects").select(row -> { return row.getValue("id").getIntData() == 1727; });

        System.out.println("Rows: " + rows.get(0).getValue("name").getStringData());

        endTime = System.currentTimeMillis();
        System.out.println("End query");

        System.out.println("MS passed: " + (endTime - startTime));
    }
}
