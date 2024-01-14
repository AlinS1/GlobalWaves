package app.player;

import app.audio.Collections.AudioCollection;
import app.audio.Files.AudioFile;
import app.utils.Enums;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The type Player source.
 */
public class PlayerSource {
    @Getter
    private Enums.PlayerSourceType type;
    @Getter
    private AudioCollection audioCollection;
    @Setter
    @Getter
    private AudioFile audioFile;
    @Getter
    private int index;
    private int indexShuffled;
    private int remainedDuration;
    private final List<Integer> indices = new ArrayList<>();


    /**
     * Builder Design Pattern used to create a PlayerSource object.
     */
    public static class Builder {
        private Enums.PlayerSourceType type; // the mandatory field
        private AudioCollection audioCollection;
        private AudioFile audioFile;
        private int index;
        private int indexShuffled;
        private int remainedDuration;

        /**
         * Instantiates a new Builder.
         *
         * @param givenType the given type
         */
        public Builder(final Enums.PlayerSourceType givenType) {
            this.type = givenType;
            this.index = 0;
            this.indexShuffled = 0;
            this.remainedDuration = 0;
            audioCollection = null;
            audioFile = null;
        }

        /**
         * Updates the audioCollection field.
         *
         * @param givenAudioCollection the given audio collection
         * @return the builder
         */
        public Builder audioCollection(final AudioCollection givenAudioCollection) {
            this.audioCollection = givenAudioCollection;
            return this;
        }

        /**
         * Updates the audioFile field.
         *
         * @param givenAudioFile the given audio file
         * @return the builder
         */
        public Builder audioFile(final AudioFile givenAudioFile) {
            this.audioFile = givenAudioFile;
            return this;
        }

        /**
         * Updates the index field.
         *
         * @param givenIndex the given index
         * @return the builder
         */
        public Builder index(final int givenIndex) {
            this.index = givenIndex;
            return this;
        }

        /**
         * Updates the remainedDuration field.
         *
         * @param givenRemainedDuration the given remained duration
         * @return the builder
         */
        public Builder remainedDuration(final int givenRemainedDuration) {
            this.remainedDuration = givenRemainedDuration;
            return this;
        }

        /**
         * Builds the PlayerSource object.
         *
         * @return the player source
         */
        public PlayerSource build() {
            return new PlayerSource(this);
        }
    }

    /**
     * Private constructor in order to force the use of the Builder
     *
     * @param builder the builder of the PlayerSource object
     */
    private PlayerSource(final Builder builder) {
        this.type = builder.type;
        this.audioCollection = builder.audioCollection;
        this.audioFile = builder.audioFile;
        this.index = builder.index;
        this.indexShuffled = builder.indexShuffled;
        this.remainedDuration = builder.remainedDuration;
    }


    /**
     * Gets duration.
     *
     * @return the duration
     */
    public int getDuration() {
        return remainedDuration;
    }

    /**
     * Sets next audio file.
     *
     * @param repeatMode the repeat mode
     * @param shuffle    the shuffle
     * @return the next audio file
     */
    public boolean setNextAudioFile(final Enums.RepeatMode repeatMode, final boolean shuffle) {
        boolean isPaused = false;

        if (type == Enums.PlayerSourceType.LIBRARY) {
            if (repeatMode != Enums.RepeatMode.NO_REPEAT) {
                remainedDuration = audioFile.getDuration();
            } else {
                remainedDuration = 0;
                isPaused = true;
            }
        } else {
            if (repeatMode == Enums.RepeatMode.REPEAT_ONCE
                    || repeatMode == Enums.RepeatMode.REPEAT_CURRENT_SONG
                    || repeatMode == Enums.RepeatMode.REPEAT_INFINITE) {
                remainedDuration = audioFile.getDuration();
            } else if (repeatMode == Enums.RepeatMode.NO_REPEAT) {
                if (shuffle) {
                    if (indexShuffled == indices.size() - 1) {
                        remainedDuration = 0;
                        isPaused = true;
                    } else {
                        indexShuffled++;

                        index = indices.get(indexShuffled);
                        updateAudioFile();
                        remainedDuration = audioFile.getDuration();
                    }
                } else {
                    if (index == audioCollection.getNumberOfTracks() - 1) {
                        remainedDuration = 0;
                        isPaused = true;
                    } else {
                        index++;
                        updateAudioFile();
                        remainedDuration = audioFile.getDuration();
                    }
                }
            } else if (repeatMode == Enums.RepeatMode.REPEAT_ALL) {
                if (shuffle) {
                    indexShuffled = (indexShuffled + 1) % indices.size();
                    index = indices.get(indexShuffled);
                } else {
                    index = (index + 1) % audioCollection.getNumberOfTracks();
                }
                updateAudioFile();
                remainedDuration = audioFile.getDuration();
            }
        }

        return isPaused;
    }

    /**
     * Sets prev audio file.
     *
     * @param shuffle the shuffle
     */
    public void setPrevAudioFile(final boolean shuffle) {
        if (type == Enums.PlayerSourceType.LIBRARY) {
            remainedDuration = audioFile.getDuration();
        } else {
            if (remainedDuration != audioFile.getDuration()) {
                remainedDuration = audioFile.getDuration();
            } else {
                if (shuffle) {
                    if (indexShuffled > 0) {
                        indexShuffled--;
                    }
                    index = indices.get(indexShuffled);
                    updateAudioFile();
                    remainedDuration = audioFile.getDuration();
                } else {
                    if (index > 0) {
                        index--;
                    }
                    updateAudioFile();
                    remainedDuration = audioFile.getDuration();
                }
            }
        }
    }

    /**
     * Generate shuffle order.
     *
     * @param seed the seed
     */
    public void generateShuffleOrder(final Integer seed) {
        indices.clear();
        Random random = new Random(seed);
        for (int i = 0; i < audioCollection.getNumberOfTracks(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, random);
    }

    /**
     * Update shuffle index.
     */
    public void updateShuffleIndex() {
        for (int i = 0; i < indices.size(); i++) {
            if (indices.get(i) == index) {
                indexShuffled = i;
                break;
            }
        }
    }

    /**
     * Skip.
     *
     * @param duration the duration
     */
    public void skip(final int duration) {
        remainedDuration += duration;
        if (remainedDuration > audioFile.getDuration()) {
            remainedDuration = 0;
            index++;
            updateAudioFile();
        } else if (remainedDuration < 0) {
            remainedDuration = 0;
        }
    }

    private void updateAudioFile() {
        setAudioFile(audioCollection.getTrackByIndex(index));
    }

}
