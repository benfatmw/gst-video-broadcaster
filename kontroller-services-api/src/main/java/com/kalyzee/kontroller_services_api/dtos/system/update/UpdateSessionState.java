package com.kalyzee.kontroller_services_api.dtos.system.update;

import android.util.SparseArray;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.concurrent.atomic.AtomicInteger;

public class UpdateSessionState {

    /**
     * The session is currently IDLE state.
     */
    public static final int IDLE = 0;
    /**
     * The session is currently downloading binary.
     */
    public static final int DOWNLOADING = 1;
    /**
     * The session is currently waiting for install/apply the binary.
     */
    public static final int WAITING_FOR_INSTALL = 2;
    /**
     * The session is currently installing/applying the binary.
     */
    public static final int INSTALLING = 3;
    /**
     * The session is done and all its effects are now visible.
     */
    public static final int DONE = 4;
    /**
     * There was an error during the session.
     */
    public static final int ERROR = 5;
    /**
     * The session were aborted/cancelled .
     */
    public static final int ABORTED = 6;
    private static final SparseArray<String> UPDATE_STATE_MAP = new SparseArray<>();

    static {
        UPDATE_STATE_MAP.put(0, "IDLE");
        UPDATE_STATE_MAP.put(1, "DOWNLOADING");
        UPDATE_STATE_MAP.put(2, "WAITING_FOR_INSTALL");
        UPDATE_STATE_MAP.put(3, "INSTALLING");
        UPDATE_STATE_MAP.put(4, "DONE");
        UPDATE_STATE_MAP.put(5, "ERROR");
        UPDATE_STATE_MAP.put(6, "ABORTED");
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
                            INSTALLING, ERROR))
                    .put(INSTALLING, ImmutableSet.of(
                            IDLE, ERROR))
                    .put(DONE, ImmutableSet.of(IDLE))
                    .put(ABORTED, ImmutableSet.of(IDLE))
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
     * @throws UpdateSessionState.InvalidTransitionException if transition is not allowed.
     */
    public void set(int newState) throws UpdateSessionState.InvalidTransitionException {
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
        return UPDATE_STATE_MAP.get(state);
    }

    /**
     * Converts status name to status code.
     */
    public static int getStateInt(String state) {
        /**
         * We use this function instead of #indexOfValue
         * because indexOfValue(E value) method the value is compared with the array elements
         * by reference (not logical value):
         */
        return indexOfValueByValue(UPDATE_STATE_MAP, state);
    }

    /**
     * Returns an index for which would return the
     * specified value, or a negative number if no keys map to the
     * specified value.
     * <p>Beware that this is a linear search, unlike lookups by key,
     * and that multiple keys can map to the same value and this will
     * find only one of them.
     */
    public static int indexOfValueByValue(SparseArray<String> array, String value) {
        for (int i = 0; i < array.size(); i++) {
            if (value == null) {
                if (array.get(i) == null) {
                    return i;
                }
            } else {
                if (value.equals(array.get(i))) {
                    return i;
                }
            }
        }
        return -1;
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
