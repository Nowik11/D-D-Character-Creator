package backend.api.dtos;
import backend.data.ClassProficiency;
import backend.data.Feature;
import backend.data.ItemChoice;
import backend.data.enums.AbilityScores;
import backend.data.enums.CasterType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public record TypeDto(@NotEmpty String name, @NotEmpty String description, int hitDie, int amountOfSkillsToChoose,
                   List<Integer> abilityScoreImprovements, List<Integer> cantripsKnownPerLevel, List<FeatureDto> features,
                   AbilityScores multiClassRequirement, AbilityScores spellcastingAbility, CasterType casterType,
                   ClassProficiency proficiency, List<ItemChoice> startingEquipment)
{

    public TypeDto {
        if(hitDie <= 1){

            throw new IllegalArgumentException("Hit dice should be greater than 1");
        }
    }
}