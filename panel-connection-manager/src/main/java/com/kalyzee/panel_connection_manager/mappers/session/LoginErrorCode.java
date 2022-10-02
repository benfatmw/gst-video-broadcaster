package com.kalyzee.panel_connection_manager.mappers.session;


import com.kalyzee.panel_connection_manager.exceptions.session.InvalidLoginErrorCodeException;

public enum LoginErrorCode {

    UNAUTHORIZED(-1),
    INTERNAL_ERROR(-2);

    private int value;

    private LoginErrorCode(int value) {
        this.value = value;
    }

    public int getInt() {
        return value;
    }

    public static LoginErrorCode value(int value) {
        for (LoginErrorCode e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        throw new InvalidLoginErrorCodeException("Input error code: " + value + " is not supported.");
    }

}
