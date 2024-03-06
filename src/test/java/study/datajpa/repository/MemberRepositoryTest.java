package study.datajpa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testMember() {

        System.out.println("memberRepository : " + memberRepository.getClass());

        Member member = new Member("tester");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertEquals(findMember.getId(), member.getId());
        Assertions.assertEquals(findMember.getUsername(), member.getUsername());
        Assertions.assertEquals(findMember, member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertEquals(findMember1, member1);
        assertEquals(findMember2, member2);

        findMember1.setUsername("daeun");

        // 리스트 조회 검증
        List<Member> members = memberRepository.findAll();
        assertEquals(members.size(), 2);

        // 카운트 검증
        long count = memberRepository.count();
        assertEquals(count, 2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long delteCount = memberRepository.count();
        assertEquals(delteCount, 0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member member1 = new Member("tester1", 10);
        Member member2 = new Member("tester1", 20);
        Member savedMember1 = memberRepository.save(member1);
        Member savedMember2 = memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(savedMember1.getId()).get();
        Member findMember2 = memberRepository.findById(savedMember2.getId()).get();

        System.out.println("findMember1 = " + findMember1);
        System.out.println("findMember2 = " + findMember2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("tester1", 15);

        assertEquals(result.get(0).getUsername(), findMember2.getUsername());
        assertEquals(result.get(0).getAge(), findMember2.getAge());
        assertEquals(result.size(), 1);
    }

    @Test
    public void findHelloBy() {
        List<Member> members = memberRepository.findTop3HelloBy();

    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("tester1", 10);
        Member member2 = new Member("tester2", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByUsername(member1.getUsername());
        assertEquals(member1, members.get(0));
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("tester1", 10);
        Member member2 = new Member("tester1", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findUser(member1.getUsername(), member1.getAge());
        assertEquals(member1, members.get(0));
    }

    @Test
    public void testUserNameList() {
        Member member1 = new Member("tester1", 10);
        Member member2 = new Member("tester1", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernames = memberRepository.findUsernameList();
        assertEquals(member1.getUsername(), usernames.get(0));
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("member1", 10);
        member1.setTeam(team);
        memberRepository.save(member1);

        List<MemberDto> members = memberRepository.findMemberDto();
        members.forEach(System.out::println);
    }

    @Test
    public void findByNames() {
        Member member1 = new Member("tester1", 10);
        Member member2 = new Member("tester2", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByNames(
            Arrays.asList(member1.getUsername(), member2.getUsername()));

        members.forEach(System.out::println);
    }

    @Test
    public void returnType() {
        Member member1 = new Member("member1", 10);
        Member member2 = new Member("member1", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 컬렉션에 없는 값이 들어오면 빈 리스트가 반환됨(컬렉션은 null이 아님)
        List<Member> findMemberList = memberRepository.findListByUsername("sadfsa");
        System.out.println(findMemberList);

        // 객체는 없는 값이 들어오면 null
        Member findMember = memberRepository.findMemberByUsername("sadfsa");
        System.out.println(findMember);

        // Optional은 없는 값이 들어오면 Optional.empty
        Optional<Member> findOptionalMember = memberRepository.findOptionalByUsername("member1");
        System.out.println(findOptionalMember);
    }
}