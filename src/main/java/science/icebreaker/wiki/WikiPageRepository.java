package science.icebreaker.wiki;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WikiPageRepository extends JpaRepository<WikiPage, Integer> {
}
