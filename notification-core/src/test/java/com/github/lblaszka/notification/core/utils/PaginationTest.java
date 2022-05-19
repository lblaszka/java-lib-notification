package com.github.lblaszka.notification.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class PaginationTest {

    @Test
    void testUnacceptedPageSize() {
        Assertions.assertThrows( IllegalArgumentException.class, () -> Pagination.of( 0, 10 ) );
    }

    @Test
    void testUnacceptedPageNumber() {
        Assertions.assertThrows( IllegalArgumentException.class, () -> Pagination.of( 10,  -1 ) );
    }

    @Test
    void testOfConstructor() {
        final int expectedPageSize = 10;
        final int exceptedPageNumber = 20;

        Pagination pagination = Pagination.of( expectedPageSize, exceptedPageNumber );

        Assertions.assertEquals( expectedPageSize, pagination.pageSize );
        Assertions.assertEquals( exceptedPageNumber, pagination.pageNumber );
        Assertions.assertFalse( pagination.noPaging );
    }

    @Test
    void testNoPagingConstructor() {
        Pagination pagination = Pagination.noPaging();

        Assertions.assertEquals( 0, pagination.pageNumber );
        Assertions.assertEquals( 0, pagination.pageSize );
        Assertions.assertTrue( pagination.noPaging );
    }
}