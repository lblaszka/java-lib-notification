package com.github.lblaszka.notification.core.utils;

public class Pagination {
    public final int pageSize;
    public final int pageNumber;
    public final boolean noPaging;

    public static Pagination of( int pageSize, int pageNumber ) {
        if( pageSize <= 0 || pageNumber < 0 )
            throw new RuntimeException("Invalid params!");

        return new Pagination( pageSize, pageNumber, false );
    }

    public static Pagination noPaging() {
        return new Pagination( -1, 0, true );
    }

    private Pagination(int pageSize, int pageNumber, boolean noPaging ) {
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.noPaging = noPaging;
    }
}
