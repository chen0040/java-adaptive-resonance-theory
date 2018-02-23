package com.github.chen0040.art.falcon;

import java.util.Map;

/**
 * Created by chen0469 on 10/2/2015 0002.
 */
public class Tuple2 <Key, Value> implements Map.Entry<Key, Value> {
    private Key key;
    private Value value;

    public Tuple2(Key key, Value value){
        this.key = key;
        this.value = value;
    }

    public Key getKey() {
        return key;
    }

    public Value getValue() {
        return value;
    }

    public Value setValue(Value value) {
        return this.value = value;
    }
}
