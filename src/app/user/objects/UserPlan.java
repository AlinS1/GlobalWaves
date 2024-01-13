package app.user.objects;

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
        BASIC, PREMIUM
    }

    private final int premiumPrice = 1000000;

    @Getter
    private PlanType type;
    @Getter
    private ArrayList<Song> songsHistoryPremium;
    private ArrayList<Song> songsHistoryBasic;

    public UserPlan() {
        this.type = PlanType.BASIC;
        this.songsHistoryPremium = new ArrayList<>();
        this.songsHistoryBasic = new ArrayList<>();
    }

    /**
     * Adds a song to the history of a Premium user.
     *
     * @param song the song to be added to the history
     */
    public void addSongToHistoryPremium(final Song song) {
        songsHistoryPremium.add(song);
    }

    /**
     * Adds a song to the history of a Basic user.
     *
     * @param song the song to be added to the history
     */
    public void addSongToHistoryBasic(final Song song) {
        songsHistoryBasic.add(song);
    }

    /**
     * Sets the type of the plan.
     *
     * @param wantedType the type of the new plan
     * @return a message that the plan was changed successfully or not
     */
    public String setType(final PlanType wantedType) {
        // If the user already has the wanted type of plan, return a message.
        if (wantedType == type) {
            if (wantedType == PlanType.BASIC) {
                return " is not a premium user.";
            } else {
                return " is already a premium user.";
            }
        } else {
            this.type = wantedType;
            if (wantedType == PlanType.BASIC) {
                // If the user was premium, update the revenue for the artists.
                updateRevenueForArtistsPremium();
                return " cancelled the subscription successfully.";
            } else {
                return " bought the subscription successfully.";
            }
        }
    }

    /**
     * Updates the revenue made by a user that was Premium.
     */
    public void updateRevenueForArtistsPremium() {
        // Group the songs by artist.
        Map<String, List<Song>> artistsHistory = songsHistoryPremium.stream()
                .collect(Collectors.groupingBy(Song::getArtist));

        double nrSongsInHistory = songsHistoryPremium.size();

        // For each artist, calculate the revenue made by the artist.
        for (Map.Entry<String, List<Song>> entry : artistsHistory.entrySet()) {
            Artist artist = Admin.getInstance().getArtist(entry.getKey());

            double nrListensArtist = entry.getValue().size();
            double artistRevenue = premiumPrice * nrListensArtist / nrSongsInHistory;

            // Add revenue for each song separately.
            // Will help us calculate the most profitable song.
            for (Song song : entry.getValue()) {
                artist.getMonetization().addRevenueForOneSong(song.getName(),
                        artistRevenue / entry.getValue().size());
            }
            // Add to the total revenue for the artist.
            artist.getMonetization().addAllSongsRevenue(artistRevenue);
        }
        songsHistoryPremium.clear();
    }

    /**
     * Updates the revenue made by a user that was Basic using the latest ad.
     *
     * @param adPrice the price of the latest ad
     */
    public void updateRevenueForArtistsBasic(final int adPrice) {
        // Group the songs by artist.
        Map<String, List<Song>> artistsHistory = songsHistoryBasic.stream()
                .collect(Collectors.groupingBy(Song::getArtist));

        double nrSongsInHistory = songsHistoryBasic.size();

        // For each artist, calculate the revenue made by the artist.
        for (Map.Entry<String, List<Song>> entry : artistsHistory.entrySet()) {
            Artist artist = Admin.getInstance().getArtist(entry.getKey());

            double nrListensArtist = entry.getValue().size();
            double artistRevenue = adPrice * nrListensArtist / nrSongsInHistory;

            // Add revenue for each song. Will help us calculate the most profitable song.
            for (Song song : entry.getValue()) {
                artist.getMonetization().addRevenueForOneSong(song.getName(),
                        artistRevenue / entry.getValue().size());
            }
            // Add to the total revenue for the artist.
            artist.getMonetization().addAllSongsRevenue(artistRevenue);
        }
        songsHistoryBasic.clear();
    }
}
