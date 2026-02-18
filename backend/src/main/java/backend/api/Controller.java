package backend.api;

import backend.data.Feature;
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


    @GetMapping("/type")
    public List<Type> getTypes() {
        return repository.getAllTypes();
    }

    @GetMapping("/feature")
    public List<Feature> getFeatures() {
        return repository.getAllFeatures();
    }

    @GetMapping("/feature/{id}")
    public Feature getFeatureById(@PathVariable int id) {
        Optional<Feature> feature = repository.getFeature(id);
        return feature.orElse(null);
    }

    @GetMapping("/type/{id}")
    public Type getTypeById(@PathVariable int id) {
        Optional<Type> type = repository.getType(id);
        return type.orElse(null);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/feature/{id}")
    public void updateFeature(@PathVariable int id, @RequestBody Feature feature) {

        repository.updateFeature(feature, id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/type/{id}")

    public void updateType(@PathVariable int id, @RequestBody Type type) {
        repository.updateType(type, id);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/feature")
    public void createFeature(@RequestBody Feature feature) {
        repository.createFeature(feature,feature.typeId());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/type")
    public void createType(@RequestBody Type type) {
        repository.createType(type);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/feature/{id}")
    public void deleteFeature(@PathVariable int id) {
        repository.deleteFeature(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/type/{id}")
    public void deleteType(@PathVariable int id) {
        repository.deleteType(id);
    }

}
