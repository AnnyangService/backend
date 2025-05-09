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
        
        Cat cat = new Cat();
        cat.setMember(currentMember);
        cat.setName(request.getName());
        cat.setImage(request.getImage());
        cat.setBirthDate(request.getBirthDate());
        cat.setBreed(request.getBreed());
        cat.setGender(request.getGender());
        cat.setWeight(request.getWeight());
        cat.setLastDiagnosis(request.getLastDiagnosis());
        cat.setSpecialNotes(request.getSpecialNotes());
        
        return catRepository.save(cat);
    }

    @Transactional(readOnly = true)
    public Cat getCat(String id) {
        System.out.println("getCat 호출됨");
        System.out.println("id: " + id);
        return catRepository.findById(id)
                .orElseThrow(() -> new CatNotFoundException());
    }

    @Transactional(readOnly = true)
    public List<Cat> getCurrentMemberCats() {
        Member currentMember = securityUtil.getCurrentMember();
        return catRepository.findByMemberId(currentMember.getId());
    }

    public Cat updateCat(String id, Cat catDetails) {
        Cat cat = getCat(id);
        cat.setName(catDetails.getName());
        cat.setImage(catDetails.getImage());
        cat.setBirthDate(catDetails.getBirthDate());
        cat.setBreed(catDetails.getBreed());
        cat.setGender(catDetails.getGender());
        cat.setWeight(catDetails.getWeight());
        cat.setLastDiagnosis(catDetails.getLastDiagnosis());
        cat.setSpecialNotes(catDetails.getSpecialNotes());
        return catRepository.save(cat);
    }

    public void deleteCat(String id) {
        Cat cat = getCat(id);
        catRepository.delete(cat);
    }
} 