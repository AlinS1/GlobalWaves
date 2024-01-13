package app.wrapped;

import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.player.PlayerSource;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@ToString
public class WrappedArtist implements Wrapped {

    @JsonIgnore
    private String userType = "ARTIST";
    @JsonIgnore
    private TreeMap<String, Integer> allAlbums = new TreeMap<>();
    @JsonIgnore
    private TreeMap<String, Integer> allSongs = new TreeMap<>();
    @JsonIgnore
    @Getter
    private TreeMap<String, Integer> allFans = new TreeMap<>();

    @Getter
    private LinkedHashMap<String, Integer> topAlbums;
    @Getter
    private LinkedHashMap<String, Integer> topSongs;
    @Getter
    private ArrayList<String> topFans;
    @Getter
    private int listeners;

    public WrappedArtist(final String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
    }

    /**
     * Adds a listen for an album.
     *
     * @param album the album that was listened
     */
    public void addListenAlbum(final String album) {
        if (allAlbums.containsKey(album)) {
            allAlbums.put(album, allAlbums.get(album) + 1);
        } else {
            allAlbums.put(album, 1);
        }
    }

    /**
     * Adds a listen for a song.
     *
     * @param song the song that was listened
     */
    public void addListenSong(final String song) {
        if (allSongs.containsKey(song)) {
            allSongs.put(song, allSongs.get(song) + 1);
        } else {
            allSongs.put(song, 1);
        }
    }

    /**
     * Adds a listen by a user.
     *
     * @param fan the name of a user that listened to a song by the artist
     */
    public void addListenFan(final String fan) {
        if (allFans.containsKey(fan)) {
            allFans.put(fan, allFans.get(fan) + 1);
        } else {
            allFans.put(fan, 1);
        }
    }


    /**
     * Determines the top 5 albums by the number of listens.
     */
    public void setTop5Albums() {
        topAlbums = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(allAlbums.entrySet());

        // Sort the albums by the number of listens in descending order.
        Collections.sort(entryList,
                (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topAlbums.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == LIMIT) {
                break;
            }
        }
    }

    /**
     * Determines the top 5 songs by the number of listens.
     */
    public void setTop5Songs() {
        topSongs = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(allSongs.entrySet());

        // Sort the songs by the number of listens in descending order.
        Collections.sort(entryList,
                (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topSongs.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == LIMIT) {
                break;
            }
        }
    }

    /**
     * Determines the top 5 fans by the number of listens.
     */
    public void setTop5Fans() {
        topFans = new ArrayList<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(allFans.entrySet());

        // Sort the fans by the number of listens in descending order.
        Collections.sort(entryList,
                (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topFans.add(entry.getKey());
            kon++;
            if (kon == LIMIT) {
                break;
            }
        }
    }

    /**
     * Determines the number of users that listened to the artist at least once.
     */
    public void setNrOfListens() {
        listeners = allFans.entrySet().size();
    }


    /**
     * Verifies if the artist has been listened to at least once.
     *
     * @return true if the artist has been listened to at least once, false otherwise
     */
    @Override
    public boolean verifyWrapped() {
        if (allAlbums.entrySet().isEmpty() && allSongs.entrySet().isEmpty() && allFans.entrySet()
                .isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Updates the wrapped using the song from the source.
     *
     * @param source the source that contains the song
     * @param user the user that is playing the song
     */
    @Override
    public void updateWrapped(final PlayerSource source, final User user) {
        AudioFile audioFile = (AudioFile) source.getAudioFile();
        addListenSong(audioFile.getName());
        addListenAlbum(((Song) audioFile).getAlbum());
        addListenFan(user.getUsername());
    }

    /**
     * Updates the wrapped in order to determine the needed data for the final output (top 5
     * albums, top 5 songs, top 5 fans, number of users that listened to the artist at least once).
     */
    public void updateWrappedForOutput() {
        setTop5Albums();
        setTop5Songs();
        setTop5Fans();
        setNrOfListens();
    }

}
