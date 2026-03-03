package backend.data;

import backend.data.enums.AbilityScores;
import backend.data.enums.CasterType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public record Type(@NotEmpty String name, @NotEmpty String description, int hitDie, int amountOfSkillsToChoose,
                   List<Integer> abilityScoreImprovements, List<Integer> cantripsKnownPerLevel, List<Feature> features,
                    AbilityScores multiClassRequirement, AbilityScores spellcastingAbility, CasterType casterType,
                   ClassProficiency proficiency, List<ItemChoice> startingEquipment, boolean user_created)
{

    public Type {
        if(hitDie <= 1){

            throw new IllegalArgumentException("Hit dice should be greater than 1");
        }
    }
}   