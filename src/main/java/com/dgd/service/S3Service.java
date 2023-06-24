package com.dgd.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.dgd.exception.ApplicationErrorCode;
import com.dgd.exception.ApplicationException;
import com.dgd.model.dto.FileDetail;
import com.dgd.model.entity.Good;
import com.dgd.model.entity.User;
import com.dgd.model.repo.AmazonS3ResourceStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class S3Service {

    @Value("${spring.s3.bucket}")
    private String bucketName;

    private final AmazonS3ResourceStorage amazonS3ResourceStorage;
    private final AmazonS3Client amazonS3Client;

    /**
     * S3 이미지 한장 업로드
     * @param multipartFile
     * @return
     */
    public String uploadImage(MultipartFile multipartFile) {
        String petImage = "";



        FileDetail fileDetail = FileDetail.multiPartOf(multipartFile);
        String path = "images/"+fileDetail.getId()+"."+fileDetail.getFormat();
        amazonS3ResourceStorage.store(fileDetail.getPath(), multipartFile);

        petImage = String.valueOf(amazonS3Client.getUrl(bucketName,path));


        return petImage;
    }

    /**
     * S3 이미지 여러장 업로드
     * @param multipartFiles
     * @return
     */
    public List<String> uploadImage(List<MultipartFile> multipartFiles) {
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
     * S3 상품 이미지 삭제
     * @param good
     */
    public void deleteImage(Good good) {
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
     * S3 유저 프로필 이미지 삭제
     * @param user
     */
    public void deleteImage(User user) {
        String userProfileUrl = user.getProfileUrl();
        String keyName = userProfileUrl.substring(58);
        boolean isObjectExist = amazonS3Client.doesObjectExist(bucketName, keyName);
        if (isObjectExist){
            amazonS3Client.deleteObject(bucketName,keyName);
        } else{
            throw new ApplicationException(ApplicationErrorCode.NOT_REGISTERED_USER);
        }


    }



}
