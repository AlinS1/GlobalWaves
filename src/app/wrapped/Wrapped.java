package app.wrapped;

import app.player.PlayerSource;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Wrapped {
    int LIMIT = 5;

    /**
     * Gets the wrapped's user type.
     *
     * @return the wrapped's user type
     */
    @JsonIgnore
    String getUserType();

    /**
     * Verifies if the wrapped has information to show
     *
     * @return true if the wrapped has information, false otherwise
     */
    boolean verifyWrapped();

    /**
     * Updates the wrapped by adding the audio file from the source.
     *
     * @param source the source that contains the audio file
     * @param user the user that is playing the audio file
     */
    void updateWrapped(PlayerSource source, User user);

    /**
     * Updates the wrapped in order to determine the needed data for the output.
     */
    void updateWrappedForOutput();
}

