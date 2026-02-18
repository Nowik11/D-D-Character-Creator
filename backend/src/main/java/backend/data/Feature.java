package backend.data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;


public record Feature(@PositiveOrZero int id, @PositiveOrZero int typeId, @Positive int requiredLevel , @NotEmpty String name, @NotEmpty String description) {

    public Feature {
        if (requiredLevel  >20) {
            throw new IllegalArgumentException("Required level should be maximum of 20");
        }
    }
}
