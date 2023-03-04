package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testMember() {
        Member memberA = new Member("memberA");
        Member save = memberRepository.save(memberA);
        Optional<Member> findMember = memberRepository.findById(save.getId());
        Member member = findMember.orElseThrow();
        assertThat(member.getUsername()).isEqualTo(memberA.getUsername());
        assertThat(member).isEqualTo(memberA);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepository.save(member1);
        memberRepository.save(member2);


        // 단건조회
        Optional<Member> byId1 = memberRepository.findById(member1.getId());
        Member findMember1 = byId1.orElseThrow();
        Optional<Member> byId2 = memberRepository.findById(member2.getId());
        Member findMember2 = byId2.orElseThrow();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);


        //리스트 조회
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void findTop3By() {
        List<Member> helloBy = memberRepository.findTop3By();
    }

    @Test
    public void namedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA",10);
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> result = memberRepository.findUsernameList();
        for (String s : result) {
            System.out.println("s = "+s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.changeTeam(team);
        memberRepository.save(m1);

        List<MemberDto> result = memberRepository.findMemberDto();
        for (MemberDto dto : result) {
            System.out.println("dto = "+dto);
        }
    }

    @Test
    public void findByNNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        Optional<Member> result = memberRepository.findOptionalByUsername("AAAAA");
        System.out.println("result = " + result.orElse(null));
    }

    @Test
    public void pagingPage() {
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        int age=10;
//        int offset=1;
//        int limit=3;

        //when
        Page<Member> page = memberRepository.findPageByAge(age, pageRequest); //반환 타입이 page 면 토탈카운트까지 자동으로 같이 날림
        //슬라이스는 전체 카운트를 가져오지않고 리미트의+1해서 요청을 한다 [더보기 개념]
//        long totalCount = memberRepository.totalCount(age);

        //Page를 DTO로 변환하기.
        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

//        for (Member member : content) {
//            System.out.println("member = "+member);
//        }
//        System.out.println("totalElements = "+totalElements);

        //Page
        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);  //페이지번호
        assertThat(page.getTotalPages()).isEqualTo(2); // 페이지 개수
        assertThat(page.isFirst()).isTrue();    // 처음 페이지인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있나?


    }


    @Test
    public void pagingSlice() {
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        int age=10;

        //when
        Slice<Member> slice = memberRepository.findSliceByAge(age, pageRequest); //반환 타입이 page 면 토탈카운트까지 자동으로 같이 날림
        //슬라이스는 전체 카운트를 가져오지않고 리미트의+1해서 요청을 한다 [더보기 개념]

        //then
        List<Member> content = slice.getContent();


        //Slice
        assertThat(content.size()).isEqualTo(3);
        assertThat(slice.getNumber()).isEqualTo(0);  //페이지번호
        assertThat(slice.isFirst()).isTrue();    // 처음 페이지인가?
        assertThat(slice.hasNext()).isTrue(); //다음 페이지가 있나?
    }

    @Test
    public void bulkTest() {
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        List<Member> result = memberRepository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = "+member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        //then
        //N+1 문제 발생   from member member0_ 이후에 team0_.team_id=1; team0_.team_id=2;
        for (Member member : members) {
            System.out.println("member = "+member.getUsername());
            System.out.println("member.teamClass = "+ member.getTeam().getClass());
            System.out.println("member.team = "+ member.getTeam().getName());
        }
    }


    @Test
    public void findMemberLazyFetchJoin() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findMemberFetchJoin();

        //then
        //N+1 문제 발생   from member member0_ 이후에 team0_.team_id=1; team0_.team_id=2;
        for (Member member : members) {
            System.out.println("member = "+member.getUsername());
            System.out.println("member.teamClass = "+ member.getTeam().getClass());
            System.out.println("member.team = "+ member.getTeam().getName());
        }
    }


    @Test
    public void findMemberLazyFetchJoinEntityGraph() {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        //then
        //N+1 문제 발생   from member member0_ 이후에 team0_.team_id=1; team0_.team_id=2;
        for (Member member : members) {
            System.out.println("member = "+member.getUsername());
            System.out.println("member.teamClass = "+ member.getTeam().getClass());
            System.out.println("member.team = "+ member.getTeam().getName());
        }
    }

    //JPA 힌트

    // 전
    @Test
    public void queryHint() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        //when
        Optional<Member> findMember = memberRepository.findById(member.getId());
        Member member1 = findMember.orElseThrow();
        member1.setUsername("member2");

        em.flush();
    }

    // 후
    @Test
    public void queryHint2() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    //JPA Lock

    @Test
    public void queryLock() {
        Member member = new Member("member1", 10);
        memberRepository.save(member);
        em.flush();
        em.clear();

        //when
        List<Member> result = memberRepository.findLockByUsername("member1");

    }

}