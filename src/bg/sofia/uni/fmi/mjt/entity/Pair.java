package bg.sofia.uni.fmi.mjt.entity;

import java.io.Serializable;

public class Pair<S, U> implements Serializable {
    private final S first;
    private final U second;

    public Pair(S first, U second) {
        this.first = first;
        this.second = second;
    }

    public S first() {
        return first;
    }

    public U second() {
        return second;
    }
}
