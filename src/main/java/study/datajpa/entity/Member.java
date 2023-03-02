package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id","username","age"})
@NamedQuery(
        name = "Member.findByUsername",
        query = "select m from Member m where m.username = :username"
)
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

//    protected Member() {
//    }


    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null) {
            changeTeam(team);
        }
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    //연관관계 세팅하는 메서드 -> 멤버는 팀을 변경할 수 있다.
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
