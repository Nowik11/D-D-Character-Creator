package backend.database;

import backend.data.*;
import backend.data.enums.CastingTime;
import backend.data.enums.SpellComponent;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DBRepository {

    private final JdbcClient jdbcClient;

    public DBRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    CastingTime getCastingTime(String str) {
        try {
            Integer castingTime = Integer.parseInt(str);
            return new CastingTime(CastingTime.ActionCastTime.TIME, castingTime);
        }
        catch (NumberFormatException e) {
            return new CastingTime(CastingTime.ActionCastTime.valueOf(str), null);
        }
    }


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
                                rs.getInt("range"),
                                rs.getInt("duration"),
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
