package app.user;

import app.Admin;
import app.audio.Collections.AudioCollection;
import app.audio.Collections.Playlist;
import app.audio.Collections.PlaylistOutput;
import app.audio.Collections.Podcast;
import app.audio.Files.AudioFile;
import app.audio.Files.Song;
import app.audio.LibraryEntry;
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
import app.wrapped.WrappedArtist;
import fileio.input.CommandInput;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
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
    ArrayList<Notification> notifications = new ArrayList<Notification>();
    @Getter
    PageHistory pageHistory = new PageHistory();


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
            List<ContentCreator> contentCreatorsEntries =
                    searchBar.searchContentCreator(filters, type);

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

        if (searchBar.getLastSearchType().equals("artist")
                || searchBar.getLastSearchType().equals("host")) {
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

        // ETAPA 3
        this.updateWrapped(player.getSource());

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

        if (!player.getType().equals("playlist")
                && !player.getType().equals("album")) {
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

        return "Skipped to next track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
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

        return "Returned to previous track successfully. The current track is %s."
                .formatted(player.getCurrentAudioFile().getName());
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

    // ===================== ETAPA 3 =====================
    public void updateWrapped(PlayerSource source) {
        if (source == null || source.getAudioFile() == null)
            return;
        this.wrapped.updateWrapped(source, this);

        if (source.getType() == Enums.PlayerSourceType.LIBRARY || source.getType() == Enums.PlayerSourceType.ALBUM || source.getType() == Enums.PlayerSourceType.PLAYLIST) {
            AudioFile audioFile = (AudioFile) source.getAudioFile();
            Artist artist = Admin.getInstance().getArtist(((Song) audioFile).getArtist());
            artist.getWrapped().updateWrapped(source, this);
        }
        if (source.getType() == Enums.PlayerSourceType.PODCAST) {
            Podcast podcast = (Podcast) source.getAudioCollection();
            //System.out.println(podcast.getOwner());

            Host host = Admin.getInstance().getHost(podcast.getOwner());
            if (host != null) {
                host.getWrapped().updateWrapped(source, this);
            }
        }
    }

    public String subscribe() {

        String message = this.getUsername();
        if (searchBar.getLastContentCreatorSelected().addSubscriber(this)) {
            message += " subscribed to " + searchBar.getLastContentCreatorSelected().getUsername();
        } else {
            message += " unsubscribed from " + searchBar.getLastContentCreatorSelected().getUsername();
        }
        message += " successfully.";

        return message;
    }

    public void addNotification(String name, String description) {
        Notification notification = new Notification(name, description);
        notifications.add(notification);
    }

    public String previousPage() {
        Page previousPage = pageHistory.getPreviousPage();
        if (previousPage != null) {
            currentPage = previousPage;

//            System.out.println("previousPage");
//            getPageHistory().printHistory();

            return "The user " + getUsername() + " has navigated successfully to the previous page.";
        }
        return "There are no pages left to go back.";
    }

    public String nextPage() {
        Page nextPage = pageHistory.getNextPage();
        if (nextPage != null) {
            currentPage = nextPage;
            return "The user " + getUsername() + " has navigated successfully to the next page.";
        }
        return "There are no pages left to go forward.";
    }

    public void updateRecommendations(CommandInput cmd) {
        String recommendationType = cmd.getRecommendationType();
        if (recommendationType.equals("random_song")) {
            int listenedTime = player.getCurrentAudioFile().getDuration() - player.getSource().getDuration();
            if (listenedTime < 30) {
                homePage.updateSongRecommendation((Song) player.getCurrentAudioFile());
            } else {
                List<Song> songsbyGenre = Admin.getInstance().getSongsByGenre(((Song) (player.getCurrentAudioFile())).getGenre());
                Random random = new Random(listenedTime);
                int idx = random.nextInt(songsbyGenre.size());
                homePage.updateSongRecommendation(songsbyGenre.get(idx));
            }
            return;
        }
        if (recommendationType.equals("random_playlist")) {
            ArrayList<String> top3Genres = getTop3Genres();
            Playlist playlistRandom = new Playlist(getUsername() + "'s recommendations", getUsername(), cmd.getTimestamp());

            int kon = 5;
            for (int i = 0; i < top3Genres.size(); i++) {
                List<Song> list = Admin.getInstance().createPlaylistByGenre(top3Genres.get(i), kon);
                kon -= 2;
                if (list != null)
                    playlistRandom.getSongs().addAll(list);
            }

            homePage.updatePlaylistRecommendation(playlistRandom, recommendationType);
            //System.out.println(playlistRandom.getName() + playlistRandom);
            return;
        }
        if (recommendationType.equals("fans_playlist")) {
            String artistCurrent = ((Song) player.getCurrentAudioFile()).getArtist();
            Artist artist = Admin.getInstance().getArtist(artistCurrent);
            WrappedArtist wrappedArtist = (WrappedArtist) artist.getWrapped();
            wrappedArtist.makeFinalWrapped();
            ArrayList<String> top5Fans = wrappedArtist.getTopFans();

            Playlist playlistFans = new Playlist(artistCurrent + " Fan Club recommendations", getUsername(), cmd.getTimestamp());
            for (String fan : top5Fans) {
                User user = Admin.getInstance().getUser(fan);
                playlistFans.getSongs().addAll(user.getTop5LikedSongs());
            }
            homePage.updatePlaylistRecommendation(playlistFans, recommendationType);
            //System.out.println(playlistFans.getName() + playlistFans);
        }
    }

    public ArrayList<Song> getTop5LikedSongs() {
        ArrayList<Song> songs = new ArrayList<>(this.likedSongs);
        songs.sort(Comparator.comparingInt(Song::getLikes).reversed());
        return songs.stream().limit(5).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<String> getTop3Genres() {
        Map<String, List<Song>> collectLikedSongs = likedSongs.stream().collect(Collectors.groupingBy(Song::getGenre));
        Map<String, List<Song>> collectFollowedPlaylists = followedPlaylists.stream().flatMap(playlist -> playlist.getSongs().stream()).collect(Collectors.groupingBy(Song::getGenre));
        Map<String, List<Song>> collectCreatedPlaylists = playlists.stream().flatMap(playlist -> playlist.getSongs().stream()).collect(Collectors.groupingBy(Song::getGenre));

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

        Stream<Map.Entry<String, List<Song>>> all = collectAll.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().size())).limit(3);
        ArrayList<String> top3Genres = all.map(Map.Entry::getKey).collect(Collectors.toCollection(ArrayList::new));
        return top3Genres;
    }

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

        this.updateWrapped(player.getSource());

        player.pause();

        return "Playback loaded successfully.";
    }

}
