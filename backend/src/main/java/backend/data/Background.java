package backend.data;

import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;

public record Background(@NotEmpty String name,
                         @NotEmpty String desc,
                         ArrayList<Skill> skillProf,
                         ArrayList<Item> toolProf,
                         ArrayList<String> languages,
                         ArrayList<Item> equipment,
                         ArrayList<Feature> features)
{ }
