package connector;

public class ResponseData {
    private int status;
    private int value;

    public ResponseData(int status, int value) {
        this.status = status;
        this.value = value;
    }

    /**
     * Get the SW status from APDU response
     * @return -1 if an error was occurred during the communication, or
     *  return the code of SW status from the response
     */
    public int getStatus() {
        return status;
    }

    /**
     * Get value of response APDU DATA
     * @return -1 if an error was occurred during the communication or APDU status is different of 9000 (getStatus), or
     *  return 1 if APDU status is 9000
     *  return *value* inside APDU DATA
     */
    public int getValue() {
        return value;
    }
}
