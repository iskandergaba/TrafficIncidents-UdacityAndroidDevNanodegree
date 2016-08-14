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