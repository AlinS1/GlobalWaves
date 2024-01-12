package app.user;

import app.wrapped.Wrapped;
import app.wrapped.WrappedArtist;
import app.wrapped.WrappedHost;
import app.wrapped.WrappedUser;
import lombok.Getter;
import lombok.Setter;

/**
 * The type User abstract.
 */
public abstract class UserAbstract {

    @Setter
    @Getter
    private String username;

    @Setter
    @Getter
    private int age;

    @Setter
    @Getter
    private String city;
    protected Wrapped wrapped;

    /**
     * Instantiates a new User abstract.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public UserAbstract(final String username, final int age, final String city) {
        this.username = username;
        this.age = age;
        this.city = city;
        this.wrapped = setWrapped(userType());
    }

    /**
     * User type string.
     *
     * @return the string
     */
    public abstract String userType();


    // ====================== ETAPA 3 ====================== //
    public Wrapped getWrapped() {
        return wrapped;
    }

    public Wrapped setWrapped(final String userType) {
        switch (userType) {
            case "user":
                return new WrappedUser("user");
            case "artist":
                return new WrappedArtist("artist");
            case "host":
                return new WrappedHost("host");
            default:
                return null;
        }
    }


}
