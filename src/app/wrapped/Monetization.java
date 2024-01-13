package app.wrapped;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Monetization {
    @Getter
    private double merchRevenue = 0;
    private double songRevenue = 0;
    @Getter
    @Setter
    private int ranking;
    @Getter
    private String mostProfitableSong = "N/A";
    private final double roundNumber = 100.0;
    private Map<String, Double> songsProfits = new HashMap<>();


    /**
     * Adds the revenue from a merchandise to the total merchandise revenue.
     *
     * @param revenue the revenue from a bought merchandise
     */
    public void addMerchRevenue(final double revenue) {
        merchRevenue += revenue;
    }


    /**
     * Adds the revenue from a song to the total song revenue.
     *
     * @param revenue the revenue from a played song
     */
    public void addAllSongsRevenue(final double revenue) {
        songRevenue += revenue;
    }


    /**
     * Gets the total song revenue.
     *
     * @return the total song revenue (rounded at two decimal points)
     */
    public double getSongRevenue() {
        return Math.round(songRevenue * roundNumber) / roundNumber;
    }


    /**
     * Adds the profit for a song to the map of songs and their profits
     *
     * @param song   the song that made profit
     * @param profit the profit made by the song
     */
    public void addRevenueForOneSong(final String song, final double profit) {
        // if the song is already in the map, add the profit to the existing profit
        if (songsProfits.containsKey(song)) {
            songsProfits.put(song, songsProfits.get(song) + profit);
        } else {
            // else, add the song to the map
            songsProfits.put(song, profit);
        }
    }

    /**
     * Determines the most profitable song
     */
    public void updateMostProfitableSong() {
        if (songsProfits.isEmpty()) {
            return;
        }
        List<Map.Entry<String, Double>> sortedSongsByProfit;

        // Sort the songs by profit in descending order, then by name in alphabetical order.
        sortedSongsByProfit = songsProfits.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey())).toList();

        mostProfitableSong = sortedSongsByProfit.get(0).getKey();
    }
}
