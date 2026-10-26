package com.ceos24.mashup4backend.domain.tutor.dto;

import com.ceos24.mashup4backend.domain.interest.entity.InterestType;
import com.ceos24.mashup4backend.domain.tutor.enums.Accent;
import com.ceos24.mashup4backend.domain.tutor.enums.ExperienceTag;
import com.ceos24.mashup4backend.domain.tutor.enums.FieldOfStudy;
import com.ceos24.mashup4backend.domain.tutor.enums.Gender;
import com.ceos24.mashup4backend.domain.tutor.enums.StrengthTag;
import com.ceos24.mashup4backend.domain.tutor.enums.TimeSlotFilter;
import com.ceos24.mashup4backend.domain.tutor.enums.Weekday;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "튜터 목록 검색 조건. 생략한 조건은 적용하지 않습니다.")
public class TutorSearchRequest {

    @Schema(description = "검색어")
    private String search;

    @Schema(description = "찜한 튜터만 조회. true이면 적용")
    private Boolean isMyTutor;

    @Schema(description = "수업한 튜터만 조회. true이면 적용")
    private Boolean isPreviousTutor;

    @Schema(description = "추천 튜터만 조회. true이면 평점 4.5 이상, 리뷰 5개 이상, 수락률 90% 이상을 모두 적용. false 또는 생략 시 미적용")
    private Boolean isFeatured;

    @Schema(description = "50% 포인트백 튜터만 조회. true이면 적용")
    private Boolean isPointback;

    @Schema(description = "수업 강점. 선택한 값 중 하나에 해당하는 튜터 조회")
    private List<StrengthTag> strengthTags;

    @Schema(description = "경험 분야")
    private List<ExperienceTag> experienceTags;

    @Schema(description = "전공 계열")
    private List<FieldOfStudy> fieldOfStudy;

    @Schema(description = "개인 관심사")
    private List<InterestType> personalInterestTags;

    @Schema(description = "성별")
    private List<Gender> genders;

    @Schema(description = "발음")
    private List<Accent> accents;

    @Schema(description = "한국 시간 기준 미래 OPEN 슬롯의 시작 요일. 시간대와 같은 슬롯에서 검사")
    private List<Weekday> weekdays;

    @Schema(description = "한국 시간 기준 슬롯 시작 시간대. 시작 포함·종료 제외, 미래 슬롯 전체 대상")
    private List<TimeSlotFilter> timeSlots;
}
