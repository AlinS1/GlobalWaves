package app.wrapped;

import app.player.PlayerSource;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@ToString
public class WrappedHost implements Wrapped {
    @JsonIgnore
    private String userType = "HOST";
    @JsonIgnore
    private TreeMap<String, Integer> allEpisodes = new TreeMap<>();
    @JsonIgnore
    private TreeMap<String, Integer> allFans = new TreeMap<>();

    @Getter
    private TreeMap<String, Integer> topEpisodes;
    @Getter
    private int listeners;

    public WrappedHost(String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
    }

    public void addListenEpisode(final String episode) {
        if (allEpisodes.containsKey(episode)) {
            allEpisodes.put(episode, allEpisodes.get(episode) + 1);
        } else {
            allEpisodes.put(episode, 1);
        }
    }

    public void addListenFan(final String fan) {
        if (allFans.containsKey(fan)) {
            allFans.put(fan, allFans.get(fan) + 1);
        } else {
            allFans.put(fan, 1);
        }
    }

    public void setTop5Episodes() {
        topEpisodes = new TreeMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(allEpisodes.entrySet());

        Collections.sort(entryList,
                (entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        int kon = 0;
        for (Map.Entry<String, Integer> entry : entryList) {
            topEpisodes.put(entry.getKey(), entry.getValue());
            kon++;
            if (kon == limit) {
                break;
            }
        }
    }

    public void setNrListens() {
        listeners = allFans.entrySet().size();
    }

    public boolean verifyWrapped() {
        if (allFans.entrySet().size() == 0 && allEpisodes.entrySet().size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    public void updateWrapped(final PlayerSource source, final User user) {
        addListenEpisode(source.getAudioFile().getName());
        addListenFan(user.getUsername());
    }

    @Override
    public void makeFinalWrapped() {
        setTop5Episodes();
        setNrListens();
    }
}
