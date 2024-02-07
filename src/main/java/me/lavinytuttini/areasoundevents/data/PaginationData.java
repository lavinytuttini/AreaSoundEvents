package me.lavinytuttini.areasoundevents.data;

public class PaginationData {
    private static PaginationData instance;
    private int currentPage;

    public PaginationData() {
        this.currentPage = 1;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void nextPage() {
        currentPage++;
    }

    public void prevPage() {
        currentPage = Math.max(1, currentPage - 1);
    }

    public static PaginationData getInstance() {
        if (instance == null) {
            instance = new PaginationData();
        }

        return instance;
    }
}
