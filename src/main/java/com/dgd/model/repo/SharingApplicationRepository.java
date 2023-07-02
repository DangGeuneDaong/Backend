package com.dgd.model.repo;

import com.dgd.model.entity.Good;
import com.dgd.model.entity.SharingApplication;
import com.dgd.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface SharingApplicationRepository extends JpaRepository<SharingApplication, Long> {

    List<SharingApplication> findAllByGood(Good good);

    Integer countByGood(Good good);
    boolean existsByUserAndGood(User user, Good good);

    List<SharingApplication> findAllByUser(User user);

    Optional<Good> findGoodById(Long id);

    Optional<SharingApplication> findGoodById(Long id);
}
