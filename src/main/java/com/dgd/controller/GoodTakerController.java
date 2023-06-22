package com.dgd.controller;

import com.dgd.model.type.MainCategory;
import com.dgd.model.type.Status;
import com.dgd.model.type.SubCategory;
import com.dgd.service.GoodTakerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/good/taker")
@RequiredArgsConstructor
public class GoodTakerController {


    private final GoodTakerService goodTakerService;


    /**
     * 나눔 상품 상세 조회
     * @param goodId
     * @return
     */
    @GetMapping("/info")
    public ResponseEntity<?> readPerOneGood(@Valid @RequestParam Long goodId) {
        var result = goodTakerService.readPerOneGood(goodId);
        return ResponseEntity.ok(result);
    }

    /**
     * 상품 검색
     * @param keyword
     * @param minLatitude
     * @param minLongitude
     * @param maxLatitude
     * @param maxLongitude
     * @param mainCategory
     * @param subCategory
     * @param status
     * @param pageable
     * @return
     */
    @GetMapping("/search/title")
    public ResponseEntity<?> searchTitle( @RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) Double minLatitude,
                                          @RequestParam(required = false) Double minLongitude,
                                          @RequestParam(required = false) Double maxLatitude,
                                          @RequestParam(required = false) Double maxLongitude,
                                          @RequestParam(required = false) MainCategory mainCategory,
                                          @RequestParam(required = false) SubCategory subCategory,
                                          @RequestParam(required = false) Status status,
                                          @PageableDefault(sort = "id", direction = Sort.Direction.DESC )Pageable pageable){
        var result = goodTakerService.searchGoods(keyword, minLatitude,minLongitude,maxLatitude,maxLongitude,mainCategory,subCategory,status,pageable);
        System.out.println("1");
        return ResponseEntity.ok(result);
    }
}