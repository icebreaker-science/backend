package science.icebreaker.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import science.icebreaker.dao.entity.WikiPage;

import java.util.List;

public interface WikiPageRepository extends JpaRepository<WikiPage, Integer> {

    List<WikiPage> findAllByType(WikiPage.PageType type);

}
