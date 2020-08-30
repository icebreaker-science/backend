package science.icebreaker.network;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PaperRepository extends JpaRepository<Paper, Integer> {

    @Query("select p from Paper p where p.icebreakerId in ?1")
    List<Paper> findAllByIds(Iterable<Integer> integers);

}
