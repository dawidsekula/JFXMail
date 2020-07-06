package site.transcendence.model;

public enum ConnectionState {

    LOGIN_STATE_NOT_READY(0),
    LOGIN_STATE_FAILED_BY_NETWORK(1),
    LOGIN_STATE_FAILED_BY_CREDENTIALS(2),
    LOGIN_STATE_SUCCEED(3),

    MESSAGE_SENT_OK(4),
    MESSAGE_SENT_FAILURE(5);

    private final int value;

    ConnectionState(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
