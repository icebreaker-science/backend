package science.icebreaker.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import science.icebreaker.dao.entity.Media;

public interface MediaRepository extends JpaRepository<Media, Integer> {
}
