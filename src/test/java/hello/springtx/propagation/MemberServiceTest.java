package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest

public class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    // memberService    = @Transactional:OFF
    // memberRepository = @Transactional:ON
    // log Resposiotry  = @Transactional:ON
    @Test
    void outerTxOff_success() {
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then : 모든 데이터 정상 저장
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    // memberService    = @Transactional:OFF
    // memberRepository = @Transactional:ON
    // log Resposiotry  = @Transactional:ON Exception
    @Test
    void outerTxOff_fail() {
        // given
        String username = "로그예외_outerTxOff_success";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then : member는 잘 저장되자만, log는 런타임예외를 확인하고 rollback된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

    // memberService    = @Transactional:ON
    // memberRepository = @Transactional:OFF
    // log Resposiotry  = @Transactional:OFF
    @Test
    void singleTx() {
        // given
        String username = "singleTx";

        // when
        memberService.joinV1(username);

        // then : 모든 데이터 정상 저장
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    // memberService    = @Transactional:ON
    // memberRepository = @Transactional:ON
    // log Resposiotry  = @Transactional:ON
    @Test
    void outerTxOn_success() {
        // given
        String username = "outerTxOn_success";

        // when
        memberService.joinV1(username);

        // then : 모든 데이터 정상 저장
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    // memberService    = @Transactional:ON
    // memberRepository = @Transactional:ON
    // log Resposiotry  = @Transactional:ON Exception
    @Test
    void outerTxOn_fail() {
        // given
        String username = "로그예외_outerTxOn_fail";

        // when
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        // then : 모든 데이터 롤백
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }
}
