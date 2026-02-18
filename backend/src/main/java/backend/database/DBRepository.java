package backend.database;

import backend.data.Feature;
import backend.data.Type;
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

    public int count(){

        return jdbcClient.sql("SELECT * FROM  types").query().listOfRows().size();
    }

    public List<Type> getAllTypes() {

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
    }


    public List<Feature> getAllFeatures() {
        return jdbcClient.sql("SELECT * from features")
                .query(Feature.class)
                .list();
    }

    public Optional<Feature> getFeature(int id) {
        return jdbcClient.sql("SELECT * from features WHERE features.id = ?")
                .param(id)
                .query(Feature.class)
                .optional();
    }

    public void createType(Type type) {

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
    }

    public void updateFeature(Feature feature, int id) {
        var updatedRows = jdbcClient.sql("UPDATE features SET type_id  = ?, required_level = ?, name = ?, description = ? WHERE id = ?")
                .params(List.of(feature.typeId(),feature.requiredLevel(),feature.name(),feature.description(),id ))
                .update();
        Assert.state(updatedRows > 0, "Failed to update feature" + feature.name());
    }

    public void deleteType(int id) {
        deleteAllFeatures(id);

        jdbcClient.sql("DELETE FROM types WHERE id = ?")
                .param(id)
                .update();
    }

    public void deleteFeature(int id) {
        jdbcClient.sql("DELETE FROM features WHERE id = ?")
                .param(id)
                .update();
    }

    public void deleteAllFeatures(int typeId) {
        jdbcClient.sql("DELETE FROM features WHERE type_id = ?")
                .param(typeId)
                .update();
    }

    public void createFeature(Feature feature, int typeId) {
        var updatedRowsFeat = jdbcClient.sql("INSERT INTO features(id, type_id, required_level, name, description ) values (?,?,?,?,?) ")
                .params(List.of(feature.id(), typeId, feature.requiredLevel(), feature.name(), feature.description()))
                .update();
        Assert.state(updatedRowsFeat > 0, "Failed to create feature" + feature.name());

    }




}
