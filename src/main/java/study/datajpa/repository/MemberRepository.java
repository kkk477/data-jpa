package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names")Collection<String> names);

    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    @Query(value = "select m from Member m left join m.team t"
            , countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
//    Slice<Member> findByAge(int age, Pageable pageable);

    // update는 db에 직접 날리기 때문에 영속성과 차이가 있다
    // 따라서 Modifying을 써주면 flush clear를 대신 해줌
    @Modifying
    @Query("update Member m set m.age = m.age + 1 where m.age>= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //페치 조인을 대신 해줌
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //페치 조인을 대신 해줌
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //페치 조인을 대신 해줌
    @EntityGraph(attributePaths = ("team"))
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    // DB에서 조회만하고 영속성 관리 안하는 기능
    // 모든 쿼리에 이걸 넣는다고 큰 차이가 있을까? ㄴㄴ 무거운거에나 넣는게 좋지 평소에는 별 차이없음
    // 차라리 Redis 쓰는게 나을텐데...
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
