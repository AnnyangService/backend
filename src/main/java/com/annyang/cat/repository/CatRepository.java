package com.annyang.cat.repository;

import com.annyang.cat.entity.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CatRepository extends JpaRepository<Cat, String> {
    List<Cat> findByMemberId(String memberId);
} 