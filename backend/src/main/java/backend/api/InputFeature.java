package backend.api;

import backend.data.Modifier;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record InputFeature( @Positive int requiredLevel , @NotEmpty String name, @NotEmpty String description, List<Modifier> modifiers) {

    public InputFeature {
        if (requiredLevel  >20) {
            throw new IllegalArgumentException("Required level should be maximum of 20");

        }
    }

}