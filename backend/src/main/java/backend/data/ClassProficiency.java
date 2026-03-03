package backend.data;

import backend.data.enums.ArmorType;
import backend.data.enums.Skill;
import java.util.List;

public record ClassProficiency(List<Skill> skills, List<ArmorType> armourProficiencies, List<String> weaponProficiencies, List<String> toolsProficiencies) {

}
