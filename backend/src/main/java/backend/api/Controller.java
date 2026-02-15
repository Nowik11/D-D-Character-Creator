package backend.api;

import backend.data.Feature;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/features")
@RestController
public class Controller {

    //generally this is a placeholder, we need a db for the controller to make sense.

    @GetMapping("")
    public List<Feature> getFeatures() {
        return List.of(
                new Feature("Power Attack", "You can choose to take a -5 penalty to your attack rolls to gain a +10 bonus to your damage rolls.", 1),
                new Feature("Cleave", "When you make a melee attack and hit, you can make an additional melee attack against a different target within reach.", 2),
                new Feature("Great Weapon Master", "On your turn, when you score a critical hit with a melee weapon or reduce a creature to 0 hit points with one, you can make one melee weapon attack as a bonus action.", 3)
        );
    }

    @GetMapping("/{id}")
    public Feature getFeatureById(@PathVariable int id) {
        if(id == 0)
            return new Feature("Power Attack", "You can choose to take a -5 penalty to your attack rolls to gain a +10 bonus to your damage rolls.", 1);
        else if(id == 1)
            return new Feature("Cleave", "When you make a melee attack and hit, you can make an additional melee attack against a different target within reach.", 2);
        else if(id == 2)
            return new Feature("Great Weapon Master", "On your turn, when you score a critical hit with a melee weapon or reduce a creature to 0 hit points with one, you can make one melee weapon attack as a bonus action.", 3);
        else
            return null;
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
