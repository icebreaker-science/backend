package science.icebreaker.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import science.icebreaker.dao.entity.AccountProfile;


public interface AccountProfileRepository extends JpaRepository<AccountProfile, Integer> {

}
