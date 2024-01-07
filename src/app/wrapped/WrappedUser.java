package app.wrapped;

import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.player.PlayerSource;
import app.user.User;
import app.utils.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

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


    public WrappedUser(String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
    }

    public void addListenedArtist(String artist) {
        if (listenedArtistsCount.containsKey(artist)) {
            listenedArtistsCount.put(artist, listenedArtistsCount.get(artist) + 1);
        } else {
            listenedArtistsCount.put(artist, 1);
        }
    }

    public void addListenedGenre(String genre) {
        if (listenedGenresCount.containsKey(genre)) {
            listenedGenresCount.put(genre, listenedGenresCount.get(genre) + 1);
        } else {
            listenedGenresCount.put(genre, 1);
        }
    }

    public void addListenedSong(Song song) {
        if (listenedSongsCount.containsKey(song.getName())) {
            listenedSongsCount.put(song.getName(), listenedSongsCount.get(song.getName()) + 1);
        } else {
            listenedSongsCount.put(song.getName(), 1);
        }
    }

    public void addListenedAlbum(String album) {
        if (listenedAlbumsCount.containsKey(album)) {
            listenedAlbumsCount.put(album, listenedAlbumsCount.get(album) + 1);
        } else {
            listenedAlbumsCount.put(album, 1);
        }
    }

    public void addWatchedEpisode(Episode episode) {
        if (watchedEpisodesCount.containsKey(episode.getName())) {
            watchedEpisodesCount.put(episode.getName(), watchedEpisodesCount.get(episode.getName()) + 1);
        } else {
            watchedEpisodesCount.put(episode.getName(), 1);
        }
    }

    public void setTop5ListenedArtists() {
        topArtists = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(listenedArtistsCount.entrySet());

        Collections.sort(entryList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topArtists.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == 5) {
                break;
            }
        }
    }

    public void setTop5ListenedGenres() {
        topGenres = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(listenedGenresCount.entrySet());

        Collections.sort(entryList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topGenres.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == 5) {
                break;
            }
        }
    }

    public void setTop5ListenedSongs() {
        topSongs = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(listenedSongsCount.entrySet());

        Collections.sort(entryList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topSongs.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == 5) {
                break;
            }
        }
    }

    public void setTop5ListenedAlbums() {
        topAlbums = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(listenedAlbumsCount.entrySet());

        Collections.sort(entryList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topAlbums.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == 5) {
                break;
            }
        }
    }

    public void setTop5WatchedEpisodes() {
        topEpisodes = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(watchedEpisodesCount.entrySet());

        Collections.sort(entryList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topEpisodes.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == 5) {
                break;
            }
        }
    }


    @Override
    public boolean verifyWrapped() {
        if (listenedArtistsCount.size() == 0 && listenedGenresCount.size() == 0 && listenedSongsCount.size() == 0 && listenedAlbumsCount.size() == 0 && watchedEpisodesCount.size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void updateWrapped(PlayerSource source, User user) {
        if (source.getType() == Enums.PlayerSourceType.LIBRARY || source.getType() == Enums.PlayerSourceType.ALBUM || source.getType() == Enums.PlayerSourceType.PLAYLIST) {
            Song song = (Song) source.getAudioFile();

            // System.out.println(song.getName() + song);

            addListenedSong(song);
            addListenedArtist(song.getArtist());
            addListenedAlbum(song.getAlbum());
            addListenedGenre(song.getGenre());
        } else if (source.getType() == Enums.PlayerSourceType.PODCAST) {
            Episode episode = (Episode) source.getAudioFile();
            addWatchedEpisode(episode);
            // update of host...
        }

//        else if (source.getType() == Enums.PlayerSourceType.PLAYLIST || source.getType() == Enums.PlayerSourceType.ALBUM) {
//            AudioCollection audioCollection = source.getAudioCollection();
//
//        }

    }

    public void makeFinalWrapped() {
        setTop5ListenedArtists();
        setTop5ListenedGenres();
        setTop5ListenedSongs();
        setTop5ListenedAlbums();
        setTop5WatchedEpisodes();
    }
}

// Create a comparator that compares the values of a TreeMap
