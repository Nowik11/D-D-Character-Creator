package backend.data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.AbstractMap;
import java.util.ArrayList;

public record Race(@NotEmpty String name,
                   @NotEmpty String desc,
                   @NotEmpty String age,
                   @NotEmpty String alignment,
                   @NotEmpty Size size,
                   @PositiveOrZero int speed,
                   AbstractMap.SimpleEntry<AbilityScores, Integer> abilityScoreInc,
                   ArrayList<Feature> features,
                   ArrayList<String> languages)
{ }