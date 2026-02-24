package backend.database;

import backend.data.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class DBRepository {

    private final JdbcClient jdbcClient;

    public DBRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
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
        Assert.state(updatedRowsFeat > 0, "Failed to create feature : " + feature.name());

        for (Modifier modifier : feature.modifiers()) {
            createModifier(modifier,feature.id());
        }
    }

    public void createModifier(Modifier modifier, int featureId) {
        jdbcClient.sql("INSERT INTO modifiers(feature_id,modifier_type, modifier_value) VALUES (?,?,?)")
                .params(List.of(featureId, modifier.modifierType().name(), modifier.modifierValue()))
                .update();
    }


    // helper function
    private Race mapRowToRace(ResultSet rs, int rowNum) throws SQLException {
        String abilityIncTypeString = rs.getString("ability_increase_type");
        AbilityScores abilityScoreType = (abilityIncTypeString != null) ? AbilityScores.valueOf(abilityIncTypeString) : null;
        int abilityScoreValue = rs.getInt("ability_increase_value");
        AbstractMap.SimpleEntry<AbilityScores, Integer> abilityScoreInc = (abilityScoreType != null) ? new AbstractMap.SimpleEntry<>(abilityScoreType, abilityScoreValue) : null;

        return new Race(
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("age"),
                rs.getString("alignment"),
                Size.valueOf(rs.getString("size")),
                rs.getInt("speed"),
                abilityScoreInc,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public List<Race> getAllRaces() {
        List<Race> races = jdbcClient.sql("SELECT * FROM races").query(this::mapRowToRace).list();

        for (Race race : races) {
            race.features().addAll(getAllFeatures("race", race.name()));
        }

        return races;
    }

    public List<String> getAllRacesNames() {
        return jdbcClient.sql("SELECT name FROM races").query(String.class).list();
    }

    public Race getRaceByName(String name) {
        Race race = jdbcClient.sql("SELECT * FROM races WHERE name = ?")
                .param(name)
                .query(this::mapRowToRace)
                .single();

        race.features().addAll(getAllFeatures("race", race.name()));

        return race;
    }

    public void createRace(Race race) {
        String abilityScoreType = (race.abilityScoreInc() != null) ? race.abilityScoreInc().getKey().name() : null;
        Integer abilityScoreValue = (race.abilityScoreInc() != null) ? race.abilityScoreInc().getValue() : null;

        var updatedRows = jdbcClient.sql("INSERT INTO races(name, description, age, alignment, size, speed, ability_increase_type, ability_increase_value, languages) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .params(Arrays.asList(race.name(), race.desc(), race.age(), race.alignment(), race.size(), race.speed(), abilityScoreType, abilityScoreValue, race.languages()))
                .update();
        Assert.state(updatedRows > 0, "Failed to create race: " + race.name());

        for (Feature feature : race.features()) {
            createFeature(feature, "race", race.name());
        }
    }

    public void updateRace(Race race, String name) {
        String abilityScoreType = (race.abilityScoreInc() != null) ? race.abilityScoreInc().getKey().name() : null;
        Integer abilityScoreValue = (race.abilityScoreInc() != null) ? race.abilityScoreInc().getValue() : null;

        var updatedRows = jdbcClient.sql("UPDATE races SET description = ?, age = ?, alignment = ?, size = ?, speed = ?, ability_increase_type = ?, ability_increase_value = ?, languages = ? WHERE name = ?")
                .param(Arrays.asList(race.desc(), race.age(), race.alignment(), race.size(), race.speed(), abilityScoreType, abilityScoreValue, race.languages(), race.name()))
                .update();
        Assert.state(updatedRows > 0, "Failed to update race: " + name);

        deleteAllFeatures("race", name);

        for (Feature feature : race.features()) {
            createFeature(feature, "race", name);
        }
    }

    public void deleteRace(String name) {
        deleteAllFeatures("race", name);

        var updatedRows = jdbcClient.sql("DELETE FROM races WHERE name = ?").param(name).update();
        Assert.state(updatedRows > 0, "Failed to delete race: " + name);
    }

    public List<Background> getAllBackgrounds() {
        List<Background> backgrounds = jdbcClient.sql("SELECT * FROM backgrounds").query(
                (rs, rowNum) -> new Background(
                        rs.getString("name"),
                        rs.getString("description"),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>()
        )
        ).list();

        for (Background background : backgrounds) {
            background.features().addAll(getAllFeatures("background", background.name()));
        }

        return backgrounds;
    }

    public List<String> getAllBackgroundsNames() {
        return jdbcClient.sql("SELECT name FROM backgrounds").query(String.class).list();
    }

    public Background getBackgroundByName(String name) {
        Background background = jdbcClient.sql("SELECT * FROM backgrounds WHERE name = ?")
                .param(name)
                .query(
                        (rs, rowNum) -> new Background(
                                rs.getString("name"),
                                rs.getString("description"),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                )
                .single();

        background.features().addAll(getAllFeatures("background", background.name()));

        return background;
    }

    public void createBackground(Background background) {
        var updatedRows = jdbcClient.sql("INSERT INTO background(name, description, skill_proficiencies, tool_proficiencies, languages, equipment) VALUES (?, ?, ?, ?, ?, ?)")
                .params(Arrays.asList(background.name(), background.desc(), background.skillProf(), background.toolProf(), background.languages(), background.equipment()))
                .update();
        Assert.state(updatedRows > 0, "Failed to create background: " + background.name());

        for (Feature feature : background.features()) {
            createFeature(feature, "background", background.name());
        }
    }

    public void updateBackground(Background background, String name) {
        var updatedRows = jdbcClient.sql("UPDATE backgrounds SET description = ?, skill_proficiencies = ?, tool_proficiencies = ?, languages = ?, equipment = ? WHERE name = ?")
                .param(Arrays.asList(background.desc(), background.skillProf(), background.toolProf(), background.languages(), background.equipment(), background.name()))
                .update();
        Assert.state(updatedRows > 0, "Failed to update background: " + name);

        deleteAllFeatures("background", name);

        for (Feature feature : background.features()) {
            createFeature(feature, "background", name);
        }
    }

    public void deleteBackground(String name) {
        deleteAllFeatures("background", name);

        var updatedRows = jdbcClient.sql("DELETE FROM backgrounds WHERE name = ?").param(name).update();
        Assert.state(updatedRows > 0, "Failed to delete background: " + name);
    }
}
