package com.example.monster.contents.repository;

import com.example.monster.contents.entity.Content;
import com.example.monster.contents.entity.Replies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepliesJpaRepository extends JpaRepository<Replies, Long> {


    @Override
    Optional<Replies> findById(Long aLong);

    @Override
    void delete(Replies entity);
}
