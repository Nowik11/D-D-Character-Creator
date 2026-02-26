package backend.data;

import backend.data.enums.DamageType;
import jakarta.validation.constraints.Positive;

public record Damage(@Positive int die, @Positive int diceCount, DamageType damageType) {
}
