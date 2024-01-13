package app.wrapped;

import app.Admin;
import app.audio.Files.Song;
import app.user.Artist;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserPlan {

    public enum PlanType {
        BASIC,
        PREMIUM
    }

    private final int premiumPrice = 1000000;

    @Getter
    private PlanType type;
    @Getter
    private ArrayList<Song> songsHistory;
    private ArrayList<Song> songsHistoryBasic;

    public UserPlan() {
        this.type = PlanType.BASIC;
        this.songsHistory = new ArrayList<>();
        this.songsHistoryBasic = new ArrayList<>();
    }


    public String setType(PlanType wantedType) {
        if (wantedType == type) {
            if (wantedType == PlanType.BASIC) {
                return " is not a premium user.";
            } else {
                return " is already a premium user.";
            }
        } else {
            this.type = wantedType;
            if (wantedType == PlanType.BASIC) {
                updateRevenueForArtistsPremium();
                return " cancelled the subscription successfully.";
            } else {
                return " bought the subscription successfully.";
            }
        }
    }

    public void updateRevenueForArtistsPremium() {
        Map<String, List<Song>> artistsHistory = songsHistory.stream().collect(Collectors.groupingBy(Song::getArtist));

        double nrSongsInHistory = songsHistory.size();
        for(Map.Entry<String, List<Song>> entry : artistsHistory.entrySet()) {
            Artist artist = Admin.getInstance().getArtist(entry.getKey());
            double nrListensArtist = entry.getValue().size();
            double artistRevenue = premiumPrice * nrListensArtist / nrSongsInHistory;

            // Add revenue for each song. Will help us calculate the most profitable song.
            for(Song song : entry.getValue()) {
                artist.getMonetization().addRevenueForOneSong(song.getName(),  artistRevenue / entry.getValue().size());
            }
            artist.getMonetization().addSongRevenue(artistRevenue);
        }

        songsHistory.clear();
    }

    public void addSongToHistory(Song song) {
        songsHistory.add(song);
    }
    public void addSongToHistoryBasic(Song song) {
        songsHistoryBasic.add(song);
    }



    public void updateRevenueForArtistsBasic(int adPrice){
        Map<String, List<Song>> artistsHistory = songsHistoryBasic.stream().collect(Collectors.groupingBy(Song::getArtist));

        double nrSongsInHistory = songsHistoryBasic.size();
        for(Map.Entry<String, List<Song>> entry : artistsHistory.entrySet()) {
            Artist artist = Admin.getInstance().getArtist(entry.getKey());
            double nrListensArtist = entry.getValue().size();
            double artistRevenue = adPrice * nrListensArtist / nrSongsInHistory;

            // Add revenue for each song. Will help us calculate the most profitable song.
            for(Song song : entry.getValue()) {
                artist.getMonetization().addRevenueForOneSong(song.getName(),  artistRevenue / entry.getValue().size());
            }
            artist.getMonetization().addSongRevenue(artistRevenue);
        }
        songsHistoryBasic.clear();
    }
}
