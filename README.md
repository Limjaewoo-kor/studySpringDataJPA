# studySpringDataJPA

스프링데이터JPA
Ex) 
  List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

쿼리 메소드 필터 조건 
스프링 데이터 JPA 공식 문서 참고: 
스프링 데이터 JPA가 제공하는 쿼리 메소드 기능 조회: find...By ,read...By ,query...By get...By, 
findHelloBy 처럼 ...에 식별하기 위한 내용(설명)이 들어가도 된다. 

COUNT: count...By 반환타입 long
EXISTS: exists...By 반환타입 boolean
삭제: delete...By, remove...By 반환타입 long 
DISTINCT: findDistinct, findMemberDistinctBy 
LIMIT: findFirst3, findFirst, findTop, findTop3 


네임드쿼리 — 
네임드 쿼리의 최고의 장점은 쿼리에 문법오류가 있을때 서버가 구동되지않는다.
정적메서드이기때문에 애플리케이션 로딩시점에 파싱을 해서 오류를 잡는다.
    

리포지토리 메서드에 @Query —

조회 결과가 많거나 없으면? 컬렉션 
결과 없음: 빈 컬렉션 반환 단건 조회 
결과 없음: null 반환
결과가 2건 이상: javax.persistence.NonUniqueResultException 예외 발생 
> 참고: 단건으로 지정한 메서드를 호출하면 스프링 데이터 JPA는 내부에서 JPQL의 Query.getSingleResult() 메서드를 호출한다. 
이 메서드를 호출했을 때 조회 결과가 없으면 javax.persistence.NoResultException 예외가 발생하는데 개발자 입장에서 다루기가 상당히 불편하다. 
스프링 데이터 JPA는 단건을 조회할 때 이 예외가 발생하면 예외를 무시하고 대신에 null 을 반환한다. 

- Specifications (명세)  — Criteria의 업그레이드 판
QueryDSL을 사용 권장 

- Query By Example 
QueryDSL을 사용 권장

- Projections
QueryDSL을 사용 권장

- 네이티브 쿼리 
네이티브 SQL을 DTO로 조회할 때는 JdbcTemplate or myBatis 권장 

