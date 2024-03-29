package app.pages;

import app.audio.Collections.Album;
import app.user.Artist;
import app.user.objects.Event;
import app.user.objects.Merchandise;
import lombok.Getter;

import java.util.List;

/**
 * The type Artist page.
 */
public final class ArtistPage implements Page {
    private List<Album> albums;
    @Getter
    private List<Merchandise> merch;
    private List<Event> events;

    /**
     * Instantiates a new Artist page.
     *
     * @param artist the artist
     */
    public ArtistPage(final Artist artist) {
        albums = artist.getAlbums();
        merch = artist.getMerch();
        events = artist.getEvents();
    }

    @Override
    public String printCurrentPage() {
        return "Albums:\n\t%s\n\nMerch:\n\t%s\n\nEvents:\n\t%s"
                .formatted(albums.stream().map(Album::getName).toList(),
                        merch.stream().map(merchItem -> "%s - %d:\n\t%s"
                                        .formatted(merchItem.getName(),
                                                merchItem.getPrice(),
                                                merchItem.getDescription()))
                                .toList(),
                        events.stream().map(event -> "%s - %s:\n\t%s"
                                        .formatted(event.getName(),
                                                event.getDate(),
                                                event.getDescription()))
                                .toList());
    }

    @Override
    public String getPageType() {
        return "artistPage";
    }

    /**
     * Gets a merchandise that has the given name.
     *
     * @param name the name of the merchandise
     * @return the merchandise or null if it doesn't exist
     */
    public Merchandise getMerchandise(final String name) {
        for (Merchandise merchItem : merch) {
            if (merchItem.getName().equals(name)) {
                return merchItem;
            }
        }
        return null;
    }
}
