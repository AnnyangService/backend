package com.annyang.cat.controller;

import com.annyang.cat.dto.CatRegisterRequest;
import com.annyang.cat.entity.Cat;
import com.annyang.cat.service.CatService;
import com.annyang.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cats")
@RequiredArgsConstructor
public class CatController {
    private final CatService catService;

    @PostMapping
    public ResponseEntity<ApiResponse<Cat>> createCat(@Valid @RequestBody CatRegisterRequest request) {
        Cat cat = catService.createCat(request);
        return ResponseEntity.ok(ApiResponse.success(cat));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Cat>>> getCurrentMemberCats() {
        List<Cat> cats = catService.getCurrentMemberCats();
        return ResponseEntity.ok(ApiResponse.success(cats));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Cat>> getCat(@PathVariable("id") String id) {
        Cat cat = catService.getCat(id);
        return ResponseEntity.ok(ApiResponse.success(cat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Cat>> updateCat(
            @PathVariable("id") String id,
            @Valid @RequestBody CatRegisterRequest request) {
        Cat cat = catService.getCat(id);
        cat.setName(request.getName());
        cat.setImage(request.getImage());
        cat.setBirthDate(request.getBirthDate());
        cat.setBreed(request.getBreed());
        cat.setGender(request.getGender());
        cat.setWeight(request.getWeight());
        cat.setLastDiagnosis(request.getLastDiagnosis());
        cat.setSpecialNotes(request.getSpecialNotes());
        
        Cat updatedCat = catService.updateCat(id, cat);
        return ResponseEntity.ok(ApiResponse.success(updatedCat));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCat(@PathVariable("id") String id) {
        catService.deleteCat(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
} 