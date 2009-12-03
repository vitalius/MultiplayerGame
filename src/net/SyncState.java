package net;

public class SyncState {
    private String state;
    
    public SyncState(){}

    public synchronized String get() {
        String temp = state;
        state = null;
        return temp;
    }

    public synchronized void set(String s) {
        state = s;
    }
}
