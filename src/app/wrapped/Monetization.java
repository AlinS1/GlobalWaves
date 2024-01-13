package app.wrapped;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Monetization {
    @Getter
    private double merchRevenue = 0;
    private double songRevenue = 0;
    @Getter
    @Setter
    private int ranking;
    @Getter
    private String mostProfitableSong = "N/A";
    private Map<String, Integer> songsProfits = new HashMap<>();

    public void addMerchRevenue(double revenue) {
        merchRevenue += revenue;
    }

    public void addSongRevenue(double revenue) {
        songRevenue += revenue;
    }

    public double getSongRevenue() {
        return Math.round(songRevenue * 100.0) / 100.0;
    }


    public void addRevenueForOneSong(String song, int profit) {
        if (songsProfits.containsKey(song)) {
            songsProfits.put(song, songsProfits.get(song) + profit);
        } else {
            songsProfits.put(song, profit);
        }
    }

    public void updateMostProfitableSong() {
        if(songsProfits.isEmpty()) {
            return;
        }
        Stream<Map.Entry<String, Integer>> sortedSongsByProfit;
        sortedSongsByProfit = songsProfits.entrySet().stream()
                                          .sorted(Map.Entry.<String, Integer>comparingByValue()
                                                           .reversed());
        mostProfitableSong = sortedSongsByProfit.findFirst().get().getKey();
    }
}
