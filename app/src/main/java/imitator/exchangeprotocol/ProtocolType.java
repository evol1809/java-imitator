package imitator.exchangeprotocol;


import imitator.App;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public enum ProtocolType {

    EAST("east"),
    NORTH("north");

    private static final Logger logger = Logger.getLogger(App.class.getName());

    String text;

    ProtocolType(String text) {
        this.text = text;
    }

    public static ProtocolType valueOfName(String value) {
        for (ProtocolType p : values()) {
            if (p.toString().equals(value)) {
                return p;
            }
        }
        logger.severe("The protocol type not exist: " + value + "."
                + " There are types: " + Arrays.stream(values())
                .map(i -> i.toString())
                .collect(Collectors.joining(", ")));
        return null;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
