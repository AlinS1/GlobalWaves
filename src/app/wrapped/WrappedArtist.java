package app.wrapped;

import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.player.PlayerSource;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.TreeMap;

public class WrappedArtist implements Wrapped {

    @JsonIgnore
    private String userType = "ARTIST";
    @JsonIgnore
    private TreeMap<String, Integer> allAlbums = new TreeMap<>();
    @JsonIgnore
    private TreeMap<String, Integer> allSongs = new TreeMap<>();
    @JsonIgnore
    private TreeMap<String, Integer> allFans = new TreeMap<>();
    private TreeMap<String, Integer> topAlbums;
    private TreeMap<String, Integer> topSongs;
    private ArrayList<String> topFans;
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

    public TreeMap<String, Integer> getMax5TopAlbums() {
        return allAlbums.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }

    public TreeMap<String, Integer> getMax5TopSongs() {
        return allSongs.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }

    public ArrayList<String> getMax5TopFans() {
        ArrayList<String> topFansList = new ArrayList<>();
        int kon = 0;
        for (String fan : allFans.keySet()) {
            topFansList.add(fan);
            kon++;
            if (kon == 5) {
                break;
            }
        }
        return topFansList;
    }

    public int getNoOfListens() {
        return allFans.entrySet().size();
    }

    public boolean verifyWrapped(){
        if(allAlbums.entrySet().size() == 0 && allSongs.entrySet().size() == 0 && allFans.entrySet().size() == 0){
            return false;
        }
        return true;
    }

    @Override
    public void updateWrapped(PlayerSource source, User user) {
        AudioFile audioFile = (AudioFile) source.getAudioFile();
        addListenSong(audioFile.getName());
        addListenAlbum(((Song)audioFile).getAlbum());
        addListenFan(user.getUsername());
    }

    public void makeFinalWrapped(){
        topAlbums = getMax5TopAlbums();
        topSongs = getMax5TopSongs();
        topFans = getMax5TopFans();
        listeners = getNoOfListens();
    }
}
