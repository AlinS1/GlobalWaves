package app.wrapped;


/**
 * We use the Factory Method Pattern to create the specific wrapped based on the user's type.
 */
public class WrappedFactory {

    /**
     * Create a wrapped based on the user's type.
     *
     * @param userType the user type
     * @return the specific wrapped
     */
    public Wrapped createWrapped(final String userType) {
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
