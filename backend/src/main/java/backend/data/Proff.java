package backend.data;

import java.util.ArrayList;

enum WeaponType{
    //TBD
}

enum ArmourType{
    //TBD
}

enum SavingThrowProff{
    //TBD
}

enum SkillsProff{
    //TBD
}


public record Proff(ArrayList<WeaponType> weaponTypes, ArrayList<ArmourType> ArmourTypes,
                    ArrayList<SavingThrowProff> SavingThrowPoints,
                    ArrayList<SkillsProff> SkillsProffs, ArrayList<String> languages){
}