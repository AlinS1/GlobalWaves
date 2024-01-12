package app.wrapped;

import lombok.Getter;
import lombok.Setter;

public class Monetization {
    @Getter
    private double merchRevenue = 0;
    @Getter
    private double songRevenue = 0;
    @Getter
    @Setter
    private int ranking;
    @Getter
    private String mostProfitableSong = "N/A";


    public void addSongRevenue(double revenue) {
        songRevenue += revenue;
    }

    public void addMerchRevenue(double revenue) {
        merchRevenue += revenue;
    }
}
