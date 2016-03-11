package com.decker.parkingSearch.core;

import com.decker.parkingSearch.entires.CityInfo;
import com.decker.parkingSearch.receiver.ParkingContentReceiver;
import com.decker.parkingSearch.receiver.ParkingListReceiver;
import com.google.gson.Gson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class Controller {
    private static Controller _instance;
    private LinkedList<CityInfo> detailList;

    private Controller() {
        this.detailList = new LinkedList<CityInfo>();
    }

    public static Controller get_instance() {
        if (_instance == null) {
            _instance = new Controller();
        }
        return _instance;
    }

    public void start() throws IOException {


        ParkingListReceiver listReceiver = new ParkingListReceiver("http://www.goseeaustralia.com.au/caravan_parks/vic");
        listReceiver.fetch();
        HashMap<String, String> list = listReceiver.getList();
        for (String key : list.keySet()) {
            ParkingContentReceiver contentReceiver = new ParkingContentReceiver(key, "http://www.goseeaustralia.com.au" + list.get(key));
            contentReceiver.fetch();
            CityInfo info = contentReceiver.getInfo();
            this.detailList.add(info);
            System.out.printf("Finished park %s %n", info.name);
        }

        FileWriter writer = new FileWriter("parks.json");
        writer.write(new Gson().toJson(this.detailList));
        writer.close();


    }
}
