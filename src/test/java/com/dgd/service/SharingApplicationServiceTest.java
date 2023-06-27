package com.dgd.service;

import com.dgd.model.dto.SharingApplicationDto;
import com.dgd.model.entity.Good;
import com.dgd.model.entity.GoodViewCount;
import com.dgd.model.entity.SharingApplication;
import com.dgd.model.entity.User;
import com.dgd.model.repo.GoodRepository;
import com.dgd.model.repo.SharingApplicationRepository;
import com.dgd.model.repo.UserRepository;
import com.dgd.model.type.MainCategory;
import com.dgd.model.type.Status;
import com.dgd.model.type.SubCategory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
class SharingApplicationServiceTest {
    @Mock
    private GoodRepository goodRepository;

    @Mock
    private SharingApplicationRepository sharingApplicationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatRoomService chatRoomService;



    @InjectMocks
    private SharingApplicationService sharingApplicationService;

    @BeforeEach
    public void setUpTest(){
        sharingApplicationService = new SharingApplicationService(
                sharingApplicationRepository,userRepository,goodRepository,chatRoomService
        );
    }
    @Test
    void applySharing() {
    }

    @Test
    void readSharingApplicationStatus() {
        //given
        List<String> list = new ArrayList<>();
        for (int i = 0; i <3; i++) {
            list.add("image"+(i+1));
        }


        List<Good> goods = new ArrayList<>();
        List<SharingApplication> sharingApplications = new ArrayList<>();

            User givenUser = User.builder()
                    .id(1L)
                    .userId("testUser")
                    .nickName("우리집쵸비다옹")
                    .password("진짜쉬운패스워드")
                    .location("경기도 서울시 바주카포동")
                    .latitude(36.5123)
                    .longitude(123.5342)
                    .profileUrl("잘생겼을까?못생겼을까?ㅋ")
                    .build();


            Good givenGood = Good.builder()
                    .id(1L)
                    .user(givenUser)
                    .mainCategory(MainCategory.CAT)
                    .subCategory(SubCategory.SNACK)
                    .title("쵸비간식")
                    .description("우리집쵸비는살찌게아니라 털이라구요")
                    .latitude(36.5123)
                    .longitude(123.5342)
                    .status(Status.SHARING)
                    .goodImageList(list)
                    .goodViewCount(GoodViewCount.builder().goodId(1L).viewCount(1L).build())
                    .build();



        for (int i = 0; i <3; i++) {
            SharingApplication givenSharingApplication =
                    SharingApplication.builder()
                            .good(givenGood)
                            .user(givenUser)
                            .distance((double)i)
                            .requestedAt(LocalDateTime.now())
                            .content("문의사항" + (i+1))
                            .build();

            sharingApplications.add(givenSharingApplication);
        }

        Mockito.when(sharingApplicationRepository.findAllByGood(givenGood))
                .thenReturn(sharingApplications);
        Mockito.when(goodRepository.findById(1L))
                .thenReturn(Optional.of(givenGood));
        //when
        List<SharingApplicationDto.Response> responses
                = sharingApplicationService.readSharingApplicationStatus(givenGood.getId());

        for (int i = 0; i < 3; i++) {
            SharingApplicationDto.Response response = responses.get(i);
            Assertions.assertEquals(response.getDistance(), (double)i);
            Assertions.assertEquals(response.getContent(),"문의사항"+(i+1));

        }

        verify(sharingApplicationRepository).findAllByGood(givenGood);

    }
}