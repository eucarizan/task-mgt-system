package dev.nj.task_mgt.dictionaries;

public enum Status {
    CREATED("CREATED");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Status valueOfString(String value) {
        for (Status e : values()) {
            if (e.value.equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;
    }
}
