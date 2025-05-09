package com.annyang.cat.service;

import com.annyang.cat.entity.Cat;
import com.annyang.cat.exception.CatNotFoundException;
import com.annyang.cat.repository.CatRepository;
import com.annyang.cat.dto.CatRegisterRequest;
import com.annyang.global.util.SecurityUtil;
import com.annyang.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CatService {
    private final CatRepository catRepository;
    private final SecurityUtil securityUtil;

    public Cat createCat(CatRegisterRequest request) {
        Member currentMember = securityUtil.getCurrentMember();
        
        Cat cat = new Cat(
            request.getName(),
            request.getImage(),
            request.getBirthDate(),
            request.getBreed(),
            request.getGender(),
            request.getWeight(),
            request.getLastDiagnosis(),
            request.getSpecialNotes(),
            currentMember
        );
        
        return catRepository.save(cat);
    }

    @Transactional(readOnly = true)
    public Cat getCat(String id) {
        return catRepository.findById(id)
                .orElseThrow(CatNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<Cat> getCurrentMemberCats() {
        Member currentMember = securityUtil.getCurrentMember();
        return catRepository.findByMemberId(currentMember.getId());
    }

    public Cat updateCat(String id, Cat catDetails) {
        Cat cat = getCat(id);
        cat.update(
            catDetails.getName(),
            catDetails.getImage(),
            catDetails.getBirthDate(),
            catDetails.getBreed(),
            catDetails.getGender(),
            catDetails.getWeight(),
            catDetails.getLastDiagnosis(),
            catDetails.getSpecialNotes()
        );
        return catRepository.save(cat);
    }

    public void deleteCat(String id) {
        Cat cat = getCat(id);
        catRepository.delete(cat);
    }
} 