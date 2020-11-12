package science.icebreaker.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import science.icebreaker.dao.entity.AccountRole;

import java.util.List;


public interface AccountRoleRepository extends JpaRepository<AccountRole, Integer> {

    List<AccountRole> findAllByEmail(String email);

}
