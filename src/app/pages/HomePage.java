package app.pages;

import app.audio.Collections.Playlist;
import app.audio.Files.Song;
import app.user.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The type Home page.
 */
public final class HomePage implements Page {
    private List<Song> likedSongs;
    private List<Playlist> followedPlaylists;
    @Getter
    private List<Song> songRecommendations = new ArrayList<>();
    @Getter
    private List<Playlist> playlistRecommendations = new ArrayList<>();
    @Getter
    private final int limit = 5;
    @Getter
    private final int limitGenre = 3;
    @Getter
    private final int limitDuration = 30;
    @Getter
    private String lastRecommendation = "none";

    /**
     * Instantiates a new Home page.
     *
     * @param user the user
     */
    public HomePage(final User user) {
        likedSongs = user.getLikedSongs();
        followedPlaylists = user.getFollowedPlaylists();
    }

    @Override
    public String printCurrentPage() {
        return ("Liked songs:\n\t%s\n\nFollowed playlists:\n\t%s\n\nSong recommendations:"
                + "\n\t%s\n\nPlaylists recommendations:\n\t%s").formatted(
                likedSongs.stream().sorted(Comparator.comparing(Song::getLikes).reversed())
                        .limit(limit).map(Song::getName).toList(), followedPlaylists.stream()
                        .sorted((o1, o2) ->
                                o2.getSongs().stream().map(Song::getLikes).reduce(Integer::sum)
                                        .orElse(0) - o1.getSongs().stream().map(Song::getLikes)
                                        .reduce(Integer::sum).orElse(0)).limit(limit)
                        .map(Playlist::getName).toList(),
                songRecommendations.stream().map(Song::getName).toList(),
                playlistRecommendations.stream().map(Playlist::getName).toList());
    }

    @Override
    public String getPageType() {
        return "homePage";
    }

    /**
     * Update the song recommendation.
     *
     * @param song the song to be added to the recommendations
     * @return true if the song was added successfully, false otherwise
     */
    public boolean updateSongRecommendation(final Song song) {
        if (songRecommendations.contains(song)) {
            return false;
        }
        songRecommendations.add(song);
        lastRecommendation = "random_song";
        return true;
    }

    /**
     * Update the playlist recommendation.
     *
     * @param playlist           the playlist to be added to the recommendations
     * @param recommendationType the type of recommendation (random/fan)
     * @return true if the playlist was added successfully, false otherwise
     */
    public boolean updatePlaylistRecommendation(final Playlist playlist,
                                                final String recommendationType) {
        if (playlist.getSongs().isEmpty()) {
            return false;
        }
        for (Playlist p : playlistRecommendations) {
            if (p.verifyPlaylistContainsSameSongs(playlist)) {
                return false;
            }
        }
        this.lastRecommendation = recommendationType;
        playlistRecommendations.add(playlist);
        return true;
    }

}
