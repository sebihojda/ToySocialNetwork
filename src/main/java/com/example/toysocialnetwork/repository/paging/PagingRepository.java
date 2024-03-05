package com.example.toysocialnetwork.repository.paging;

import com.example.toysocialnetwork.domain.Entity;
import com.example.toysocialnetwork.repository.RepositoryOptional;

public interface PagingRepository<ID , E extends Entity<ID>> extends RepositoryOptional<ID, E> {

    Page<E> findAll(Pageable pageable);
}
