package app.wrapped;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    private Map<String, Double> songsProfits = new HashMap<>();

    public void addMerchRevenue(double revenue) {
        merchRevenue += revenue;
    }

    public void addSongRevenue(double revenue) {
        songRevenue += revenue;
    }

    public double getSongRevenue() {
        return Math.round(songRevenue * 100.0) / 100.0;
    }


    public void addRevenueForOneSong(String song, double profit) {
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
        List<Map.Entry<String, Double>> sortedSongsByProfit;
        sortedSongsByProfit = songsProfits.entrySet().stream()
                                          .sorted(Map.Entry.<String, Double>comparingByValue()
                                                           .reversed().thenComparing(Map.Entry.comparingByKey())).collect(Collectors.toList());

        //  System.out.println(sortedSongsByProfit);
        mostProfitableSong = sortedSongsByProfit.get(0).getKey();
    }
}
