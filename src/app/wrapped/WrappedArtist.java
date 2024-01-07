package app.wrapped;

import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.player.PlayerSource;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.ToString;

import java.util.*;

@ToString
public class WrappedArtist implements Wrapped {

    @JsonIgnore
    private String userType = "ARTIST";
    @JsonIgnore
    private TreeMap<String, Integer> allAlbums = new TreeMap<>();
    @JsonIgnore
    private TreeMap<String, Integer> allSongs = new TreeMap<>();
    @JsonIgnore @Getter
    private TreeMap<String, Integer> allFans = new TreeMap<>();

    @Getter
    private LinkedHashMap<String, Integer> topAlbums;
    @Getter
    private LinkedHashMap<String, Integer> topSongs;
    @Getter
    private ArrayList<String> topFans;
    @Getter
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

    public void setTop5Albums() {
        topAlbums = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(allAlbums.entrySet());

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

    public void setTop5Songs() {
        topSongs = new LinkedHashMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(allSongs.entrySet());

        Collections.sort(entryList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topSongs.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == 5) {
                break;
            }
        }    }

    public void setTop5Fans() {
        topFans = new ArrayList<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(allFans.entrySet());

        Collections.sort(entryList, (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topFans.add(entry.getKey());
            kon++;
            if (kon == 5) {
                break;
            }
        }
    }

    public void setNrOfListens() {
        listeners = allFans.entrySet().size();
    }

    public boolean verifyWrapped() {
        if (allAlbums.entrySet().isEmpty() && allSongs.entrySet().isEmpty() && allFans.entrySet().isEmpty()) {
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
        setTop5Albums();
        setTop5Songs();
        setTop5Fans();
        setNrOfListens();
    }

}
