package backend.data;

public class IdSetter {

    private static int currentIdFeature = 0;

    public static int getIdFeature(){
        return currentIdFeature++;
    }


}
