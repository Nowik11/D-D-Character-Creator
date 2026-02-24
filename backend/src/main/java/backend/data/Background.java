package backend.data;

import java.util.ArrayList;

public record Background(String name,
                         String desc,
                         ArrayList<Skill> skillProf,
                         ArrayList<Tool> toolProf,
                         ArrayList<String> languages,
                         ArrayList<Item> equipment,
                         ArrayList<Feature> features)
{ }
