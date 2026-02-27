package backend.api;

import backend.data.Feature;
import backend.data.IdSetter;
import backend.data.Spell;
import backend.data.Subclass;
import backend.data.Race;
import backend.data.Background;
import backend.data.Item;
import backend.database.DBRepository;

import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
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


    // BACKGROUND
    @GetMapping("/background")
    public List<Background> getAllBackgrounds() {
        return repository.getAllBackgrounds();
    }

    @GetMapping("/background/names")
    public List<String> getAllBackgroundsNames() {
        return repository.getAllBackgroundsNames();
    }

    @GetMapping("/background/{name}")
    public Background getBackgroundByName(@PathVariable String name) {
        return repository.getBackgroundByName(name);
    }

    @PostMapping("/background")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBackground(@RequestBody Background background) {
        repository.createBackground(background);
    }

    @PutMapping("/background/{name}")
    public void updateBackground(@RequestBody Background background, @PathVariable String name) {
        repository.updateBackground(background, name);
    }

    @DeleteMapping("/background/{name}")
    public void deleteBackground(@PathVariable String name) {
        repository.deleteBackground(name);
    }


    // ITEM
    @GetMapping("/item")
    public List<Item> getAllItems() {
        return repository.getAllItems();
    }

    @GetMapping("/item/names")
    public List<String> getAllItemsNames() {
        return repository.getAllItemsNames();
    }

    @GetMapping("/item/{name}")
    public Item getItemByName(@PathVariable String name) {
        return repository.getItemByName(name);
    }

    @PostMapping("/item")
    @ResponseStatus(HttpStatus.CREATED)
    public void createItem(@RequestBody Item item) {
        repository.createItem(item);
    }

    @PutMapping("/item/{name}")
    public void updateItem(@RequestBody Item item, @PathVariable String name) {
        repository.updateItem(item, name);
    }

    @DeleteMapping("/item/{name}")
    public void deleteItem(@PathVariable String name) {
        repository.deleteItem(name);
    }

    //SPELL

    @GetMapping("/spell/get_all/{type_name}")
    public List<Spell> getAllSpells(@PathVariable String type_name) {
        return repository.getAllSpells(type_name);
    }

    @PostMapping("/spell/{type_name}/{spell_name}")
    public void addToSpellList(@PathVariable String type_name, @PathVariable String spell_name) {
        repository.addToSpellList(type_name,spell_name);
    }

    @PostMapping("/spell")
    public void addSpell(@RequestBody SpellDto spell) {
        repository.createSpell(new Spell(spell.name(), spell.description(),spell.schoolOfMagic(), spell.level(),
                spell.range(), spell.duration(), spell.isConcentration(), spell.isAttack(), true,
                spell.damage(),spell.spellComponent(),spell.castingTime()));
    }

    @PutMapping("/spell")
    public void updateSpell(@RequestBody SpellDto spell) {

        repository.updateSpell(new Spell(spell.name(), spell.description(),spell.schoolOfMagic(), spell.level(),
                spell.range(), spell.duration(), spell.isConcentration(), spell.isAttack(), true,
                spell.damage(),spell.spellComponent(),spell.castingTime()));

    }

    @DeleteMapping("/spell/{spell_name}")
    public void deleteSpell(@PathVariable String spell_name) {
        repository.deleteSpell(spell_name);
    }

    @DeleteMapping("/spell/{type_name}/{spell_name}")
    public void deleteFromSpellList(@PathVariable String type_name, @PathVariable String spell_name) {
        repository.deleteFromSpellList(type_name,spell_name);
    }

}
