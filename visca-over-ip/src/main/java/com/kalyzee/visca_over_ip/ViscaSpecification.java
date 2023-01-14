package com.kalyzee.visca_over_ip;

public enum ViscaSpecification {

    SPEC_A("SPEC_A"),
    SPEC_B("SPEC_B");

    private String spec;

    private ViscaSpecification(String spec) {
        this.spec = spec;
    }

    public String getString() {
        return spec;
    }

    public static ViscaSpecification value(String specification) {
        for (ViscaSpecification e : values()) {
            if (e.spec.equals(specification)) {
                return e;
            }
        }
        throw new IllegalArgumentException();
    }
}
