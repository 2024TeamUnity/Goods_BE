package com.unity.goods.domain.point.repository;

import com.unity.goods.domain.member.entity.Member;
import com.unity.goods.domain.point.entity.Point;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

  Optional<Point> findByMember(Member member);
}
