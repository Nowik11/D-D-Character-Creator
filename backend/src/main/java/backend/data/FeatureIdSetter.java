package backend.data;

import org.springframework.context.annotation.Bean;

public class FeatureIdSetter {

    private static int currentId = 0;
    public static int getId(){
        return currentId++;
    }
}
