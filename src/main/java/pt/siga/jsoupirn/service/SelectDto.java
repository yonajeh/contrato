package pt.siga.jsoupirn.service;

public class SelectDto {
    private final String value;
    private final String label;

    public SelectDto(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
