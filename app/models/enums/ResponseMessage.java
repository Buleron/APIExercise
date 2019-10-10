package models.enums;

public enum ResponseMessage {
    SUCCESSFULLY("Successfully"),
    PARAMETERS_ERROR("Parameters error"),
    TOKEN_EXPIRE("Token has been expired"),
    WRONG_TOKEN("Wrong token"),
    MISSING_PARAMETERS("Missing parameters"),
    WRONG_CREDENTIAL("Wrong username or password");
    private final String text;
    /**
     * @param text
     */
    ResponseMessage(final String text) {
        this.text = text;
    }
    @Override
    public String toString() {
        return text;
    }
}
