package com.atlasrh.controller;

import java.util.Set;
import org.springframework.data.domain.Pageable;
import com.atlasrh.exception.BadRequestException;
final class Paging {
    private Paging() {}
    static Pageable check(Pageable p, Set<String> allowed) {
        for (var order : p.getSort()) if (!allowed.contains(order.getProperty())) throw new BadRequestException("Unsupported sort field: " + order.getProperty());
        return p;
    }
}
