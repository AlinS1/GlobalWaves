package app.wrapped;

import app.audio.Collections.Album;
import app.audio.Files.Episode;
import app.audio.Files.Song;
import app.user.Artist;
import app.utils.Enums;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class WrappedUser implements Wrapped{
    private String userType = "USER";
    private TreeMap<String, Integer> listenedArtistsCount = new TreeMap<>();
    private TreeMap<String, Integer> listenedGenresCount = new TreeMap<>();
    private TreeMap<String, Integer> listenedSongsCount = new TreeMap<>();
    private TreeMap<String, Integer> listenedAlbumsCount = new TreeMap<>();
    private TreeMap<String, Integer> watchedEpisodesCount = new TreeMap<>();

    public WrappedUser(String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
    }

    public void addListenedArtist(Artist artist) {
        if (listenedArtistsCount.containsKey(artist.getUsername())) {
            listenedArtistsCount.put(artist.getUsername(), listenedArtistsCount.get(artist.getUsername()) + 1);
        } else {
            listenedArtistsCount.put(artist.getUsername(), 1);
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

    public void addListenedAlbum(Album album) {
        if (listenedAlbumsCount.containsKey(album.getName())) {
            listenedAlbumsCount.put(album.getName(), listenedAlbumsCount.get(album.getName()) + 1);
        } else {
            listenedAlbumsCount.put(album.getName(), 1);
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
        if(listenedArtistsCount.size() == 0 && listenedGenresCount.size() == 0 && listenedSongsCount.size() == 0 && listenedAlbumsCount.size() == 0 && watchedEpisodesCount.size() == 0){
            return false;
        }
        return true;
    }
}
