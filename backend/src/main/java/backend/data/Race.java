package backend.data;

import java.util.AbstractMap;
import java.util.ArrayList;

public record Race(String name,
                   String desc,
                   String age,
                   String alignment,
                   Size size,
                   int speed,
                   AbstractMap.SimpleEntry<AbilityScores, Integer> abilityScoreInc,
                   ArrayList<Feature> features,
                   ArrayList<String> languages)
{ }