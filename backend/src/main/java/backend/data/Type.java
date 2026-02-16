package backend.data;

import java.util.ArrayList;

public record Type(int level, String name, int hitdice,
                   ArrayList<Feature> features, String desc, Proff proff){
}