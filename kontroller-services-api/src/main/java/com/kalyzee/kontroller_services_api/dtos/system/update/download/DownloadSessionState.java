package com.kalyzee.kontroller_services_api.dtos.system.update.download;

import android.util.SparseArray;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.concurrent.atomic.AtomicInteger;

public class DownloadSessionState {

    public static final int IDLE = 0;
    public static final int ERROR = 1;
    public static final int RUNNING = 2;
    public static final int FINISHED = 3;
    private static final SparseArray<String> STATE_MAP = new SparseArray<>();

    static {
        STATE_MAP.put(0, "IDLE");
        STATE_MAP.put(1, "ERROR");
        STATE_MAP.put(2, "RUNNING");
        STATE_MAP.put(4, "FINISHED");
    }

    /**
     * Allowed state transitions. It's a map: key is a state, value is a set of states that
     * are allowed to transition to from key.
     */
    private static final ImmutableMap<Integer, ImmutableSet<Integer>> TRANSITIONS =
            ImmutableMap.<Integer, ImmutableSet<Integer>>builder()
                    .put(IDLE, ImmutableSet.of(IDLE, ERROR, RUNNING))
                    .put(ERROR, ImmutableSet.of(IDLE))
                    .put(RUNNING, ImmutableSet.of(
                            IDLE, ERROR, FINISHED))
                    .put(FINISHED, ImmutableSet.of(IDLE))
                    .build();

    private AtomicInteger state;

    public DownloadSessionState(int state) {
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
