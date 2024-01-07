package app.wrapped;

import lombok.Getter;
import lombok.Setter;

public class Notification {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private String description;


    public Notification(final String name, final String description) {
        this.name = name;
        this.description = description;
    }

}
