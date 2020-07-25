package science.icebreaker.account;

import org.springframework.data.jpa.repository.JpaRepository;


interface ProfileRepository extends JpaRepository<Profile, Long> {

}