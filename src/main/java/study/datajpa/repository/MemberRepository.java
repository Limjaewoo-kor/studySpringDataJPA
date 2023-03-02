package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

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
}
