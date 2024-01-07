package app.wrapped;

import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.player.PlayerSource;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
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
    private TreeMap<String, Integer> allFans = new TreeMap<>();

    @Getter @Setter
    private TreeMap<String, Integer> topAlbums;
    @Getter @Setter
    private TreeMap<String, Integer> topSongs;
    @Getter @Setter
    private ArrayList<String> topFans;
    @Getter @Setter
    private int listeners;


    public WrappedArtist(String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
    }

    public void addListenAlbum(String album) {
        if (allAlbums.containsKey(album)) {
            allAlbums.put(album, allAlbums.get(album) + 1);
        } else {
            allAlbums.put(album, 1);
        }
    }

    public void addListenSong(String song) {
        if (allSongs.containsKey(song)) {
            allSongs.put(song, allSongs.get(song) + 1);
        } else {
            allSongs.put(song, 1);
        }
    }

    public void addListenFan(String fan) {
        if (allFans.containsKey(fan)) {
            allFans.put(fan, allFans.get(fan) + 1);
        } else {
            allFans.put(fan, 1);
        }
    }

    public void getMax5TopAlbums() {
        topAlbums = allAlbums.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }

    public void getMax5TopSongs() {
        topSongs = allSongs.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }

    public void getMax5TopFans() {
        ArrayList<String> topFansList = new ArrayList<>();
        int kon = 0;
        for (String fan : allFans.keySet()) {
            topFansList.add(fan);
            kon++;
            if (kon == 5) {
                break;
            }
        }
        topFans = topFansList;
    }

    public void getNoOfListens() {
        listeners = allFans.entrySet().size();
    }

    public boolean verifyWrapped() {
        if (allAlbums.entrySet().size() == 0 && allSongs.entrySet().size() == 0 && allFans.entrySet().size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void updateWrapped(PlayerSource source, User user) {
        AudioFile audioFile = (AudioFile) source.getAudioFile();
        addListenSong(audioFile.getName());
        addListenAlbum(((Song) audioFile).getAlbum());
        addListenFan(user.getUsername());
    }

    public void makeFinalWrapped() {
        getMax5TopAlbums();
        getMax5TopSongs();
        getMax5TopFans();
        getNoOfListens();
    }

}
