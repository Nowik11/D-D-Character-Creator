package backend.api;

import backend.api.dtos.FeatureDto;
import backend.api.dtos.SpellDto;
import backend.api.dtos.SubclassDto;
import backend.api.dtos.TypeDto;
import backend.data.*;
import backend.database.DBRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
@RestController
public class Controller {

    DBRepository repository;

    // CONSTRUCTOR
    public Controller(DBRepository repository) {
        this.repository = repository;
    }

    //HELPER FUNCTIONS
    private List<Feature> dtoToFeature(List<FeatureDto> features) {
        List<Feature> list = new ArrayList<>();
        for (FeatureDto feature : features) {
            list.add(new Feature(IdSetter.getIdFeature(),feature.requiredLevel(),feature.name(),feature.description(),feature.modifiers(),true));
        }
        return list;
    }

    //CLASS
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/class/get_names")
    public List<String> getClassNames(){
        return repository.getAllClassNames();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/class/{name}")
    public Type getTypeByName(@PathVariable String name){
        Optional<Type> type = repository.getClass(name);
        return type.orElse(null);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/class")
        public void createClass(@RequestBody TypeDto type) {

        repository.createClass(new Type(type.name(), type.description(),type.hitDie(),type.amountOfSkillsToChoose(),type.abilityScoreImprovements(),type.cantripsKnownPerLevel(),dtoToFeature(type.features()),type.multiClassRequirement(),type.spellcastingAbility(),type.casterType(),type.proficiency(),type.startingEquipment(),true));

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/class")
    public void updateClass(@RequestBody TypeDto type) {
        repository.updateClass(new Type(type.name(), type.description(),type.hitDie(),type.amountOfSkillsToChoose(),type.abilityScoreImprovements(),type.cantripsKnownPerLevel(),dtoToFeature(type.features()),type.multiClassRequirement(),type.spellcastingAbility(),type.casterType(),type.proficiency(),type.startingEquipment(),true));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/class/{name}")
    public void deleteClass(@PathVariable String name) {
        repository.deleteClass(name);
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
    public List<Subclass> getAllOwnedSubclasses(@PathVariable String type_name) {
        return  repository.getAllSubclasses(type_name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/subclass/{name}")
    public void updateSubclass( @PathVariable String name,@RequestBody SubclassDto subclass) {

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
