package backend.data.enums;

public record CastingTime(ActionCastTime actionCastTime, Integer time) {
    public enum ActionCastTime{
        REACTION,
        BONUS_ACTION,
        ACTION,
        TIME
    }
    public CastingTime {
        if(actionCastTime == ActionCastTime.TIME && time == null)
            throw new IllegalArgumentException("Time cannot be null");
        if(actionCastTime != ActionCastTime.TIME && time != null)
            throw new IllegalArgumentException("Time must be null if action based cast time is used");
    }

}
