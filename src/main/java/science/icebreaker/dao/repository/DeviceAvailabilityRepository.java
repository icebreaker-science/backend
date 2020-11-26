package science.icebreaker.dao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.DeviceAvailability;

@Repository
public interface DeviceAvailabilityRepository extends JpaRepository<DeviceAvailability, Integer> {
    List<DeviceAvailability> findByDeviceId(Integer deviceId);
    Optional<DeviceAvailability> findByIdAndDisabledFalse(Integer id);
    Long deleteByIdAndAccount(Integer id, Account account);
}
