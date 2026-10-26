# 튜터 목록 조회 필터 및 URL 가이드

작성 기준: 2026-10-26 현재 로컬 구현 코드. 배포 서버 반영 여부는 별도 확인이 필요하다.

## 1. 요청 방식

- 메서드와 경로: `GET /tutors`
- 검색 조건은 **Query Parameter**로 전달한다. Request Body는 사용하지 않는다.
- 로컬 기본 주소: `http://localhost:8080`
- 배포 시에는 기본 주소만 실제 백엔드 서버 주소로 변경한다.
- 모든 검색 조건은 선택 사항이다. 선택하지 않은 조건은 URL에서 생략한다.
- 현재 사용자 ID는 Controller에서 **1로 고정**되어 있다. DB에 해당 사용자가 있어야 하며, 없으면 `USER_NOT_FOUND`가 반환된다. 사용자 ID를 쿼리로 바꾸는 기능은 없다.

```http
GET /tutors
GET /tutors?search=Rina
GET /tutors?weekdays=MON&timeSlots=TIME_09_12
```

### 화면 URL과 API URL의 차이

여기서 설명하는 URL은 프론트가 호출하는 **백엔드 API URL**이다. 필터 선택 시 브라우저 주소창을 변경할지는 프론트에서 별도로 결정한다. 주소창은 그대로 두고 API 요청 URL만 변경해도 된다. 실제 호출은 개발자 도구의 Network → Fetch/XHR에서 확인한다.

## 2. 공통 필터 규칙

| 항목 | 현재 동작 |
| --- | --- |
| 서로 다른 필터 | AND: 모두 만족해야 한다. |
| 같은 필터의 여러 선택값 | OR: 하나 이상 만족하면 된다. |
| 배열 전달 | 같은 키를 반복한다. 예: `genders=MALE&genders=FEMALE` |
| enum 값 | 코드에 정의된 대문자 값을 사용한다. 한국어 표시명을 보내지 않는다. |
| boolean 필터 | 구현된 필터는 `true`일 때만 적용한다. `false` 또는 생략은 미적용이다. |
| 선택 해제 | 해당 키 또는 선택값을 URL에서 제거한다. |
| 빈 검색어 | 빈 문자열 또는 공백만 있으면 검색어 조건을 적용하지 않는다. |
| 빈 목록 | 서버의 null·빈 컬렉션은 미적용이다. 프론트에서는 `weekdays=` 같은 빈 값 대신 키 자체를 생략한다. |
| 기본 정렬 | 튜터 ID 오름차순 |
| 페이지네이션 | 현재 없음. 검색 결과 전체를 반환한다. `page`, `size`, `sort` 파라미터는 구현되어 있지 않다. |

`isMyTutor=false`는 “찜하지 않은 튜터만”이라는 뜻이 아니다. 찜 여부와 관계없이 조회한다.

## 3. 현재 구현된 필터

아래 URL은 백엔드 기본 주소 뒤에 붙이는 경로다.

| 파라미터 | 의미 및 실제 조회 대상 | URL 예시                                  |
| --- | --- |-----------------------------------------|
| `search` | 튜터 이름 **또는 상세 전공**에 검색어 포함 | `/tutors?search=jane`                   |
| `isMyTutor` | 현재 사용자(1번)가 찜한 튜터 | `/tutors?isMyTutor=true`                |
| `isPreviousTutor` | 현재 사용자와 `COMPLETED` 수업이 있는 튜터 | `/tutors?isPreviousTutor=true`          |
| `isPointback` | `Tutor.pointback=true`인 튜터 | `/tutors?isPointback=true`              |
| `strengthTags` | 튜터의 단일 수업 강점 `strength`가 선택값 중 하나에 해당 | `/tutors?strengthTags=INTERVIEW`        |
| `experienceTags` | 튜터의 단일 대표 경험 `primaryKeyword`가 선택값 중 하나에 해당 | `/tutors?experienceTags=IT_DEVELOPMENT` |
| `fieldOfStudy` | 전공 계열 일치. 상세 전공 문자열 검색과는 별도 | `/tutors?fieldOfStudy=ENGINEERING`      |
| `personalInterestTags` | 연결된 개인 관심사 중 선택한 관심사가 하나 이상 존재 | `/tutors?personalInterestTags=TRAVEL`   |
| `genders` | 성별 일치 | `/tutors?genders=FEMALE`                |
| `accents` | 발음 일치. 국적과는 별도 | `/tutors?accents=AMERICAN`              |
| `weekdays` | 선택한 요일에 시작하는 미래 `OPEN` 슬롯이 존재 | `/tutors?weekdays=MON`                  |
| `timeSlots` | 선택한 시간대에 시작하는 미래 `OPEN` 슬롯이 존재 | `/tutors?timeSlots=TIME_09_12`          |

검색어는 앞뒤 공백을 제거하고 소문자로 비교한다. `%`, `_`는 와일드카드가 아닌 검색 문자로 처리한다. 공백·한글·특수문자는 URL 인코딩해서 전송한다.

```http
GET /tutors?search=Computer%20Science
```

### 추천 필터: `isFeatured`

`isFeatured=true`이면 **평점 4.5 이상, 리뷰 수 5개 이상, 수락률 90% 이상**을 모두 만족하는 튜터만 조회한다. 평점 또는 수락률이 `null`이면 제외한다. `false`이거나 생략하면 추천 조건을 적용하지 않는다.

예: `GET /tutors?isFeatured=true&weekdays=MON&timeSlots=TIME_09_12`는 추천 기준을 만족하면서 월요일 09~12시 사이에 시작하는 미래 OPEN 슬롯이 있는 튜터를 조회한다. 추천 조건 자체는 슬롯을 요구하지 않으며, 다른 필터와 AND로 결합한다. 추천 여부는 기존 필드로 조회 시 계산하므로 엔티티에 별도 필드를 저장하지 않는다.

## 4. enum 선택값

| 파라미터 | 허용값 |
| --- | --- |
| `strengthTags` | `BEGINNER_ENGLISH`, `BUSINESS_ENGLISH`, `INTERVIEW`, `ENGLISH_EXAM`, `WRITING`, `ADVANCED_DISCUSSION` |
| `experienceTags` | `MARKETING_SALES`, `STRATEGY_PLANNING`, `IT_DEVELOPMENT`, `FINANCE_ACCOUNTING`, `LAW_POLICY`, `MEDICAL_BIO`, `ARTS_MEDIA`, `EDUCATION`, `SERVICE`, `GRADUATE_SCHOOL` |
| `fieldOfStudy` | `SOCIAL_SCIENCE`, `HUMANITIES`, `BUSINESS`, `NATURAL_SCIENCE`, `ENGINEERING`, `MEDICINE`, `LAW`, `ARTS` |
| `personalInterestTags` | `TRAVEL`, `MUSIC`, `SPORTS`, `CULTURE`, `GAME`, `TECH_IT`, `FASHION_BEAUTY`, `SOCIAL_MEDIA`, `CAREER_DEVELOPMENT`, `ECONOMY_INVESTMENT` |
| `genders` | `MALE`, `FEMALE` |
| `accents` | `AMERICAN`, `BRITISH` |
| `weekdays` | `SUN`, `MON`, `TUE`, `WED`, `THU`, `FRI`, `SAT` |

### 시간대 코드

| `timeSlots` 값 | 수업 시작 시각 범위 |
| --- | --- |
| `TIME_00_03` | 00:00 이상, 03:00 미만 |
| `TIME_05_07` | 05:00 이상, 07:00 미만 |
| `TIME_07_09` | 07:00 이상, 09:00 미만 |
| `TIME_09_12` | 09:00 이상, 12:00 미만 |
| `TIME_12_15` | 12:00 이상, 15:00 미만 |
| `TIME_19_21` | 19:00 이상, 21:00 미만 |
| `TIME_21_24` | 21:00 이상, 다음 날 00:00 미만 |

03:00~05:00, 15:00~19:00을 선택하는 코드는 현재 없다. 시간 필터를 생략하면 이 구간의 슬롯도 다른 조건에 따라 검색될 수 있다.

## 5. 요일·시간대의 상세 동작

요일 또는 시간대 중 하나라도 선택하면 다음 조건을 적용한다.

1. 슬롯 상태가 `OPEN`이다. `BOOKED`, `CLOSED`는 제외한다.
2. 슬롯 시작 시각이 현재 시각보다 **엄격히 이후**다. 이미 시작했거나 현재 시각과 같은 슬롯은 제외한다.
3. 저장된 슬롯 시각을 **한국 시간(Asia/Seoul)**으로 해석한다. 현재 사용자의 `timeZone`에 따라 자동 변환하지 않는다.
4. 검색 기간 상한은 없다. DB에 저장된 미래 슬롯 전체가 대상이다.
5. 요일과 시간대를 함께 선택하면 **동일한 슬롯**이 두 조건을 모두 만족해야 한다.
6. 일치하는 슬롯이 여러 개 있어도 튜터는 한 번만 반환한다.

```http
GET /tutors?weekdays=MON&timeSlots=TIME_09_12
```

| 미래 슬롯 예시 | 위 요청의 검색 대상 여부 |
| --- | --- |
| 월요일 09:00~09:40, OPEN | 포함 |
| 월요일 11:40~12:20, OPEN | 포함. 종료가 아니라 시작 시각 기준이다. |
| 월요일 12:00~12:40, OPEN | 제외. 종료 경계는 포함하지 않는다. |
| 월요일 09:00~09:40, BOOKED | 제외 |
| 월요일 저녁 슬롯과 화요일 오전 슬롯만 보유 | 제외. 서로 다른 슬롯의 조건을 합치지 않는다. |

`TIME_21_24`는 시작일의 21시부터 자정 전까지다. 월요일 23:40에 시작해 화요일에 끝나는 슬롯은 월요일 밤에 해당하고, 화요일 00:00에 시작하면 화요일 새벽에 해당한다.

요일만 선택하면 해당 요일의 모든 시간대를 검사한다. 시간대만 선택하면 모든 요일을 검사한다. **둘 다 생략하면 예약 가능 슬롯이 없는 튜터도 반환될 수 있다.**

## 6. 필터 선택에 따라 요청 URL이 바뀌는 예시

아래는 필터를 순서대로 선택하고 해제하는 예시다. 기존 선택은 유지하고 변경된 조건만 추가·제거한다.

| 단계 | 사용자 동작 | API 요청 URL |
| --- | --- | --- |
| 1 | 최초 조회 | `/tutors` |
| 2 | 월요일 선택 | `/tutors?weekdays=MON` |
| 3 | 화요일도 선택 | `/tutors?weekdays=MON&weekdays=TUE` |
| 4 | 오전 9~12시 선택 | `/tutors?weekdays=MON&weekdays=TUE&timeSlots=TIME_09_12` |
| 5 | 찜한 튜터 선택 | `/tutors?weekdays=MON&weekdays=TUE&timeSlots=TIME_09_12&isMyTutor=true` |
| 6 | 월요일 해제 | `/tutors?weekdays=TUE&timeSlots=TIME_09_12&isMyTutor=true` |
| 7 | 찜한 튜터 해제 | `/tutors?weekdays=TUE&timeSlots=TIME_09_12` |
| 8 | 전체 초기화 | `/tutors` |

다른 복합 조건 예시:

```http
GET /tutors?genders=FEMALE&strengthTags=INTERVIEW&strengthTags=WRITING&personalInterestTags=TRAVEL
```

의미: 여성 **AND** (인터뷰 또는 글쓰기 강점) **AND** 여행 관심사를 가진 튜터.

```http
GET /tutors?weekdays=MON&weekdays=WED&timeSlots=TIME_09_12&timeSlots=TIME_19_21
```

의미: (월요일 또는 수요일) **AND** (09~12시 또는 19~21시)에 시작하는 미래 OPEN 슬롯이 있는 튜터.

## 7. 프론트 URL 생성 예시

배열을 JSON 문자열로 보내지 말고 `append()`로 같은 키를 반복한다. 아래 코드는 URL 구성 예시이며 요청을 실행하지 않는다.

```javascript
const params = new URLSearchParams();

const search = "Computer Science";
const weekdays = ["MON", "TUE"];
const timeSlots = ["TIME_09_12"];
const isMyTutor = true;

if (search.trim()) params.set("search", search.trim());
weekdays.forEach(value => params.append("weekdays", value));
timeSlots.forEach(value => params.append("timeSlots", value));
if (isMyTutor) params.set("isMyTutor", "true");

const query = params.toString();
const path = query ? `/tutors?${query}` : "/tutors";
// 실제 호출 시 백엔드 기본 주소와 path를 조합한다.
```

`URLSearchParams`가 공백 등을 인코딩한다. URL에서 공백이 `+`로 보일 수도 있으며 `%20`과 마찬가지로 쿼리 파라미터의 공백을 나타낸다.

## 8. 응답 및 현재 제한

- `data.count`: 현재 검색 결과 전체 튜터 수. 페이지네이션은 없다.
- `data.tutors`: 튜터 ID 오름차순 목록.
- 조건에 맞는 결과가 없으면 정상 응답으로 `count: 0`, `tutors: []`를 반환한다.
- 필터가 적용되지 않아도 각 카드의 `isMyTutor`에는 1번 사용자의 실제 찜 여부를 반환한다.
- `interests`는 한국어 표시명 배열이며, `primaryKeyword`는 현재 enum 코드 문자열이다.
- 요일·시간대는 조회 조건이고, 응답에 일치한 슬롯 목록이나 가장 빠른 시간이 추가되는 것은 아니다.
- 추천 필터는 평점·리뷰 수·수락률 기준으로 적용하며, 사용자 인증 대신 사용자 ID 1을 사용한다.

```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "튜터 목록을 조회했습니다.",
  "data": {
    "count": 0,
    "tutors": []
  },
  "error": null
}
```

## 9. 구현 위치

- `TutorController`: `GET /tutors`, 쿼리 바인딩 및 임시 사용자 지정
- `TutorSearchRequest`: 파라미터 이름과 타입, Swagger 설명
- `TutorSpecifications.basicFilters()`: 필터 조합
- `TutorSpecifications.hasAvailableSlot()`: 같은 슬롯의 요일·시간대 검사
- `TutorService.getTutors()`: 조회·관심사 및 찜 정보 조합·응답 생성
- `TimeSlotFilter`, `Weekday`, 기타 enum: 허용 코드 정의

