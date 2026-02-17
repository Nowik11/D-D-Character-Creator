package backend.api;

import backend.data.Feature;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/features")
@RestController
public class Controller {

    //generally this is a placeholder, we need a db for the controller to make sense.

    @GetMapping("")
    public List<Feature> getFeatures() {
        return List.of(
                new Feature(1,0,1,"test", "test")
        );
    }

    @GetMapping("/{id}")
    public Feature getFeatureById(@PathVariable int id) {
        if(id == 0)
            return new Feature(0,0,1,"test", "test");
        else
            return new Feature(id,1,0,"test", "test");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void updateFeature(@PathVariable int id, @RequestBody Feature feature) {
        //placeholder, we  need a db to actually update the feature.
        System.out.println("Updating feature with id " + id + " to " + feature);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("")
    public void createFeature(@RequestBody Feature feature) {
        //placeholder, we need a db to actually create the feature.
        System.out.println("Creating feature " + feature);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteFeature(@PathVariable int id) {
        //placeholder, we need a db to actually delete the feature.
        System.out.println("Deleting feature with id " + id);
    }

}
