package com.dgd.model.repo;

import com.dgd.model.entity.Good;
import com.dgd.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodRepository extends JpaRepository<Good, Long> {

    List<Good> findAllByUser(User user);

    Page<Good> findByTitleContaining(String keyword, Pageable pageable);
}