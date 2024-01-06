package app.wrapped;

import java.util.ArrayList;
import java.util.TreeMap;

public class WrappedArtist implements Wrapped {
    private String userType = "ARTIST";
    private TreeMap<String, Integer> topAlbums = new TreeMap<>();
    private TreeMap<String, Integer> topSongs = new TreeMap<>();
    private TreeMap<String, Integer> topFans = new TreeMap<>();

    public WrappedArtist(String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
    }

    public void addListenAlbum(String album) {
        if (topAlbums.containsKey(album)) {
            topAlbums.put(album, topAlbums.get(album) + 1);
        } else {
            topAlbums.put(album, 1);
        }
    }

    public void addListenSong(String song) {
        if (topSongs.containsKey(song)) {
            topSongs.put(song, topSongs.get(song) + 1);
        } else {
            topSongs.put(song, 1);
        }
    }

    public void addListenFan(String fan) {
        if (topFans.containsKey(fan)) {
            topFans.put(fan, topFans.get(fan) + 1);
        } else {
            topFans.put(fan, 1);
        }
    }

    public TreeMap<String, Integer> getMax5TopAlbums() {
        return topAlbums.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }

    public TreeMap<String, Integer> getMax5TopSongs() {
        return topSongs.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }

    public ArrayList<String> getMax5TopFans() {
        ArrayList<String> topFansList = new ArrayList<>();
        int kon = 0;
        for (String fan : topFans.keySet()) {
            topFansList.add(fan);
            kon++;
            if (kon == 5) {
                break;
            }
        }
        return topFansList;
    }

    public int getNoOfListens() {
        return topFans.entrySet().size();
    }

    public boolean verifyWrapped(){
        if(topAlbums.entrySet().size() == 0 && topSongs.entrySet().size() == 0 && topFans.entrySet().size() == 0){
            return false;
        }
        return true;
    }
}
