package com.dgd.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.dgd.exception.ApplicationErrorCode;
import com.dgd.exception.ApplicationException;
import com.dgd.model.dto.FileDetail;
import com.dgd.model.dto.GoodDto;
import com.dgd.model.dto.MatchUserDto;
import com.dgd.model.entity.Good;
import com.dgd.model.entity.GoodViewCount;
import com.dgd.model.entity.User;
import com.dgd.model.repo.*;
import com.dgd.model.type.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoodOfferService {

    @Value("${spring.s3.bucket}")
    private String bucketName;

    private final GoodRepository goodRepository;
    private final GoodViewCountRepository goodViewCountRepository;
    private final UserRepository userRepository;
    private final SharingApplicationRepository sharingApplicationRepository;
    private final AmazonS3ResourceStorage amazonS3ResourceStorage;
    private final AmazonS3Client amazonS3Client;

    /**
     * 상품 상세조회
     * @param goodId
     * @return
     */
    public GoodDto.Response readPerOneGood(Long goodId){
        Good good = goodRepository.findById(goodId)
                .orElseThrow(()-> new ApplicationException(ApplicationErrorCode.NOT_REGISTERED_GOOD));


        return good.toResponseDto(good.getUser());
    }

    /**
     * 상품 등록
     * @param form
     */
    @Transactional
    public void saveGood(GoodDto.Request form, List<MultipartFile> multipartFiles){
        User user = userRepository.findByUserId(form.getUserId())
                .orElseThrow( ()->new ApplicationException(ApplicationErrorCode.NOT_REGISTERED_USER));


        List<String> goodImages = uploadImage(multipartFiles);

        GoodViewCount goodViewCount = goodViewCountRepository.save(GoodViewCount.builder().viewCount(0L).build());
        goodRepository.save(form.toEntity(user,goodViewCount, Status.SHARING, goodImages));
    }

    /**
     * S3에 이미지 업로드 및 이미지 객채 경로들 반환
     * @param multipartFiles
     * @return
     */
    private List<String> uploadImage(List<MultipartFile> multipartFiles) {
        List<String> goodImages = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles){
            FileDetail fileDetail = FileDetail.multiPartOf(multipartFile);
            String path = "images/"+fileDetail.getId()+"."+fileDetail.getFormat();
            amazonS3ResourceStorage.store(fileDetail.getPath(), multipartFile);

            goodImages.add(String.valueOf(amazonS3Client.getUrl(bucketName,path)));

        }

        return goodImages;
    }

    /**
     * 등록한 상품조회
     * @param userId
     * @return
     */
    public List<GoodDto.MyResponseList> readGoods(String userId){
        User user = userRepository.findByUserId(userId)
                .orElseThrow( () -> new ApplicationException(ApplicationErrorCode.NOT_REGISTERED_USER));

        List<Good> goods = goodRepository.findAllByUser(user);

        List<GoodDto.MyResponseList> response = new ArrayList<>();
        for (Good good : goods){
            Integer sharingApplicationNum = sharingApplicationRepository.countByGood(good);
            response.add(good.toResponsesDto(sharingApplicationNum));
        }

        return response;
    }

    /**
     * 등록한 상품수정
     * @param form
     */
    @Transactional
    public void updateGoods(GoodDto.UpdateRequest form, List<MultipartFile> multipartFiles){
        Good good = goodRepository.findById(form.getGoodId())
                .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.NOT_REGISTERED_GOOD));

        deleteImage(good);
        List<String> goodImages = uploadImage(multipartFiles);
        good.update(form,goodImages);

        goodRepository.save(good);
    }

    /**
     * S3 이미지 삭제
     * @param good
     */
    private void deleteImage(Good good) {
        List<String> goodImageList = good.getGoodImageList();
        for( String goodImage : goodImageList){
            String keyName = goodImage.substring(58);
            boolean isObjectExist = amazonS3Client.doesObjectExist(bucketName, keyName);
            if (isObjectExist){
                amazonS3Client.deleteObject(bucketName,keyName);
            } else{
                throw new ApplicationException(ApplicationErrorCode.NOT_REGISTERED_USER);
            }

        }
    }

    /**
     * 등록한 상품 삭제
     * @param goodId
     */
    @Transactional
    public void deleteGood(Long goodId){
        Good good = goodRepository.findById(goodId)
                .orElseThrow( () -> new ApplicationException(ApplicationErrorCode.NOT_REGISTERED_USER));

        deleteImage(good);
        goodRepository.deleteById(goodId);
    }

    /**
     * 나눔 상태 변경
     * @param goodId
     */
    public void updateStatus(Long goodId) {
        Good good = goodRepository.findById(goodId)
                .orElseThrow(() -> new ApplicationException(ApplicationErrorCode.NOT_REGISTERED_GOOD));

        good.updateStatus(good.getStatus());
        goodRepository.save(good);
    }

    public boolean matchUser(MatchUserDto matchUserDto) {
        Optional<User> user = goodRepository.findUserById(matchUserDto.getGoodId());
        return matchUserDto.getUserId().equals(user.get().getUserId());
    }
}
