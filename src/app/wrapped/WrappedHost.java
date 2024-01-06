package app.wrapped;

import app.player.PlayerSource;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.TreeMap;

public class WrappedHost implements Wrapped {
    @JsonIgnore
    private String userType = "HOST";
    @JsonIgnore
    private TreeMap<String, Integer> allEpisodes = new TreeMap<>();
    @JsonIgnore
    private TreeMap<String, Integer> allFans = new TreeMap<>();
    private TreeMap<String, Integer> topEpisodes;
    private int listeners;

    public WrappedHost(String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
    }

    public void addListenEpisode(String episode) {
        if (allEpisodes.containsKey(episode)) {
            allEpisodes.put(episode, allEpisodes.get(episode) + 1);
        } else {
            allEpisodes.put(episode, 1);
        }
    }

    public void addListenFan(String fan) {
        if (allFans.containsKey(fan)) {
            allFans.put(fan, allFans.get(fan) + 1);
        } else {
            allFans.put(fan, 1);
        }
    }

    public TreeMap<String, Integer> getMax5TopEpisodes() {
        return allEpisodes.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }

    public int getNrListens() {
        return allFans.entrySet().size();
    }

    public boolean verifyWrapped() {
        if (allFans.entrySet().size() == 0 && allEpisodes.entrySet().size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void updateWrapped(PlayerSource source, User user) {
        addListenEpisode(source.getAudioFile().getName());
        addListenFan(user.getUsername());
    }

    @Override
    public void makeFinalWrapped() {
        topEpisodes = getMax5TopEpisodes();
        listeners = getNrListens();
    }
}
