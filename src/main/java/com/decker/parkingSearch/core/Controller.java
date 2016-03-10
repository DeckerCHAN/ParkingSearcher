package com.decker.parkingSearch.core;

public class Controller {
    private static Controller _instance;

    private Controller() {

    }

    public static Controller get_instance() {
        if (_instance == null) {
            _instance = new Controller();
        }
        return _instance;
    }
}
