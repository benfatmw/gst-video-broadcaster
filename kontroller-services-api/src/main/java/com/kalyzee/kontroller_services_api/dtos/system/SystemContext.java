package com.kalyzee.kontroller_services_api.dtos.system;

import android.os.SystemClock;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Method;
import java.util.TimeZone;

public class SystemContext {

    static final String TIMEZONE_PROPERTY = "persist.sys.timezone";
    static final String SYSTEM_PROPERTIES_PACKAGE_NAME = "android.os.SystemProperties";

    @JsonProperty("timezone_raw_offset")
    private int timezoneRawOffset = TimeZone.getDefault().getRawOffset();
    @JsonProperty("time_zone")
    private String timezone = getSystemProperty(TIMEZONE_PROPERTY);
    @JsonProperty("current_time_in_ms")
    private long currentTimeInMs = System.currentTimeMillis();
    @JsonProperty("elapsed_realtime")
    private long elapsedRealTime = SystemClock.elapsedRealtime();

    public static String getSystemProperty(String key) {
        String pValue = null;
        try {
            Class<?> c = Class.forName(SYSTEM_PROPERTIES_PACKAGE_NAME);
            Method m = c.getMethod("get", String.class);
            pValue = m.invoke(null, key).toString();
        } catch (Exception e) {
        }
        return pValue;
    }
}
