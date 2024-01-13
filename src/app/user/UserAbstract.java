package app.user;

import app.wrapped.Wrapped;
import app.wrapped.WrappedFactory;
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

    /**
     * Gets wrapped.
     *
     * @return the wrapped
     */
    public Wrapped getWrapped() {
        return wrapped;
    }

    /**
     * Sets wrapped based on the user's type.
     *
     * @param userType the user type
     * @return the wrapped
     */
    public Wrapped setWrapped(final String userType) {
        WrappedFactory wrappedFactory = new WrappedFactory();
        return wrappedFactory.createWrapped(userType);
    }


}
