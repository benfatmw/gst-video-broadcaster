package com.kalyzee.kontroller_services_api.dtos.system.update;

import android.util.SparseArray;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.concurrent.atomic.AtomicInteger;

public class UpdateSessionState {

    /** The session is currently IDLE state. */
    public static final int IDLE = 0;
    /** The session is currently downloading binary. */
    public static final int DOWNLOADING = 1;
    /** The session is currently waiting for install/apply the binary. */
    public static final int WAITING_FOR_INSTALL = 2;
    /** The session is done and all its effects are now visible. */
    public static final int DONE = 3;
    /** There was an error during the session. */
    public static final int ERROR = 4;
    private static final SparseArray<String> STATE_MAP = new SparseArray<>();

    static {
        STATE_MAP.put(0, "IDLE");
        STATE_MAP.put(1, "DOWNLOADING");
        STATE_MAP.put(2, "WAITING_FOR_INSTALL");
        STATE_MAP.put(3, "DONE");
        STATE_MAP.put(4, "ERROR");
    }

    /**
     * Allowed state transitions. It's a map: key is a state, value is a set of states that
     * are allowed to transition to from key.
     */
    private static final ImmutableMap<Integer, ImmutableSet<Integer>> TRANSITIONS =
            ImmutableMap.<Integer, ImmutableSet<Integer>>builder()
                    .put(IDLE, ImmutableSet.of(IDLE, ERROR, DOWNLOADING))
                    .put(ERROR, ImmutableSet.of(IDLE))
                    .put(DOWNLOADING, ImmutableSet.of(
                            IDLE, ERROR, WAITING_FOR_INSTALL))
                    .put(WAITING_FOR_INSTALL, ImmutableSet.of(
                            IDLE, ERROR))
                    .put(DONE, ImmutableSet.of(IDLE))
                    .build();

    private AtomicInteger state;

    public UpdateSessionState(int state) {
        this.state = new AtomicInteger(state);
    }

    /**
     * Returns updater state.
     */
    public int get() {
        return state.get();
    }

    /**
     * Sets the downloader state.
     *
     * @throws InvalidTransitionException if transition is not allowed.
     */
    public void set(int newState) throws InvalidTransitionException {
        int oldState = state.get();
        if (!TRANSITIONS.get(oldState).contains(newState)) {
            throw new InvalidTransitionException("Can't transition from " + oldState + " to " + newState);
        }
        state.set(newState);
    }

    /**
     * Converts status code to status name.
     */
    public static String getStateText(int state) {
        return STATE_MAP.get(state);
    }

    /**
     * Defines invalid state transition exception.
     */
    public static class InvalidTransitionException extends Exception {
        public InvalidTransitionException(String msg) {
            super(msg);
        }
    }
}
