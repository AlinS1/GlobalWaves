package app.wrapped;

import java.util.TreeMap;
import java.util.function.IntToDoubleFunction;

public class WrappedHost implements Wrapped{
    private String userType = "HOST";
    private TreeMap<String, Integer> topEpisodes = new TreeMap<>();
    private TreeMap<String, Integer> topFans = new TreeMap<>();

    public WrappedHost(String userType) {
        this.userType = userType;
    }

    @Override
    public String getUserType() {
        return userType;
    }

    public void addListenEpisode(String episode) {
        if (topEpisodes.containsKey(episode)) {
            topEpisodes.put(episode, topEpisodes.get(episode) + 1);
        } else {
            topEpisodes.put(episode, 1);
        }
    }

    public void addListenFan(String fan) {
        if (topFans.containsKey(fan)) {
            topFans.put(fan, topFans.get(fan) + 1);
        } else {
            topFans.put(fan, 1);
        }
    }

    public TreeMap<String, Integer> getMax5TopEpisodes() {
        return topEpisodes.entrySet().stream().limit(5).collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), TreeMap::putAll);
    }
    public int getNrListens(){
        return topFans.entrySet().size();
    }

    public boolean verifyWrapped() {
        if(topFans.entrySet().size() == 0 && topEpisodes.entrySet().size() == 0){
            return false;
        }
        return true;
    }

}
