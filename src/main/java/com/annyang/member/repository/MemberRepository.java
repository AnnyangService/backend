package com.annyang.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.annyang.member.entity.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);
} 