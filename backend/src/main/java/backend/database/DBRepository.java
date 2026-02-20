package backend.database;

import backend.data.Feature;
import backend.data.FeatureIdSetter;
import backend.data.Modifier;
import backend.data.Type;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
public class DBRepository {

    private final JdbcClient jdbcClient;

    public DBRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }


    /*public List<Type> getAllTypes() {

        List<Type> types = jdbcClient.sql("SELECT id, hit_dice, name, description  FROM types ")
                .query((rs,rowNumber)->

                        new Type(rs.getInt("id"), rs.getInt("hit_dice")
                                ,rs.getString("name"), rs.getString("description"),new ArrayList<>() ))
                .list();
        for(Type type : types){
            List<Feature> features = jdbcClient.sql("SELECT * FROM features WHERE type_id= ?")
                    .param(type.id())
                    .query(Feature.class)
                    .list();
            type.features().addAll(features);
        }
        return types;
    }


    public Optional<Type> getType(int id) {

       Optional<Type> type = jdbcClient.sql("SELECT id, hit_dice, name, description FROM types where id = ?" )
                .param( id)
                .query((rs,rowNumber)->

                        new Type(rs.getInt("id"), rs.getInt("hit_dice")
                ,rs.getString("name"), rs.getString("description"),new ArrayList<>() ))

               .optional();

       if(type.isPresent()){
           List <Feature> features = jdbcClient.sql("SELECT * FROM FEATURES WHERE type_id = ? ")
                   .param(type.get().id())
                   .query(Feature.class)
                   .list();
           type.ifPresent(feature -> feature.features().addAll(features));
       }
       return type;
    }*/


    // for example if u want to get features of type just pass "type" as argument of owner and name of current instance
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

    // i don't think that's needed but i will leave it here for now
//    public Optional<Feature> getFeature(int id, String typeOfOwner) {
//        return jdbcClient.sql("SELECT * from features_"+typeOfOwner+ " WHERE owner_name = ?")
//                .params(id)
//                .query(Feature.class)
//                .optional();
//    }

   /* public void createType(Type type) {

        var updatedRows = jdbcClient.sql("INSERT INTO types(id, hit_dice, name, description) values (?,?,?,?)")
                .params(List.of(type.id(), type.hitDice(), type.name(), type.description()))
                .update();

        Assert.state(updatedRows > 0, "Failed to create type" + type.name());
        for(Feature feature : type.features()){

            createFeature(feature, type.id());
        }

    }

    public void createAllTypes(List<Type> types){
        types.forEach(this::createType);
    }

    public void updateType(Type type, int id) {
        var updatedRows = jdbcClient.sql("UPDATE types SET  hit_dice = ?, name = ?, description = ? WHERE id = ?")
                .params(List.of( type.hitDice(), type.name(), type.description(), id))
                .update();
        Assert.state(updatedRows > 0, "Failed to update type" + type.name());

        deleteAllFeatures(id);

        for(Feature feature : type.features()){

            createFeature(feature, type.id());
        }
    }*/

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

    /*public void deleteType(String name) {
        deleteAllFeatures(name);

        jdbcClient.sql("DELETE FROM types WHERE name = ?")
                .param(name)
                .update();
    }*/

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
    }

    public void createModifier(Modifier modifier, int featureId) {
        jdbcClient.sql("INSERT INTO modifiers(feature_id,modifier_type, modifier_value) VALUES (?,?,?)")
                .params(List.of(featureId, modifier.modifierType().name(), modifier.modifierValue()))
                .update();
    }






}
