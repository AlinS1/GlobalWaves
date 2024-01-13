package app.wrapped;

import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.player.PlayerSource;
import app.user.User;
import app.utils.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@ToString
@JsonSerialize
public class WrappedUser implements Wrapped {
    @JsonIgnore
    private String userType = "USER";
    @JsonIgnore
    private TreeMap<String, Integer> listenedArtistsCount = new TreeMap<>();
    @JsonIgnore
    private TreeMap<String, Integer> listenedGenresCount = new TreeMap<>();
    @JsonIgnore
    private TreeMap<String, Integer> listenedSongsCount = new TreeMap<>();
    @JsonIgnore
    private TreeMap<String, Integer> listenedAlbumsCount = new TreeMap<>();
    @JsonIgnore
    private TreeMap<String, Integer> watchedEpisodesCount = new TreeMap<>();

    @Getter
    private LinkedHashMap<String, Integer> topArtists;
    @Getter
    private LinkedHashMap<String, Integer> topGenres;
    @Getter
    private LinkedHashMap<String, Integer> topSongs;
    @Getter
    private LinkedHashMap<String, Integer> topAlbums;
    @Getter
    private LinkedHashMap<String, Integer> topEpisodes;


    public WrappedUser(final String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
    }


    /**
     * Adds a listen for an artist.
     *
     * @param artist the artist that was listened to
     */
    public void addListenedArtist(final String artist) {
        if (listenedArtistsCount.containsKey(artist)) {
            listenedArtistsCount.put(artist, listenedArtistsCount.get(artist) + 1);
        } else {
            listenedArtistsCount.put(artist, 1);
        }
    }

    /**
     * Adds a listen for a genre.
     *
     * @param genre the genre that was listened to
     */
    public void addListenedGenre(final String genre) {
        if (listenedGenresCount.containsKey(genre)) {
            listenedGenresCount.put(genre, listenedGenresCount.get(genre) + 1);
        } else {
            listenedGenresCount.put(genre, 1);
        }
    }

    /**
     * Adds a listen for a song.
     *
     * @param song the song that was listened to
     */
    public void addListenedSong(final Song song) {
        if (listenedSongsCount.containsKey(song.getName())) {
            listenedSongsCount.put(song.getName(), listenedSongsCount.get(song.getName()) + 1);
        } else {
            listenedSongsCount.put(song.getName(), 1);
        }
    }

    /**
     * Adds a listen for an album.
     *
     * @param album the album that was listened to
     */
    public void addListenedAlbum(final String album) {
        if (listenedAlbumsCount.containsKey(album)) {
            listenedAlbumsCount.put(album, listenedAlbumsCount.get(album) + 1);
        } else {
            listenedAlbumsCount.put(album, 1);
        }
    }

    /**
     * Adds a listen for an episode.
     *
     * @param episode the episode that was listened to
     */
    public void addWatchedEpisode(final Episode episode) {
        if (watchedEpisodesCount.containsKey(episode.getName())) {
            watchedEpisodesCount.put(episode.getName(),
                    watchedEpisodesCount.get(episode.getName()) + 1);
        } else {
            watchedEpisodesCount.put(episode.getName(), 1);
        }
    }

    /**
     * Determines the top 5 artists by the number of listens.
     */
    public void setTop5ListenedArtists() {
        topArtists = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(
                listenedArtistsCount.entrySet());

        Collections.sort(entryList,
                (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topArtists.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == LIMIT) {
                break;
            }
        }
    }


    /**
     * Determines the top 5 genres by the number of listens.
     */
    public void setTop5ListenedGenres() {
        topGenres = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(
                listenedGenresCount.entrySet());

        Collections.sort(entryList,
                (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topGenres.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == LIMIT) {
                break;
            }
        }
    }

    /**
     * Determines the top 5 songs by the number of listens.
     */
    public void setTop5ListenedSongs() {
        topSongs = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(
                listenedSongsCount.entrySet());

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
     * Determines the top 5 albums by the number of listens.
     */
    public void setTop5ListenedAlbums() {
        topAlbums = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(
                listenedAlbumsCount.entrySet());

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
     * Determines the top 5 episodes by the number of listens.
     */
    public void setTop5WatchedEpisodes() {
        topEpisodes = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(
                watchedEpisodesCount.entrySet());

        Collections.sort(entryList,
                (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topEpisodes.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == LIMIT) {
                break;
            }
        }
    }


    /**
     * Verifies if the user has ever played something.
     *
     * @return true if the user has ever played something, false otherwise
     */
    @Override
    public boolean verifyWrapped() {
        if (listenedArtistsCount.isEmpty() && listenedGenresCount.isEmpty()
                && listenedSongsCount.isEmpty() && listenedAlbumsCount.isEmpty()
                && watchedEpisodesCount.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Updates the wrapped using the audio file from the source.
     *
     * @param source the source that contains the audio file
     * @param user   the user that is playing the audio file
     */
    @Override
    public void updateWrapped(final PlayerSource source, final User user) {
        if (source.getType() == Enums.PlayerSourceType.LIBRARY
                || source.getType() == Enums.PlayerSourceType.ALBUM
                || source.getType() == Enums.PlayerSourceType.PLAYLIST) {
            Song song = (Song) source.getAudioFile();
            addListenedSong(song);
            addListenedArtist(song.getArtist());
            addListenedAlbum(song.getAlbum());
            addListenedGenre(song.getGenre());
        } else if (source.getType() == Enums.PlayerSourceType.PODCAST) {
            Episode episode = (Episode) source.getAudioFile();
            addWatchedEpisode(episode);
        }
    }

    /**
     * Updates the wrapped in order to determine the needed data for the output (top 5
     * artists, genres, songs, albums, episodes).
     */
    public void updateWrappedForOutput() {
        setTop5ListenedArtists();
        setTop5ListenedGenres();
        setTop5ListenedSongs();
        setTop5ListenedAlbums();
        setTop5WatchedEpisodes();
    }
}

// Create a comparator that compares the values of a TreeMap
