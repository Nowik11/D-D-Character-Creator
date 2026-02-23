package backend.api;

import backend.data.Feature;
import backend.data.IdSetter;
import backend.data.Race;
import backend.data.Subclass;
import backend.database.DBRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
@RestController
public class Controller {

    DBRepository repository;

    // CONSTRUCTOR
    public Controller(DBRepository repository) {
        this.repository = repository;
    }

    //FEATURE
    @GetMapping("/feature/{owner_type}/{owner_name}")
    public List<Feature> getAllOwnedFeatures(@PathVariable String owner_type, @PathVariable String owner_name) {
      return  repository.getAllFeatures(owner_type,owner_name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/feature/{id}")
    public void updateFeature(@PathVariable int id, @RequestBody FeatureDto feature) {

        repository.updateFeature(new Feature(id, feature.requiredLevel(),feature.name(),feature.description(),feature.modifiers(),true), id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/feature/{owner_type}/{owner_name}")
    public void createFeature(@RequestBody FeatureDto feature, @PathVariable String owner_type, @PathVariable String owner_name) {
        repository.createFeature(new Feature(IdSetter.getIdFeature(),feature.requiredLevel(),feature.name(),feature.description(),feature.modifiers(),true),owner_type, owner_name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/feature/{id}")
    public void deleteFeature(@PathVariable int id) {
        repository.deleteFeature(id);
    }


    //SUBCLASS
    @GetMapping("/subclass/{type_name}")
    public List<Subclass> getAllOwnedFeatures(@PathVariable String type_name) {
        return  repository.getAllSubclasses(type_name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/subclass/{name}")
    public void updateFeature( @PathVariable String name,@RequestBody SubclassDto subclass) {

        repository.updateSubclass(new Subclass(name,subclass.description(),dtoToFeature(subclass.features()),true),name);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/subclass/{type_name}")
    public void createSubclass(@PathVariable String type_name, @RequestBody SubclassDto subclass) {

        repository.createSubclass(new Subclass(subclass.name(),subclass.description(),dtoToFeature(subclass.features()),true),type_name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/subclass/{name}")
    public void deleteSubclass(@PathVariable String name) {
        repository.deleteSubclass(name);
    }

    public List<Feature> dtoToFeature(List<FeatureDto> features) {
        List<Feature> list = new ArrayList<>();
        for (FeatureDto feature : features) {
            list.add(new Feature(IdSetter.getIdFeature(),feature.requiredLevel(),feature.name(),feature.description(),feature.modifiers(),true));
        }
        return list;
    }


    // RACE
    @GetMapping("/race")
    public List<Race> getAllRaces() {
        return repository.getAllRaces();
    }

    @GetMapping("/race/names")
    public List<String> getAllRacesNames() {
        return repository.getAllRacesNames();
    }

    @GetMapping("/race/{name}")
    public Race getRaceByName(@PathVariable String name) {
        return repository.getRaceByName(name);
    }

    @PostMapping("/race")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRace(@RequestBody Race race) {
        repository.createRace(race);
    }

    @PutMapping("/race/{name}")
    public void updateRace(@RequestBody Race race, @PathVariable String name) {
        repository.updateRace(race, name);
    }

    @DeleteMapping("/race/{name}")
    public void deleteRace(@PathVariable String name) {
        repository.deleteRace(name);
    }
}
