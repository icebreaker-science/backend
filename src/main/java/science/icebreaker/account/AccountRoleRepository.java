package science.icebreaker.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AccountRoleRepository extends JpaRepository<AccountRole, Integer> {

    List<AccountRole> findAllByEmail(String email);

}
