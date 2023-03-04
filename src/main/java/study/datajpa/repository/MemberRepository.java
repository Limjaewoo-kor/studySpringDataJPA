package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    //방법 1 이름으로 쿼리 만들기 [실무에서는 간단 간단한 쿼리를 짤때 사용]
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    List<Member> findTop3By();

    //방법2 네임드쿼리

//    @Query(name = "Member.findByUsername")  주석해도 가능 -> 엔티티 명 + 메서드 이름으로 네임드쿼리를 찾고, 없으면 메서드 이름으로 쿼리를 짠다[네임드 쿼리는 잘 사용하지 않음]
    List<Member> findByUsername(@Param("username") String username);

    //방법3 어노테이션 쿼리 [실무에서 정적 쿼리용으로 제일 많이 사용 -> 동적쿼리는 QueryDSL 사용] 애플리케이션 로딩시점에 파싱하여서 쿼리 오류도 잡아낸다.
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t") //DTO로 조회할때는 new operation을 사용한다. 경로를 다 써야하는 번거로움이 있다.
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")  //in 절
    List<Member> findByNames(@Param("names") List<String> names);

    //반환 타입이 유연하게 가능하다

    List<Member> findListByUsername(String username);  //컬렉션  -- 리턴값이 컬렉션일때 값이 없으면 널이 아니라 빈 컬렉션을 제공해준다.

    //단건 조회시 결과값이 2개 이상이면 예외 발생
    Member findMemberByUsername(String username); // 단건  -- 결과값이 없으면 널을 반환한다.
    Optional<Member> findOptionalByUsername(String username); // 단건 옵셔널 -- 데이터가 널일지 아닐지 모르면 그냥 옵셔널을 선택하자.

    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
    Page<Member> findPageByAge(int age, Pageable pageable);  // 카운트쿼리와 원쿼리를 분리할수있다. -- 분리하지않고 자동에 맡기면 카운트쿼리에도 조인을 넣어서 최적화에 안좋을수 있다.

    Slice<Member> findSliceByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)
    // @Modifying 이 있어야 .executeUpdate();를 호출한다 == clearAutomatically 을 넣으면 업데이트쿼리가 나간후에 엔티티메니져를 플러쉬,클리어한다.
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //JPQL + 엔티티 그래프
    //EntityGraph 와 JPQL 을 같이 쓸 수도 있다.
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //메서드 이름으로 쿼리에서 특히 편리하다.
    //이름으로 만들어주는 쿼리 사용하면서도 EntityGraph 사용할 수 있다.
//    @EntityGraph(attributePaths = {"team"})
//    List<Member> findByEntityGraphByUsername(@Param("username") String username);

    //entity 에 선언한 NamedEntityGraph 를 사용할 수도 있다.
    @EntityGraph("Member.all")
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph2();

    //JPA 힌트
    // 이렇게 할경우 스냅샷을 남기지않고 변경감지[더티체킹]을 하지 않음  -- 성능최적화를 위한 방법
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly",value = "true"))
    Member findReadOnlyByUsername(String username);

    //JPA lock
    //select 쿼리를 돌릴때 lock 을 잡기위해 for update 를 붙인다
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String name);

}
