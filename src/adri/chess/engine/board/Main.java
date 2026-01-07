package adri.chess.engine.board;

import java.util.HashMap;
import java.util.Map;

public class Main {
    
    public static void main(String[] args) {
        // create an empty tile
        Map<String, Integer> map = new HashMap<>();
        map.put("Apple", 1);
        map.put("Banana", 3);
        map.put("Peach", 2);
        map.put("Orange", 4);
        map.put("Pear", 1);
        System.out.println(map.get("Pear"));
    }
}
