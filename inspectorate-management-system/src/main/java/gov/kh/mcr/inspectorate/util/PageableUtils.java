package gov.kh.mcr.inspectorate.util;

import org.springframework.data.domain.*;

public final class PageableUtils {

    private PageableUtils() {}

    public static Pageable of(int page, int size,
                              String sortBy) {
        return PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, sortBy));
    }

    public static Pageable ofAsc(int page, int size,
                                 String sortBy) {
        return PageRequest.of(page, size,
                Sort.by(Sort.Direction.ASC, sortBy));
    }

    public static Pageable ofDefault(int page, int size) {
        return of(page, size, "createdAt");
    }

    public static Pageable unpaged() {
        return Pageable.unpaged();
    }
}