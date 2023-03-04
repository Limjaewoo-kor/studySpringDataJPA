package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    private String findMember(@PathVariable("id") long id){
        Optional<Member> byId = memberRepository.findById(id);
        Member member = byId.orElseThrow();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    private String findMember2(@PathVariable("id") Member member){
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5,sort = "username") Pageable pageable) {
        // Entity는 변경될 가능성이있기에 API에 Entity를 바로 반환하면 안된다, 반드시 DTO로 변환하여 반환할것
        // /members?page=0&size=3&sort=id,desc&sort=username,desc
        Page<Member> all = memberRepository.findAll(pageable);
        Page<MemberDto> map = all.map(member -> new MemberDto(member));
        return map;
    }

//    @PostConstruct
//    private void init() {
//        memberRepository.save(new Member("member1"));
//        memberRepository.save(new Member("member2"));
//        memberRepository.save(new Member("member3"));
//        memberRepository.save(new Member("member4"));
//        memberRepository.save(new Member("member5"));
//        memberRepository.save(new Member("member6"));
//        memberRepository.save(new Member("member7"));
//    }
}
