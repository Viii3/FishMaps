package fish.payara.fishmaps.event;

public enum IconEnum {
    DEATH("images/event/death.png"),
    COMBAT("images/event/combat.png");

    public final String fileName;

    IconEnum (String file) {
        this.fileName = file;
    }
}
