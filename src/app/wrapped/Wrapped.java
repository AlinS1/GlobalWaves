package app.wrapped;

import app.audio.Files.AudioFile;
import app.player.PlayerSource;
import app.user.User;

import java.nio.file.attribute.UserDefinedFileAttributeView;

public interface Wrapped {
    public String getUserType();
    public boolean verifyWrapped();
    public void updateWrapped(PlayerSource source, User user);
    public void makeFinalWrapped();
}

