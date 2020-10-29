package rpc.message;

public class HttpCallContent extends CallContent{
    private long state;

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }
}
