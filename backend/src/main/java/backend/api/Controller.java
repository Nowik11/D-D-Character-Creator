package backend.api;

import backend.data.Feature;
import backend.data.FeatureIdSetter;
import backend.data.Type;
import backend.database.DBRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
@RestController
public class Controller {

    DBRepository repository;

    public Controller(DBRepository repository) {
        this.repository = repository;
    }


  /*  @GetMapping("/type")
    public List<Type> getTypes() {
        return repository.getAllTypes();
    }*/

// to be changed

    @GetMapping("/feature/{owner_type}/{owner_name}")
    public List<Feature> getAllOwnedFeatures(@PathVariable String owner_type, @PathVariable String owner_name) {
      return  repository.getAllFeatures(owner_type,owner_name);
    }

    /*@GetMapping("/type/{name}")
    public Type getTypeById(@PathVariable String name) {
        Optional<Type> type = repository.getType(name);
        return type.orElse(null);
    }*/

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/feature/{id}")
    public void updateFeature(@PathVariable int id, @RequestBody InputFeature feature) {

        repository.updateFeature(new Feature(id, feature.requiredLevel(),feature.name(),feature.description(),feature.modifiers(),true), id);
    }

  /*  @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/type/{id}")

    public void updateType(@PathVariable int id, @RequestBody Type type) {
        repository.updateType(type, id);
    }*/


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/feature/{owner_type}/{owner_name}")
    public void createFeature(@RequestBody InputFeature feature, @PathVariable String owner_type, @PathVariable String owner_name) {
        repository.createFeature(new Feature(FeatureIdSetter.getId(),feature.requiredLevel(),feature.name(),feature.description(),feature.modifiers(),true),owner_type, owner_name);
    }

   /* @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/type")
    public void createType(@RequestBody Type type) {
        repository.createType(type);
    }*/

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/feature/{id}")
    public void deleteFeature(@PathVariable int id) {
        repository.deleteFeature(id);
    }

  /*  @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/type/{id}")
    public void deleteType(@PathVariable int id) {
        repository.deleteType(id);
    }*/

}
