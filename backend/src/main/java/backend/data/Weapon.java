package backend.data;

import java.util.ArrayList;

public record Weapon(int dmgDice, int amountDmg,
                     dmgType dmgType, ArrayList<WeaponType> weaponTypes, int goldCost) {
}
