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

    public WrappedHost(final String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
    }


    /**
     * Adds a listen for an episode.
     *
     * @param episode the episode that was listened
     */
    public void addListenEpisode(final String episode) {
        if (allEpisodes.containsKey(episode)) {
            allEpisodes.put(episode, allEpisodes.get(episode) + 1);
        } else {
            allEpisodes.put(episode, 1);
        }
    }

    /**
     * Adds a listen by a user.
     *
     * @param fan the fan that was listened
     */
    public void addListenFan(final String fan) {
        if (allFans.containsKey(fan)) {
            allFans.put(fan, allFans.get(fan) + 1);
        } else {
            allFans.put(fan, 1);
        }
    }


    /**
     * Determines the top 5 episodes by the number of listens.
     */
    public void setTop5Episodes() {
        topEpisodes = new TreeMap<>();

        ArrayList<Map.Entry<String, Integer>> entryList = new ArrayList<>(allEpisodes.entrySet());

        // Sort the list of episodes by the number of listens in descending order.
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
     * Determines the number of users that listened to the host at least once.
     */
    public void setNrListens() {
        listeners = allFans.entrySet().size();
    }


    /**
     * Verifies if the host has been listened to at least once.
     *
     * @return true if the host has been listened to at least once, false otherwise
     */
    @Override
    public boolean verifyWrapped() {
        if (allFans.entrySet().size() == 0 && allEpisodes.entrySet().size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Updates the wrapped using the episode from the source.
     *
     * @param source the source that contains the episode
     * @param user the user that is playing the episode
     */
    @Override
    public void updateWrapped(final PlayerSource source, final User user) {
        addListenEpisode(source.getAudioFile().getName());
        addListenFan(user.getUsername());
    }

    /**
     * Updates the wrapped in order to determine the needed data for the final output (top 5
     * episodes and number of users that listened to the host at least once).
     */
    @Override
    public void updateWrappedForOutput() {
        setTop5Episodes();
        setNrListens();
    }
}
