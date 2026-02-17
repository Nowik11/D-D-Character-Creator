package backend.data;

import java.util.ArrayList;
//I removed proff for easier start with DB, I will add it back later
public record Type(int id, int hitDice,
                   String name, String description, ArrayList<Feature> features){
}