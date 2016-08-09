package com.gaba.alex.trafficincidents.Data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public class IncidentsColumns {
    @DataType(DataType.Type.TEXT) @PrimaryKey
    public static final String _ID = "_id";
    @DataType(DataType.Type.REAL) @NotNull
    public static final String LAT = "latitude";
    @DataType(DataType.Type.REAL) @NotNull
    public static final String LNG = "longitude";
    @DataType(DataType.Type.INTEGER) @NotNull
    public static final String TYPE = "type";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String DESCRIPTION = "description";
    @DataType(DataType.Type.INTEGER)
    public static final String SEVERITY = "severity";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String ROAD_CLOSED = "road_closed";
    @DataType(DataType.Type.TEXT)
    public static final String START_DATE= "start_date";
    @DataType(DataType.Type.TEXT)
    public static final String END_DATE = "end_date";
}