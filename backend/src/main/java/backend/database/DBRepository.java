package backend.database;

import backend.data.*;
import backend.data.enums.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DBRepository {

    private final JdbcClient jdbcClient;

    public DBRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    private CastingTime getCastingTime(String str) {
        try {
            Integer castingTime = Integer.parseInt(str);
            return new CastingTime(CastingTime.ActionCastTime.TIME, castingTime);
        }
        catch (NumberFormatException e) {
            return new CastingTime(CastingTime.ActionCastTime.valueOf(str), null);
        }
    }

    private List<Integer> getAbilityScoreImprovements(int abilityScoreImprovements) {
        List<Integer> abilityScoreImprovementsList = new ArrayList<>();
        for(int i = 1,j=0; j<=30; j++, i*=2){
            if((abilityScoreImprovements & i) >0){
                abilityScoreImprovementsList.add(j);
            }
        }
        return abilityScoreImprovementsList;
    }

    private Integer putListToInt(List<Integer> list) {
        int result = 0;
        for(int i =0 ; i < list.size() && i<=30 ; i++){
            result += (int)Math.pow(2,list.get(i));
        }
        return result;
    }

//CLASS ( NAMED TYPE CAUSE OF KEYWORD BLOCK)

public List<String> getAllClassNames(){
        return jdbcClient.sql("SELECT name from types").query(String.class).list();
}

public Optional<Type> getClass(String typeName) {

    return jdbcClient.sql("SELECT * from types WHERE name = ?").param(typeName)
            .query(
            (rs, rowNumber) -> new Type(
                    typeName,
                    rs.getString("description"),
                    rs.getInt("hit_die"),
                    rs.getInt("amount_of_skills_to_choose"),
                    getAbilityScoreImprovements(rs.getInt("ability_score_improvements")),
                    getCantripsPerLevel(typeName),
                    getAllFeatures("type", typeName),
                    AbilityScores.valueOf(rs.getString("multiclass_requirement")),
                    AbilityScores.valueOf(rs.getString("spellcasting_ability")),
                    CasterType.valueOf(rs.getString("caster_type")),
                    getClassProficiency(typeName),
                    getStartingEquipment(typeName),
                    rs.getBoolean("user_created")

            )
    ).optional();


}

public void createClass(Type type) {
        var updatedRows = jdbcClient.sql("INSERT INTO types (name,description, hit_die, amount_of_skills_to_choose, ability_score_improvements, multiclass_requirement, spellcasting_ability, caster_type, user_created) VALUES (?,?,?,?,?,?,?,?,?)")
                .params(List.of(type.name(), type.description(),type.hitDie(), type.amountOfSkillsToChoose(),putListToInt(type.abilityScoreImprovements()),type.multiClassRequirement().name(), type.spellcastingAbility().name(),type.casterType().name(),type.user_created()))
                .update();
        Assert.state(updatedRows > 0, "Failed to insert new type : " +type.name());

        createCantripsPerLevel(type.name(), type.cantripsKnownPerLevel());

        for(Feature feature: type.features())
            createFeature(feature,"type", type.name());

        createClassProficiency(type.name(), type.proficiency());

        createStartingEquipment(type.name(), type.startingEquipment());


}

public void updateClass(Type type) {

       deleteAllFeatures("type", type.name());

        var updatedRows = jdbcClient.sql("UPDATE types SET description=?, hit_die=?,amount_of_skills_to_choose=?,ability_score_improvements=?,multiclass_requirement=?,spellcasting_ability=?,caster_type=?,user_created=? WHERE name = ?")
                .params(List.of(type.description(),type.hitDie(),type.amountOfSkillsToChoose(),putListToInt(type.abilityScoreImprovements()),type.multiClassRequirement().name(),type.spellcastingAbility().name(),type.casterType().name(),type.user_created(),type.name()))
                .update();
        Assert.state(updatedRows > 0, "Failed to update type : " +type.name());

        deleteCantripsPerLevel(type.name());
        createCantripsPerLevel(type.name(), type.cantripsKnownPerLevel());

        for(Feature feature: type.features()){
            createFeature(feature,"type", type.name());
        }
        deleteClassProficiency(type.name());
        createClassProficiency(type.name(), type.proficiency());

        deleteStartingEquipment(type.name());
        createStartingEquipment(type.name(), type.startingEquipment());

}

public void deleteClass(String typeName) {
        jdbcClient.sql("DELETE FROM types WHERE name = ?")
                .param(typeName)
                .update();
        deleteAllFeatures("type", typeName);
        deleteCantripsPerLevel(typeName);
        deleteClassProficiency(typeName);
        deleteStartingEquipment(typeName);
}
//CANTRIPS
    public List<Integer> getCantripsPerLevel(String className){
        return jdbcClient.sql("SELECT cantrips from cantrips_per_level WHERE type_name = ? ORDER BY level")
                .param(className).query(Integer.class).list();
    }

    public void createCantripsPerLevel(String typeName, List<Integer> cantripsPerLevel){
        for(int i = 0; i<cantripsPerLevel.size(); i++){
        jdbcClient.sql("INSERT INTO cantrips_per_level  (level, cantrips, type_name) VALUES (?, ?,?)")
                .params(List.of(i, cantripsPerLevel.get(i),typeName))
                .update();
        }
    }

    public void deleteCantripsPerLevel(String typeName){
        jdbcClient.sql("DELETE FROM cantrips_per_level WHERE type_name = ?")
                .param(typeName).update();
    }
//STARTING EQUIPMENT
    public List<ItemChoice> getStartingEquipment(String typeName){
        List<ItemChoice> startingEquipment = new ArrayList<>();
        Integer[] currentIndex = {-1};
        jdbcClient.sql("SELECT name, count, index, option_a from starting_equipment WHERE type_name = ? ORDER BY index")
                .param(typeName).query(
                        (rs, rowNumber) ->{
                            if(currentIndex[0] < rs.getInt("index")){
                                startingEquipment.add(new ItemChoice(new ArrayList<>(), new ArrayList<>()));
                                currentIndex[0]++;
                            }
                            if(rs.getBoolean("option_a")){
                                startingEquipment.get(startingEquipment.size()-1).optionA().add(new ItemChoice.Choice(rs.getString("name"),rs.getInt("count")));
                            }
                            else{
                                startingEquipment.get(startingEquipment.size()-1).optionB().add(new ItemChoice.Choice(rs.getString("name"),rs.getInt("count")));
                            }
                            return null;
                        }
                ).list();
        return startingEquipment;
    }

    public void createStartingEquipment(String typeName, List<ItemChoice> startingEquipment){

        for(int i = 0; i<startingEquipment.size(); i++){

            ItemChoice itemChoice = startingEquipment.get(i);
            for(int j = 0 ; j<itemChoice.optionA().size(); j++){
                jdbcClient.sql("INSERT INTO starting_equipment (type_name, name, count, index,option_a) VALUES (?, ?,?,?,?)")
                        .params(List.of(typeName, itemChoice.optionA().get(j).name(), itemChoice.optionA().get(j).count()
                        ,i,true)).update();
            }

            for(int j = 0 ; j<itemChoice.optionB().size(); j++){
                jdbcClient.sql("INSERT INTO starting_equipment (type_name, name, count, index,option_a) VALUES (?, ?,?,?,?)")
                        .params(List.of(typeName, itemChoice.optionB().get(j).name(), itemChoice.optionB().get(j).count()
                                ,i,false)).update();
            }
        }
    }

    public void deleteStartingEquipment(String typeName){
        jdbcClient.sql("DELETE FROM starting_equipment WHERE type_name = ?")
                .param(typeName).update();
    }


//CLASS PROFICIENCY
    public ClassProficiency getClassProficiency(String className) {
        ClassProficiency classProficiency = new ClassProficiency(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        jdbcClient.sql("SELECT  proficiency_type, proficiency from type_proficiencies WHERE type_name = ?").param(className)
                .query(
                        (rs, rowNumber) -> {
                            String proficiencyType = rs.getString("proficiency_type");
                            String proficiency = rs.getString("proficiency");
                            switch(proficiencyType){
                                case "skill" :
                                {
                                    classProficiency.skills().add(Skill.valueOf(proficiency));
                                    break;
                                }
                                case "armor" :{
                                    classProficiency.armourProficiencies().add(ArmorType.valueOf(proficiency));
                                    break;
                                }
                                case "weapon" :{
                                    classProficiency.weaponProficiencies().add(proficiency);
                                    break;
                                }
                                case "tool" :{
                                    classProficiency.toolsProficiencies() .add(proficiency);
                                    break;
                                }
                                default:
                                    throw new IllegalArgumentException("Invalid proficiency type: " + proficiencyType);

                            }
                            return null;
                        }
                ).list();
        return classProficiency;

    }

    public void createClassProficiency(String typeName, ClassProficiency classProficiency){

        for(Skill skill : classProficiency.skills()){
            jdbcClient.sql("Insert into type_proficiencies(type_name, proficiency_type, proficiency) values (?,?,?)")
                    .params(List.of(typeName, "skill", skill.name()))
                    .update();
        }

        for(ArmorType armor : classProficiency.armourProficiencies()){
            jdbcClient.sql("Insert into type_proficiencies(type_name, proficiency_type, proficiency) values (?,?,?)")
                    .params(List.of(typeName, "armor", armor.name()))
                    .update();
        }

        for(String weapon : classProficiency.weaponProficiencies()){
            jdbcClient.sql("Insert into type_proficiencies(type_name, proficiency_type, proficiency) values (?,?,?)")
                    .params(List.of(typeName, "weapon", weapon))
                    .update();
        }
        for(String tool : classProficiency.toolsProficiencies()){
            jdbcClient.sql("INSERT INTO type_proficiencies(type_name, proficiency_type, proficiency) values (?,?,?)")
                    .params(List.of(typeName, "tool", tool))
                    .update();
        }




    }

    public void deleteClassProficiency(String typeName){
        jdbcClient.sql("DELETE FROM type_proficiencies WHERE type_name = ?")
                .param(typeName)
                .update();
    }

//SUBCLASS
    public List<Subclass> getAllSubclasses(String typeName) {
        List<Subclass> subclasses = jdbcClient.sql("SELECT name, description,user_created FROM subclasses WHERE type_name = ?")
                .param(typeName)
                .query(
                        (rs, rowNumber)->
                                new Subclass(
                                       rs.getString("name"),
                                       rs.getString("description"),
                                       new ArrayList<>(),
                                       rs.getBoolean("user_created")
                                )
                )
                .list();

        for(Subclass subclass : subclasses) {
            subclass.features().addAll(getAllFeatures("subclass", subclass.name()));
        }
        return subclasses;
    }


    public void createSubclass(Subclass subclass, String typeName) {
        var updated_rows = jdbcClient.sql("INSERT INTO subclasses(name, description, user_created, type_name) VALUES (?, ?, ?,?)")
                .params(List.of(subclass.name(), subclass.description(), subclass.userCreated(), typeName))
                .update();
        Assert.state(updated_rows > 0, "Subclass was not created");

        for(Feature feature : subclass.features()) {

            createFeature(feature,"subclass", subclass.name());
        }

    }

    public void updateSubclass(Subclass subclass, String name) {
        var updatedRows = jdbcClient.sql("UPDATE subclasses SET description = ? WHERE name = ?")
                .params(List.of(subclass.description(), name))
                .update();
        Assert.state(updatedRows > 0, "Subclass was not updated");

        deleteAllFeatures("subclass", subclass.name());

        for(Feature feature : subclass.features()) {
            createFeature(feature,"subclass", subclass.name());
        }
    }

    public void deleteSubclass(String name) {
        var updatedRows = jdbcClient.sql("DELETE FROM subclasses WHERE name = ?")
                .param(name)
                .update();
        Assert.state(updatedRows > 0, "Subclass was not deleted");

        deleteAllFeatures("subclass", name);
    }

    //SPELL SECTION


    public List<Spell> getAllSpells(String typeName) {
        List<Spell> spells =
        jdbcClient.sql("SELECT *  FROM spells WHERE name IN " +
                        "(SELECT spell_name FROM spell_lists WHERE owner_name = ?)")
                .param(typeName)
                .query((rs, rowNumber)->
                        new Spell(
                                rs.getString("name"),
                                rs.getString("description"),
                                rs.getString("school_of_magic"),
                                rs.getInt("level"),
                                rs.getString("range"),
                                rs.getString("duration"),
                                rs.getBoolean("is_concentration"),
                                rs.getBoolean("is_attack"),
                                rs.getBoolean("user_created"),
                                new ArrayList<>(),
                                new SpellComponent((rs.getByte("spell_component")&1) !=0, (rs.getByte("spell_component")&2) !=0, ((rs.getByte("spell_component")&4)!= 0) ),
                                getCastingTime(rs.getString("casting_time"))
                        ))
                .list();
        for(Spell spell : spells) {
           spell.damage().addAll(getSpellDamage(spell.name()));
        }
    return spells;
    }

    public void createSpell(Spell spell) {


        if(jdbcClient.sql("SELECT name FROM spells WHERE name = ? ")
                .param(spell.name())
                .query((rs, rowNumber)->
                        rs.getString("name"))
                .optional()
                .isPresent())
            return;

        var updatedRows = jdbcClient.sql("INSERT INTO spells(name, description, school_of_magic, level, range, duration, is_concentration," +
                        " is_attack, user_created, spell_component, casting_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .params(List.of(spell.name(),spell.description(),spell.schoolOfMagic(),spell.level(),spell.range()
                ,spell.duration(),spell.isConcentration(), spell.isAttack(), spell.userCreated(),
                        ((spell.spellComponent().material() ? 1 : 0 )+
                                (spell.spellComponent().material() ? 2 : 0)
                                + (spell.spellComponent().verbal() ? 4 : 0))
                , (spell.castingTime().actionCastTime() == CastingTime.ActionCastTime.TIME ?
                        spell.castingTime().time().toString() :
                        spell.castingTime().actionCastTime().name())))
                .update();

        Assert.state(updatedRows > 0, "Spell was not created " + spell.name());

        for(Damage damage : spell.damage()) {
            createSpellDamage(damage, spell.name());
        }
    }

    public void updateSpell(Spell spell) {

        var updatedRows = jdbcClient.sql("UPDATE spells SET description = ?, school_of_magic=?, level = ?, range = ?" +
                ", duration = ?, is_concentration = ?, is_attack = ?, user_created = ?, spell_component = ?, casting_time = ? WHERE name = ? ")
                .params(List.of(
                        spell.description(),
                        spell.schoolOfMagic(),
                        spell.level(),
                        spell.range(),
                        spell.duration(),
                        spell.isConcentration(),
                        spell.isAttack(),
                        spell.userCreated(),
                        ((spell.spellComponent().material() ? 1 : 0 )+
                                (spell.spellComponent().material() ? 2 : 0)
                                + (spell.spellComponent().verbal() ? 4 : 0)),
                        (spell.castingTime().actionCastTime() == CastingTime.ActionCastTime.TIME ?
                                spell.castingTime().time().toString() :
                                spell.castingTime().actionCastTime().name()),
                        spell.name())

                )
                .update();

        Assert.state(updatedRows > 0, "Spell was not updated " + spell.name());

        deleteSpellDamage(spell.name());

        for(Damage damage : spell.damage()) {
            createSpellDamage(damage, spell.name());
        }

    }

    public void deleteFromSpellList(String spellName, String typeName) {

        jdbcClient.sql("DELETE FROM spell_lists WHERE owner_name = ? AND spell_name = ?")
                .param(typeName, spellName)
                .update();

    }

    public void deleteSpell(String name) {

        jdbcClient.sql("DELETE FROM spells WHERE name = ?")
                .param(name)
                .update();

        jdbcClient.sql("DELETE FROM spell_lists WHERE spell_name = ?")
                .param(name)
                .update();

        deleteSpellDamage(name);

    }

    public void addToSpellList(String spellName, String typeName) {
        jdbcClient.sql("INSERT INTO spell_lists (owner_name, spell_name) VALUES (?, ?)")
                .params(List.of(spellName, typeName))
                .update();
    }

    //FEATURE SECTION
    public List<Feature> getAllFeatures(String typeOfOwner, String nameOfOwner) {
        List<Feature> list =  jdbcClient.sql("SELECT * from features WHERE owner_type = ? AND owner_name = ?")
                .params(List.of(typeOfOwner, nameOfOwner))
                .query(
                        (rs,rowNumber)-> new Feature(
                                rs.getInt("id"),
                                rs.getInt("required_level"),
                                rs.getString("name"),
                                rs.getString("description"),
                                new ArrayList<>(),
                                rs.getBoolean("user_created")
                        )

                )
                .list();
        for(Feature feature : list){
            feature.modifiers().addAll(getAllModifiers(feature.id()));
        }
        return list;
    }

    public void updateFeature(Feature feature, int id) {
        var updatedRows = jdbcClient.sql("UPDATE features SET name  = ?, description = ?, required_level = ?, user_created= ? WHERE id = ?")
                .params(List.of(feature.name(), feature.description(), feature.id(), feature.userCreated(), id ))
                .update();
        Assert.state(updatedRows > 0, "Failed to update feature" + feature.name());

        deleteAllModifiers(feature.id());

        for(Modifier modifier : feature.modifiers()){
            createModifier(modifier, id);
        }
    }


 public void deleteFeature(int id) {

     deleteAllModifiers(id);

     jdbcClient.sql("DELETE FROM features WHERE id = ?")
               .param(id)
               .update();
}

    public void deleteAllFeatures(String typeOfOwner, String nameOfOwner) {

        List<Integer> ids = jdbcClient.sql("SELECT id FROM features WHERE owner_name = ? AND owner_type = ?")
                .params(List.of(typeOfOwner,nameOfOwner))
                .query(Integer.class)
                .list();

        for(Integer id : ids){
            deleteAllModifiers(id);
        }

        jdbcClient.sql("DELETE FROM features WHERE owner_name = ? AND owner_type = ?")
                .params(List.of(nameOfOwner, typeOfOwner))
                .update();
    }

    public void createFeature(Feature feature, String typeOfOwner, String nameOfOwner) {
        var updatedRowsFeat = jdbcClient.sql("INSERT INTO features(id,owner_type, owner_name, name, description, required_level, user_created) values (?,?,?,?,?,?,?) ")
                .params(List.of(feature.id(),typeOfOwner,nameOfOwner, feature.name(), feature.description(), feature.requiredLevel(), feature.userCreated()))
                .update();
        Assert.state(updatedRowsFeat > 0, "Failed to create feature : " + feature.name());

        for (Modifier modifier : feature.modifiers()) {
            createModifier(modifier,feature.id());
        }
    }

    //MINOR SUBCLASSES

    public List<Modifier> getAllModifiers( int featureId) {
        return jdbcClient.sql("SELECT modifier_type, modifier_value FROM modifiers WHERE feature_id = ?")
                .param(featureId)
                .query(Modifier.class)
                .list();
    }

    public void deleteAllModifiers (int featureId) {

        jdbcClient.sql("DELETE FROM modifiers WHERE feature_id = ?")
                .param(featureId)
                .update();

    }

    public void createModifier(Modifier modifier, int featureId) {
        jdbcClient.sql("INSERT INTO modifiers(feature_id,modifier_type, modifier_value) VALUES (?,?,?)")
                .params(List.of(featureId, modifier.modifierType().name(), modifier.modifierValue()))
                .update();
    }

    public List<Damage> getSpellDamage(String spellName){
        return jdbcClient.sql("SELECT die, dice_count, damage_type FROM spells_damage WHERE spell_name = ?")
                .param(spellName)
                .query(Damage.class)
                .list();
    }

    public void createSpellDamage(Damage damage, String spellName){
        jdbcClient.sql("INSERT INTO spells_damage(spell_name, die, dice_count, damage_type) VALUES (?,?,?,?)")
                .params(List.of(spellName,damage.die(),damage.diceCount(),damage.damageType().name()))
                .update();
    }

    public void deleteSpellDamage(String spellName){
        jdbcClient.sql("DELETE FROM spells_damage WHERE spell_name = ?")
                .param(spellName)
                .update();
    }






}
