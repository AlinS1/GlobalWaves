package app.wrapped;

import app.pages.Page;
import lombok.val;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PageHistory {
    private ArrayList<Page> pages;
    private int currentPos;

    public PageHistory() {
        pages = new ArrayList<Page>();
        currentPos = -1;
    }

    public void addPage(Page page) {
        if (currentPos < pages.size() - 1) {
            pages.subList(currentPos + 1, pages.size()).clear();
        }
        pages.add(page);
        currentPos++;
    }


    public Page getPreviousPage() {
        if (currentPos <= 0)
            return null;
        currentPos--;
        return pages.get(currentPos);
    }

    public Page getNextPage() {
        if (currentPos >= pages.size() - 1)
            return null;
        currentPos++;
        return pages.get(currentPos);
    }
}
