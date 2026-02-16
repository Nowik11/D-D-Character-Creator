package backend.data;

import java.util.ArrayList;

enum dmgType{
    //TBD
}

public record Attack(String name, ArrayList<Integer> dice, ArrayList<Integer> diceCount,
                     ArrayList<dmgType> dmgType, int hitMod) {
}
