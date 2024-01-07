package app.wrapped;

import app.audio.Files.AudioFile;
import app.player.PlayerSource;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;

import java.nio.file.attribute.UserDefinedFileAttributeView;
public interface Wrapped {
    @JsonIgnore
    public String getUserType();
    @JsonIgnore
    public boolean verifyWrapped();
    @JsonIgnore
    public void updateWrapped(PlayerSource source, User user);
    @JsonIgnore
    public void makeFinalWrapped();
}

