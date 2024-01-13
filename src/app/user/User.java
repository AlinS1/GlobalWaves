package app.user;

import app.Admin;
import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.PlaylistOutput;
import app.audio.Collections.Podcast;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
import app.pages.ArtistPage;
import app.pages.HomePage;
import app.pages.LikedContentPage;
import app.pages.Page;
import app.player.Player;
import app.player.PlayerSource;
import app.player.PlayerStats;
import app.searchBar.Filters;
import app.searchBar.SearchBar;
import app.utils.Enums;
import app.wrapped.Notification;
import app.wrapped.PageHistory;
import app.wrapped.UserPlan;
import app.wrapped.WrappedArtist;
import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * The type User.
 */
public final class User extends UserAbstract {
    @Getter
    private ArrayList<Playlist> playlists;
    @Getter
    private ArrayList<Song> likedSongs;
    @Getter
    private ArrayList<Playlist> followedPlaylists;
    @Getter
    private final Player player;
    @Getter
    private boolean status;
    private final SearchBar searchBar;
    private boolean lastSearched;
    @Getter
    @Setter
    private Page currentPage;
    @Getter
    @Setter
    private HomePage homePage;
    @Getter
    @Setter
    private LikedContentPage likedContentPage;

    @Getter
    @Setter
    private ArrayList<Notification> notifications = new ArrayList<Notification>();
    @Getter
    private PageHistory pageHistory = new PageHistory();
    @Getter
    private List<String> boughtMerch = new ArrayList<String>();
    @Getter
    private UserPlan userPlan = new UserPlan();


    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param age      the age
     * @param city     the city
     */
    public User(final String username, final int age, final String city) {
        super(username, age, city);
        playlists = new ArrayList<>();
        likedSongs = new ArrayList<>();
        followedPlaylists = new ArrayList<>();
        player = new Player();
        searchBar = new SearchBar(username);
        lastSearched = false;
        status = true;

        homePage = new HomePage(this);
        currentPage = homePage;
        pageHistory.addPage(currentPage);
        likedContentPage = new LikedContentPage(this);
    }

    @Override
    public String userType() {
        return "user";
    }


    /**
     * Search array list.
     *
     * @param filters the filters
     * @param type    the type
     * @return the array list
     */
    public ArrayList<String> search(final Filters filters, final String type) {
        searchBar.clearSelection();
        player.stop();

        lastSearched = true;
        ArrayList<String> results = new ArrayList<>();

        if (type.equals("artist") || type.equals("host")) {
            List<ContentCreator> contentCreatorsEntries = searchBar.searchContentCreator(filters,
                    type);

            for (ContentCreator contentCreator : contentCreatorsEntries) {
                results.add(contentCreator.getUsername());
            }
        } else {
            List<LibraryEntry> libraryEntries = searchBar.search(filters, type);

            for (LibraryEntry libraryEntry : libraryEntries) {
                results.add(libraryEntry.getName());
            }
        }
        return results;
    }

    /**
     * Select string.
     *
     * @param itemNumber the item number
     * @return the string
     */
    public String select(final int itemNumber) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (!lastSearched) {
            return "Please conduct a search before making a selection.";
        }

        lastSearched = false;

        if (searchBar.getLastSearchType().equals("artist") || searchBar.getLastSearchType()
                .equals("host")) {
            ContentCreator selected = searchBar.selectContentCreator(itemNumber);

            if (selected == null) {
                return "The selected ID is too high.";
            }

            currentPage = selected.getPage();
            pageHistory.addPage(currentPage);
            return "Successfully selected %s's page.".formatted(selected.getUsername());
        } else {
            LibraryEntry selected = searchBar.select(itemNumber);

            if (selected == null) {
                return "The selected ID is too high.";
            }

            return "Successfully selected %s.".formatted(selected.getName());
        }
    }

    /**
     * Load string.
     *
     * @return the string
     */
    public String load() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (searchBar.getLastSelected() == null) {
            return "Please select a source before attempting to load.";
        }

        if (!searchBar.getLastSearchType().equals("song")
                && ((AudioCollection) searchBar.getLastSelected()).getNumberOfTracks() == 0) {
            return "You can't load an empty audio collection!";
        }

        player.setSource(searchBar.getLastSelected(), searchBar.getLastSearchType());
        searchBar.clearSelection();

        this.updateWrapped(player.getSource());
        this.updateHistoryForMonetization();
        player.setAdBreakIncoming(false); // if an ad break was incoming, it is now cancelled

        player.pause();

        return "Playback loaded successfully.";
    }

    /**
     * Play pause string.
     *
     * @return the string
     */
    public String playPause() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to pause or resume playback.";
        }

        player.pause();

        if (player.getPaused()) {
            return "Playback paused successfully.";
        } else {
            return "Playback resumed successfully.";
        }
    }

    /**
     * Repeat string.
     *
     * @return the string
     */
    public String repeat() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before setting the repeat status.";
        }

        Enums.RepeatMode repeatMode = player.repeat();
        String repeatStatus = "";

        switch (repeatMode) {
            case NO_REPEAT -> {
                repeatStatus = "no repeat";
            }
            case REPEAT_ONCE -> {
                repeatStatus = "repeat once";
            }
            case REPEAT_ALL -> {
                repeatStatus = "repeat all";
            }
            case REPEAT_INFINITE -> {
                repeatStatus = "repeat infinite";
            }
            case REPEAT_CURRENT_SONG -> {
                repeatStatus = "repeat current song";
            }
            default -> {
                repeatStatus = "";
            }
        }

        return "Repeat mode changed to %s.".formatted(repeatStatus);
    }

    /**
     * Shuffle string.
     *
     * @param seed the seed
     * @return the string
     */
    public String shuffle(final Integer seed) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before using the shuffle function.";
        }

        if (!player.getType().equals("playlist") && !player.getType().equals("album")) {
            return "The loaded source is not a playlist or an album.";
        }

        player.shuffle(seed);

        if (player.getShuffle()) {
            return "Shuffle function activated successfully.";
        }
        return "Shuffle function deactivated successfully.";
    }

    /**
     * Forward string.
     *
     * @return the string
     */
    public String forward() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before attempting to forward.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipNext();

        return "Skipped forward successfully.";
    }

    /**
     * Backward string.
     *
     * @return the string
     */
    public String backward() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please select a source before rewinding.";
        }

        if (!player.getType().equals("podcast")) {
            return "The loaded source is not a podcast.";
        }

        player.skipPrev();

        return "Rewound successfully.";
    }

    /**
     * Like string.
     *
     * @return the string
     */
    public String like() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before liking or unliking.";
        }

        if (!player.getType().equals("song") && !player.getType().equals("playlist")
                && !player.getType().equals("album")) {
            return "Loaded source is not a song.";
        }

        Song song = (Song) player.getCurrentAudioFile();

        if (likedSongs.contains(song)) {
            likedSongs.remove(song);
            song.dislike();

            return "Unlike registered successfully.";
        }

        likedSongs.add(song);
        song.like();
        return "Like registered successfully.";
    }

    /**
     * Next string.
     *
     * @return the string
     */
    public String next() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        player.next();

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before skipping to the next track.";
        }

        return "Skipped to next track successfully. The current track is %s.".formatted(
                player.getCurrentAudioFile().getName());
    }

    /**
     * Prev string.
     *
     * @return the string
     */
    public String prev() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before returning to the previous track.";
        }

        player.prev();

        return "Returned to previous track successfully. The current track is %s.".formatted(
                player.getCurrentAudioFile().getName());
    }

    /**
     * Create playlist string.
     *
     * @param name      the name
     * @param timestamp the timestamp
     * @return the string
     */
    public String createPlaylist(final String name, final int timestamp) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (playlists.stream().anyMatch(playlist -> playlist.getName().equals(name))) {
            return "A playlist with the same name already exists.";
        }

        playlists.add(new Playlist(name, getUsername(), timestamp));

        return "Playlist created successfully.";
    }

    /**
     * Add remove in playlist string.
     *
     * @param id the id
     * @return the string
     */
    public String addRemoveInPlaylist(final int id) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (player.getCurrentAudioFile() == null) {
            return "Please load a source before adding to or removing from the playlist.";
        }

        if (player.getType().equals("podcast")) {
            return "The loaded source is not a song.";
        }

        if (id > playlists.size()) {
            return "The specified playlist does not exist.";
        }

        Playlist playlist = playlists.get(id - 1);

        if (playlist.containsSong((Song) player.getCurrentAudioFile())) {
            playlist.removeSong((Song) player.getCurrentAudioFile());
            return "Successfully removed from playlist.";
        }

        playlist.addSong((Song) player.getCurrentAudioFile());
        return "Successfully added to playlist.";
    }

    /**
     * Switch playlist visibility string.
     *
     * @param playlistId the playlist id
     * @return the string
     */
    public String switchPlaylistVisibility(final Integer playlistId) {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (playlistId > playlists.size()) {
            return "The specified playlist ID is too high.";
        }

        Playlist playlist = playlists.get(playlistId - 1);
        playlist.switchVisibility();

        if (playlist.getVisibility() == Enums.Visibility.PUBLIC) {
            return "Visibility status updated successfully to public.";
        }

        return "Visibility status updated successfully to private.";
    }

    /**
     * Show playlists array list.
     *
     * @return the array list
     */
    public ArrayList<PlaylistOutput> showPlaylists() {
        ArrayList<PlaylistOutput> playlistOutputs = new ArrayList<>();
        for (Playlist playlist : playlists) {
            playlistOutputs.add(new PlaylistOutput(playlist));
        }

        return playlistOutputs;
    }

    /**
     * Follow string.
     *
     * @return the string
     */
    public String follow() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        LibraryEntry selection = searchBar.getLastSelected();
        String type = searchBar.getLastSearchType();

        if (selection == null) {
            return "Please select a source before following or unfollowing.";
        }

        if (!type.equals("playlist")) {
            return "The selected source is not a playlist.";
        }

        Playlist playlist = (Playlist) selection;

        if (playlist.getOwner().equals(getUsername())) {
            return "You cannot follow or unfollow your own playlist.";
        }

        if (followedPlaylists.contains(playlist)) {
            followedPlaylists.remove(playlist);
            playlist.decreaseFollowers();

            return "Playlist unfollowed successfully.";
        }

        followedPlaylists.add(playlist);
        playlist.increaseFollowers();


        return "Playlist followed successfully.";
    }

    /**
     * Gets player stats.
     *
     * @return the player stats
     */
    public PlayerStats getPlayerStats() {
        return player.getStats();
    }

    /**
     * Show preferred songs array list.
     *
     * @return the array list
     */
    public ArrayList<String> showPreferredSongs() {
        ArrayList<String> results = new ArrayList<>();
        for (AudioFile audioFile : likedSongs) {
            results.add(audioFile.getName());
        }

        return results;
    }

    /**
     * Gets preferred genre.
     *
     * @return the preferred genre
     */
    public String getPreferredGenre() {
        String[] genres = {"pop", "rock", "rap"};
        int[] counts = new int[genres.length];
        int mostLikedIndex = -1;
        int mostLikedCount = 0;

        for (Song song : likedSongs) {
            for (int i = 0; i < genres.length; i++) {
                if (song.getGenre().equals(genres[i])) {
                    counts[i]++;
                    if (counts[i] > mostLikedCount) {
                        mostLikedCount = counts[i];
                        mostLikedIndex = i;
                    }
                    break;
                }
            }
        }

        String preferredGenre = mostLikedIndex != -1 ? genres[mostLikedIndex] : "unknown";
        return "This user's preferred genre is %s.".formatted(preferredGenre);
    }

    /**
     * Switch status.
     */
    public void switchStatus() {
        status = !status;
    }

    /**
     * Simulate time.
     *
     * @param time the time
     */
    public void simulateTime(final int time) {
        if (!status) {
            return;
        }

        player.simulatePlayer(time, this);
    }


    /**
     * Update wrapped based on the playing source.
     *
     * @param source the source to be added to the wrapped
     */
    public void updateWrapped(final PlayerSource source) {
        if (source == null || source.getAudioFile() == null) {
            return;
        }

        // Update the wrapped of the normal user.
        this.wrapped.updateWrapped(source, this);

        // Update the wrapped of the artist.
        if (source.getType() == Enums.PlayerSourceType.LIBRARY
                || source.getType() == Enums.PlayerSourceType.ALBUM
                || source.getType() == Enums.PlayerSourceType.PLAYLIST) {
            AudioFile audioFile = (AudioFile) source.getAudioFile();
            Artist artist = Admin.getInstance().getArtist(((Song) audioFile).getArtist());
            artist.getWrapped().updateWrapped(source, this);
        }

        // Update the wrapped of the host.
        if (source.getType() == Enums.PlayerSourceType.PODCAST) {
            Podcast podcast = (Podcast) source.getAudioCollection();

            Host host = Admin.getInstance().getHost(podcast.getOwner());
            if (host != null) {
                host.getWrapped().updateWrapped(source, this);
            }
        }
    }

    /**
     * Subscribe to a ContentCreator.
     *
     * @return the output of the command
     */
    public String subscribe() {
        String message = this.getUsername();
        if (searchBar.getLastContentCreatorSelected().addSubscriber(this)) {
            message += " subscribed to " + searchBar.getLastContentCreatorSelected().getUsername();
        } else {
            message += " unsubscribed from " + searchBar.getLastContentCreatorSelected()
                    .getUsername();
        }
        message += " successfully.";

        return message;
    }

    /**
     * Add a new notification.
     *
     * @param name        the name of the notification
     * @param description the description of the notification
     */
    public void addNotification(final String name, final String description) {
        Notification notification = new Notification(name, description);
        notifications.add(notification);
    }

    /**
     * Change the user's page to the previous one from their history.
     *
     * @return the output of the command
     */
    public String previousPage() {
        Page previousPage = pageHistory.getPreviousPage();
        if (previousPage != null) {
            currentPage = previousPage;

            return "The user " + getUsername()
                    + " has navigated successfully to the previous page.";
        }
        return "There are no pages left to go back.";
    }

    /**
     * Change the user's page to the next one from their history.
     *
     * @return the output of the command
     */
    public String nextPage() {
        Page nextPage = pageHistory.getNextPage();
        if (nextPage != null) {
            currentPage = nextPage;
            return "The user " + getUsername() + " has navigated successfully to the next page.";
        }
        return "There are no pages left to go forward.";
    }

    /**
     * Update the recommendations of the user.
     *
     * @param cmd the command that triggered the update
     * @return true if the recommendations were updated successfully, false otherwise
     */
    public boolean updateRecommendations(final CommandInput cmd) {
        String recommendationType = cmd.getRecommendationType();

        if (recommendationType.equals("random_song")) {
            int listenedTime = player.getCurrentAudioFile().getDuration() - player.getSource()
                    .getDuration();
            if (listenedTime < homePage.getLimitDuration()) {
                // The playing song will be the recommendation.
                return homePage.updateSongRecommendation((Song) player.getCurrentAudioFile());
            } else {
                // The recommendation will be a random song from the same genre as the playing song.
                List<Song> songsbyGenre = Admin.getInstance()
                        .getSongsByGenre(((Song) (player.getCurrentAudioFile())).getGenre());
                Random random = new Random(listenedTime);
                int idx = random.nextInt(songsbyGenre.size());
                return homePage.updateSongRecommendation(songsbyGenre.get(idx));
            }
        }

        if (recommendationType.equals("random_playlist")) {
            ArrayList<String> top3Genres = getTop3Genres();
            Playlist playlistRandom = new Playlist(getUsername() + "'s recommendations",
                    getUsername(), cmd.getTimestamp());

            // Create a playlist with songs from the top 3 genres.
            // The first genre will have 5 songs, the second 3 and the third 2.
            int kon = homePage.getLimit();
            for (int i = 0; i < top3Genres.size(); i++) {
                List<Song> list = Admin.getInstance().createPlaylistByGenre(top3Genres.get(i), kon);
                kon -= 2;
                if (list != null) {
                    playlistRandom.getSongs().addAll(list);
                }
            }
            return homePage.updatePlaylistRecommendation(playlistRandom, recommendationType);
        }

        if (recommendationType.equals("fans_playlist")) {
            String artistCurrent = ((Song) player.getCurrentAudioFile()).getArtist();
            Artist artist = Admin.getInstance().getArtist(artistCurrent);

            // In order to ease the work, we will use the wrapped of the artist,
            // because the wrapped can determine the top 5 fans.
            WrappedArtist wrappedArtist = (WrappedArtist) artist.getWrapped();
            wrappedArtist.updateWrappedForOutput();
            ArrayList<String> top5Fans = wrappedArtist.getTopFans();

            // Create a playlist with the 5 most liked songs of the top 5 fans.
            Playlist playlistFans = new Playlist(artistCurrent + " Fan Club recommendations",
                    getUsername(), cmd.getTimestamp());

            for (String fan : top5Fans) {
                User user = Admin.getInstance().getUser(fan);
                playlistFans.getSongs().addAll(user.getTop5LikedSongs());
            }

            return homePage.updatePlaylistRecommendation(playlistFans, recommendationType);
        }
        return false;
    }

    /**
     * Get the top 5 liked songs (by number of likes) of the user.
     *
     * @return the ArrayList with the top 5 liked songs
     */
    public ArrayList<Song> getTop5LikedSongs() {
        ArrayList<Song> songs = new ArrayList<>(this.likedSongs);

        // Sort the songs by number of likes in descending order.
        songs.sort(Comparator.comparingInt(Song::getLikes).reversed());

        // Return the first 5 songs.
        return songs.stream().limit(homePage.getLimit())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get the top 3 genres of the user regarding the songs that the user has in their liked songs,
     * followed playlists and created playlists.
     *
     * @return the ArrayList with the top 3 genres
     */
    public ArrayList<String> getTop3Genres() {
        // Create a Map for each type of songs (liked, followed playlists, created playlists)
        // and group them by genre.
        Map<String, List<Song>> collectLikedSongs = likedSongs.stream()
                .collect(Collectors.groupingBy(Song::getGenre));
        Map<String, List<Song>> collectFollowedPlaylists = followedPlaylists.stream()
                .flatMap(playlist -> playlist.getSongs().stream())
                .collect(Collectors.groupingBy(Song::getGenre));
        Map<String, List<Song>> collectCreatedPlaylists = playlists.stream()
                .flatMap(playlist -> playlist.getSongs().stream())
                .collect(Collectors.groupingBy(Song::getGenre));

        // Merge the 3 Maps into one Map.
        Map<String, List<Song>> collectAll = new HashMap<>(collectLikedSongs);
        for (Map.Entry<String, List<Song>> entry : collectFollowedPlaylists.entrySet()) {
            if (collectAll.containsKey(entry.getKey())) {
                collectAll.get(entry.getKey()).addAll(entry.getValue());
            } else {
                collectAll.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, List<Song>> entry : collectCreatedPlaylists.entrySet()) {
            if (collectAll.containsKey(entry.getKey())) {
                collectAll.get(entry.getKey()).addAll(entry.getValue());
            } else {
                collectAll.put(entry.getKey(), entry.getValue());
            }
        }

        // Sort the Map by the number of songs in each genre in ascending order
        // and limit them to 3 elements.
        Stream<Map.Entry<String, List<Song>>> all = collectAll.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getValue().size()))
                .limit(homePage.getLimitGenre());

        // Get only the keys from the resulted Stream.
        ArrayList<String> top3Genres = all.map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
        return top3Genres;
    }

    /**
     * Load the last recommendation of the user.
     *
     * @return the output of the command
     */
    public String loadRecommendation() {
        if (!status) {
            return "%s is offline.".formatted(getUsername());
        }

        if (homePage.getLastRecommendation().equals("none")) {
            return "No recommendations available.";
        }

        String lastRecommendation = homePage.getLastRecommendation();
        if (lastRecommendation.equals("random_song")) {
            player.setSource(homePage.getSongRecommendations().get(0), "song");
        } else if (lastRecommendation.equals("random_playlist")) {
            player.setSource(homePage.getPlaylistRecommendations().get(0), "playlist");
        } else if (lastRecommendation.equals("fan_playlist")) {
            player.setSource(homePage.getPlaylistRecommendations().get(1), "playlist");
        }

        searchBar.clearSelection();

        // Need to update the histories for Wrapped and Monetization.
        this.updateWrapped(player.getSource());
        this.updateHistoryForMonetization();

        player.pause();

        return "Playback loaded successfully.";
    }

    /**
     * Buys a merch from the current page's content creator.
     *
     * @param merchName the name of the merch to be bought
     * @return the output of the command
     */
    public String buyMerch(final String merchName) {
        if (!currentPage.getPageType().equals("artistPage")) {
            return "Cannot buy merch from this page.";
        }

        ArtistPage page = (ArtistPage) currentPage;
        Merchandise merch = page.getMerchandise(merchName);
        if (merch == null) {
            return "The merch " + merchName + " doesn't exist.";
        }
        boughtMerch.add(merch.getName());

        // We don't have access to the artist from the page,
        // so we have to search for it in the list with all the artists.
        for (Artist artist : Admin.getInstance().getArtists()) {
            if (artist.getPage() == currentPage) {
                artist.buyMerch(merch);
                return getUsername() + " has added new merch successfully.";
            }
        }
        return "Artist not found for the bought merch";
    }

    /**
     * Buys a premium plan for the user.
     *
     * @return output of the command
     */
    public String buyPremium() {
        return getUsername() + userPlan.setType(UserPlan.PlanType.PREMIUM);
    }

    /**
     * Cancels the premium plan for the user.
     *
     * @return output of the command
     */
    public String cancelPremium() {
        return getUsername() + userPlan.setType(UserPlan.PlanType.BASIC);
    }

    /**
     * Updates the history for monetization based on whether the user has a Premium or Basic plan.
     */
    public void updateHistoryForMonetization() {
        if (player.getType().equals("podcast")) {
            return;
        }

        if (userPlan.getType() == UserPlan.PlanType.PREMIUM) {
            userPlan.addSongToHistoryPremium((Song) player.getCurrentAudioFile());
        } else {
            userPlan.addSongToHistoryBasic((Song) player.getCurrentAudioFile());
        }
    }


    /**
     * Updates the user's player that there will be an Ad to be played.
     *
     * @param price the price of the Ad
     * @return output of the command
     */
    public String adBreak(final int price) {
        if (userPlan.getType() == UserPlan.PlanType.PREMIUM) {
            return "Ad break unavailable.";
        }
        if (player.getCurrentAudioFile() == null || player.getType().equals("podcast")) {
            return getUsername() + " is not playing any music.";
        }

        player.updateAdBreakIncoming(price);
        return "Ad inserted successfully.";
    }

}
