package dk.magenta.datafordeler.geo.data;

import java.util.HashMap;
import java.util.Map;

public class GeoHardcode {

    private static Map<Integer, Map<Integer, String>> munipialicities = new HashMap<Integer, Map<Integer, String>>();

    static {
        munipialicities.put(961, new HashMap<Integer, String>());
        munipialicities.get(961).put(4030, "Thule Air Base");
        munipialicities.get(961).put(9901, "Grønland Hjemmestyre");
        munipialicities.get(961).put(9999, "Grønland Hjemmestyre");

        munipialicities.put(957, new HashMap<Integer, String>());
        munipialicities.get(957).put(9901, "Folkeregister 1");
        munipialicities.get(957).put(9903, "Folkeregister 3");
        munipialicities.get(957).put(9902, "Folkeregister 2");
        munipialicities.get(957).put(9904, "Folkeregister 4");
        munipialicities.get(957).put(9905, "Folkeregister 5");
        munipialicities.get(957).put(9908, "Uden Fast Bopæl");
        munipialicities.get(957).put(9909, "Ukendt Adresse");
        munipialicities.get(957).put(9912, "Folkeregister 2");
        munipialicities.get(957).put(9913, "Folkeregister 3");
        munipialicities.get(957).put(9915, "Folkeregister 5");
        munipialicities.get(957).put(9918, "Uden Fast Bopæl 3912");
        munipialicities.get(957).put(9919, "Ukendt Adresse 3912");
        munipialicities.get(957).put(9929, "Ukendt Adresse");
        munipialicities.get(957).put(9998, "Uden Vejnavn");

        munipialicities.put(956, new HashMap<Integer, String>());
        munipialicities.get(956).put(9997, "Internordiske Tilfl");
        munipialicities.get(956).put(9998, "Færinger På Trawler");

        munipialicities.put(958, new HashMap<Integer, String>());
        munipialicities.get(958).put(9901, "Skattesty,Qaasuitsup");
    }

    public static String getHardcodedRoadname(int munipialicity, int roadcode) {
        return munipialicities.get(munipialicity).get(roadcode);
    }
}
