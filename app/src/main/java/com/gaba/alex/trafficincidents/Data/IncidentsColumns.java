/*
Copyright 2016 Iskander Gaba

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.gaba.alex.trafficincidents.Data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

public class IncidentsColumns {
    @DataType(DataType.Type.TEXT) @PrimaryKey
    public static final String _ID = "_id";
    @DataType(DataType.Type.REAL) @NotNull
    public static final String LAT = "latitude";
    @DataType(DataType.Type.REAL) @NotNull
    public static final String LNG = "longitude";
    @DataType(DataType.Type.REAL) @NotNull @DefaultValue("0.0")
    public static final String TO_LAT = "to_latitude";
    @DataType(DataType.Type.REAL) @NotNull @DefaultValue("0.0")
    public static final String TO_LNG = "to_longitude";
    @DataType(DataType.Type.INTEGER) @NotNull
    public static final String TYPE = "type";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String DESCRIPTION = "description";
    @DataType(DataType.Type.INTEGER) @NotNull
    public static final String SEVERITY = "severity";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String ROAD_CLOSED = "road_closed";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String START_DATE= "start_date";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String END_DATE = "end_date";
}