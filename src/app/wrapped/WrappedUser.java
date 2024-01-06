package app.wrapped;

import app.Admin;
import app.audio.Collections.Album;
import app.audio.Collections.AudioCollection;
import app.audio.Files.AudioFile;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.player.PlayerSource;
import app.user.Artist;
import app.user.User;
import app.utils.Enums;
import com.fasterxml.jackson.annotation.JsonIgnore;
import net.sf.saxon.trans.packages.PackageLibrary;
import org.antlr.v4.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

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
    private TreeMap<String, Integer> topArtists;
    private TreeMap<String, Integer> topGenres;
    private TreeMap<String, Integer> topSongs;
    private TreeMap<String, Integer> topAlbums;
    private TreeMap<String, Integer> topEpisodes;

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

    public TreeMap<String, Integer> getMax5ListenedArtistsCount() {
        return listenedArtistsCount.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }

    public TreeMap<String, Integer> getMax5ListenedGenresCount() {
        return listenedGenresCount.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }

    public TreeMap<String, Integer> getMax5ListenedSongsCount() {
        return listenedSongsCount.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }

    public TreeMap<String, Integer> getMax5ListenedAlbumsCount() {
        return listenedAlbumsCount.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }

    public TreeMap<String, Integer> getMax5WatchedEpisodesCount() {
        return watchedEpisodesCount.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
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
            AudioFile audioFile = (AudioFile) source.getAudioFile();
            addListenedSong((Song) audioFile);
            addListenedArtist(((Song) audioFile).getArtist());
            addListenedAlbum(((Song) audioFile).getAlbum());
            addListenedGenre(((Song) audioFile).getGenre());
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

    public void makeFinalWrapped(){
        topArtists = getMax5ListenedArtistsCount();
        topGenres = getMax5ListenedGenresCount();
        topSongs = getMax5ListenedSongsCount();
        topAlbums = getMax5ListenedAlbumsCount();
        topEpisodes = getMax5WatchedEpisodesCount();
    }
}
