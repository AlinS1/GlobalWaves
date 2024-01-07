package app.user;

import app.pages.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * The type Content creator.
 */
public abstract class ContentCreator extends UserAbstract {

    @Setter
    @Getter
    private String description;

    @Setter
    @Getter
    private Page page;

    @Getter
    private ArrayList<User> subscribers = new ArrayList<>();

    /**
     * Instantiates a new Content creator.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public ContentCreator(final String username, final int age, final String city) {
        super(username, age, city);
    }


    public boolean addSubscriber(final User user) {
        if (subscribers.contains(user)) {
            subscribers.remove(user);
            return false;
        }
        subscribers.add(user);
        return true;
    }
}
