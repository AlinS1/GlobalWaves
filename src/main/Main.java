package main;

import app.Admin;
import app.CommandRunner;
import app.searchBar.SearchBar;
import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.CommandInput;
import fileio.input.LibraryInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     *
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1, final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(
                new File(CheckerConstants.TESTS_PATH + "library/library.json"), LibraryInput.class);
        CommandInput[] commands = objectMapper.readValue(
                new File(CheckerConstants.TESTS_PATH + filePath1), CommandInput[].class);
        ArrayNode outputs = objectMapper.createArrayNode();

        Admin admin = Admin.getInstance();
        SearchBar.updateAdmin();
        admin.setUsers(library.getUsers());
        admin.setSongs(library.getSongs());
        admin.setPodcasts(library.getPodcasts());
        CommandRunner cmdRunner = CommandRunner.getInstance();
        CommandRunner.updateAdmin();


        for (CommandInput command : commands) {
            admin.updateTimestamp(command.getTimestamp());

            String commandName = command.getCommand();

            switch (commandName) {
                case "search" -> outputs.add(cmdRunner.search(command));
                case "select" -> outputs.add(cmdRunner.select(command));
                case "load" -> outputs.add(cmdRunner.load(command));
                case "playPause" -> outputs.add(cmdRunner.playPause(command));
                case "repeat" -> outputs.add(cmdRunner.repeat(command));
                case "shuffle" -> outputs.add(cmdRunner.shuffle(command));
                case "forward" -> outputs.add(cmdRunner.forward(command));
                case "backward" -> outputs.add(cmdRunner.backward(command));
                case "like" -> outputs.add(cmdRunner.like(command));
                case "next" -> outputs.add(cmdRunner.next(command));
                case "prev" -> outputs.add(cmdRunner.prev(command));
                case "createPlaylist" -> outputs.add(cmdRunner.createPlaylist(command));
                case "addRemoveInPlaylist" ->
                        outputs.add(cmdRunner.addRemoveInPlaylist(command));
                case "switchVisibility" -> outputs.add(cmdRunner.switchVisibility(command));
                case "showPlaylists" -> outputs.add(cmdRunner.showPlaylists(command));
                case "follow" -> outputs.add(cmdRunner.follow(command));
                case "status" -> outputs.add(cmdRunner.status(command));
                case "showPreferredSongs" -> outputs.add(cmdRunner.showLikedSongs(command));
                case "getPreferredGenre" -> outputs.add(cmdRunner.getPreferredGenre(command));
                case "getTop5Songs" -> outputs.add(cmdRunner.getTop5Songs(command));
                case "getTop5Playlists" -> outputs.add(cmdRunner.getTop5Playlists(command));
                case "switchConnectionStatus" ->
                        outputs.add(cmdRunner.switchConnectionStatus(command));
                case "addUser" -> outputs.add(cmdRunner.addUser(command));
                case "deleteUser" -> outputs.add(cmdRunner.deleteUser(command));
                case "addPodcast" -> outputs.add(cmdRunner.addPodcast(command));
                case "removePodcast" -> outputs.add(cmdRunner.removePodcast(command));
                case "addAnnouncement" -> outputs.add(cmdRunner.addAnnouncement(command));
                case "removeAnnouncement" -> outputs.add(cmdRunner.removeAnnouncement(command));
                case "addAlbum" -> outputs.add(cmdRunner.addAlbum(command));
                case "removeAlbum" -> outputs.add(cmdRunner.removeAlbum(command));
                case "addEvent" -> outputs.add(cmdRunner.addEvent(command));
                case "removeEvent" -> outputs.add(cmdRunner.removeEvent(command));
                case "addMerch" -> outputs.add(cmdRunner.addMerch(command));
                case "changePage" -> outputs.add(cmdRunner.changePage(command));
                case "printCurrentPage" -> outputs.add(cmdRunner.printCurrentPage(command));
                case "getTop5Albums" -> outputs.add(cmdRunner.getTop5AlbumList(command));
                case "getTop5Artists" -> outputs.add(cmdRunner.getTop5ArtistList(command));
                case "getAllUsers" -> outputs.add(cmdRunner.getAllUsers(command));
                case "getOnlineUsers" -> outputs.add(cmdRunner.getOnlineUsers(command));
                case "showAlbums" -> outputs.add(cmdRunner.showAlbums(command));
                case "showPodcasts" -> outputs.add(cmdRunner.showPodcasts(command));
                case "wrapped" -> outputs.add(cmdRunner.wrapped(command));
                case "subscribe" -> outputs.add(cmdRunner.subscribe(command));
                case "getNotifications" -> outputs.add(cmdRunner.getNotifications(command));
                case "previousPage" -> outputs.add(cmdRunner.previousPage(command));
                case "nextPage" -> outputs.add(cmdRunner.nextPage(command));
                case "updateRecommendations" ->
                        outputs.add(cmdRunner.updateRecommendations(command));
                case "loadRecommendations" ->
                        outputs.add(cmdRunner.loadRecommendations(command));
                case "buyMerch" -> outputs.add(cmdRunner.buyMerch(command));
                case "seeMerch" -> outputs.add(cmdRunner.seeMerch(command));
                case "buyPremium" -> outputs.add(cmdRunner.buyPremium(command));
                case "cancelPremium" -> outputs.add(cmdRunner.cancelPremium(command));
                case "adBreak" -> outputs.add(cmdRunner.adBreak(command));

                default -> System.out.println("Invalid command " + commandName);
            }
        }
        outputs.add(cmdRunner.endProgram());

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePath2), outputs);

        Admin.resetInstance();
    }
}
