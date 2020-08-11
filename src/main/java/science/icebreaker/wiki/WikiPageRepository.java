package science.icebreaker.wiki;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WikiPageRepository extends JpaRepository<WikiPage, Integer> {

    List<WikiPage> findAllByType(WikiPage.PageType type);

}
