package rpc.message;

import java.io.Serializable;

public class RetContent implements Serializable, Content {
    private static final long serialVersionUID = -5498229122240775396L;
    private Object res;
    private long state;

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public Object getRes() {
        return res;
    }

    public void setRes(Object res) {
        this.res = res;
    }
}
