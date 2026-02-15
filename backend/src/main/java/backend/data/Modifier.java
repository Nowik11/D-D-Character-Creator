package backend.data;

enum ModifierType {
   DAMAGE_BONUS,
   AC_BONUS,
}

public record Modifier(ModifierType modifierType, int value) {
}
