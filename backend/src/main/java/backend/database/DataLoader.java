package backend.database;

import backend.data.Feature;
import backend.data.Type;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final ObjectMapper mapper;
    private final DBRepository dbRepository;

    public DataLoader(ObjectMapper mapper,  DBRepository dbRepository) {

        this.mapper = mapper;
        this.dbRepository = dbRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loading data to database...");
        try( InputStream inputStream = getClass().getResourceAsStream("/data.json");){

            List<Type> types = mapper.readValue(inputStream, new TypeReference<>() {
            });
            dbRepository.createAllTypes(types);

        }
        catch(Exception e){
            throw new RuntimeException(e);
        }

        //testing the db
            System.out.println(dbRepository.getAllTypes());
            System.out.println(dbRepository.getType(0));
            dbRepository.updateType(new Type(0,7,"ziemniak","dobra pyrka nie jest zła", new ArrayList<Feature>(Collections.singleton((new Feature(1, 0, 0, "pieczenie pyrki", "upieczona pyrka lepsza niż zwykł"))))),0);
            System.out.println(dbRepository.getAllTypes());
            //dbRepository.deleteType(0);
    }


}
