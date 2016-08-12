package com.gaba.alex.trafficincidents.Data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public class SettingsColumns {
    @DataType(DataType.Type.REAL) @NotNull @PrimaryKey
    public static final String LAT = "latitude";
    @DataType(DataType.Type.REAL) @NotNull
    public static final String LNG = "longitude";
    @DataType(DataType.Type.REAL)
    public static final String RANGE = "range";
    @DataType(DataType.Type.INTEGER)
    public static final String AUTO_REFRESH = "auto_refresh";
    @DataType(DataType.Type.INTEGER)
    public static final String SEVERITY = "severity";
}