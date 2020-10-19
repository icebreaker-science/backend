package science.icebreaker.device_availability;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import science.icebreaker.account.Account;

@Repository
public interface DeviceAvailabilityRepository extends JpaRepository<DeviceAvailability, Integer> {
    List<DeviceAvailability> findByDeviceId(Integer deviceId);
    Long deleteByIdAndAccount(Integer id, Account account);
}
