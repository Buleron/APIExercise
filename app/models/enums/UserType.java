package models.enums;

public enum UserType {
    BUSINESS("business"),
    ADMIN("admin"),
    ANALYTIC("analytic"),
    OPRIME("oprime"),
    BASIC("basic");

    private final String text;

    /**
     * @param text
     */
    private UserType(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
