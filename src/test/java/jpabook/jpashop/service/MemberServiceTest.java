package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = Member.builder().name("kim").build();

        //when
        Long saveId = memberService.join(member);

        //then
        assertThat(member).isEqualTo(memberRepository.findOne(saveId));

    }

    @Test
    public void 중복_회원_예외() throws Exception {
        assertThrows(IllegalStateException.class, () -> {
            //given
            Member member1 = Member.builder().name("kim").build();
            Member member2 = Member.builder().name("kim").build();

            //when
            memberService.join(member1);
            memberService.join(member2);

            //then
            Assertions.fail("예외가 발생해야 한다.");
        });
    }

}