package app.wrapped;

import app.pages.Page;
import java.util.ArrayList;

public class PageHistory {
    private ArrayList<Page> pages;
    private int currentPos;

    public PageHistory() {
        pages = new ArrayList<Page>();
        currentPos = -1; // start on -1 because the first page will be added at position 0
    }


    /**
     * Adds a page to the history.
     *
     * @param page the page to be added to the history
     */
    public void addPage(final Page page) {
        // If the current position is not the last one, delete all the pages after the current one.
        if (currentPos < pages.size() - 1) {
            pages.subList(currentPos + 1, pages.size()).clear();
        }
        pages.add(page);
        currentPos++;
    }

    /**
     * Gets the previous page from the history.
     *
     * @return the previous page from the history
     */
    public Page getPreviousPage() {
        if (currentPos <= 0) {
            return null; // already reached the first page
        }
        currentPos--;
        return pages.get(currentPos);
    }

    /**
     * Gets the next page from the history.
     *
     * @return the next page from the history
     */
    public Page getNextPage() {
        if (currentPos >= pages.size() - 1) {
            return null; // already reached the last page
        }
        currentPos++;
        return pages.get(currentPos);
    }
}
