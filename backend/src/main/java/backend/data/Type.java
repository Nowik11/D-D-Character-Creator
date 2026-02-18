package backend.data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
//I removed proff for easier start with DB, I will add it back later
public record Type(@PositiveOrZero int id, int hitDice,
                   @NotEmpty String name,@NotEmpty String description, @NotNull ArrayList<Feature> features){

    public Type {
        if(hitDice <= 1){
            throw new IllegalArgumentException("Hit dice should be greater than 1");
        }
    }
}