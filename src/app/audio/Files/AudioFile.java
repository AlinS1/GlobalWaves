package app.audio.Files;

import app.audio.LibraryEntry;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public abstract class AudioFile extends LibraryEntry {
    private final Integer duration;

    public AudioFile(final String name, final Integer duration) {
        super(name);
        this.duration = duration;
    }
}
