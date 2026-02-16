package backend.data;

import java.util.ArrayList;

public record Race(String name, String desc, ArrayList<Feature> features, Proff proff) {
}
