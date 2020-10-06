package science.icebreaker.device_availability;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceAvailabilityRepository extends JpaRepository<DeviceAvailability, Integer> {
    List<DeviceAvailability> findByDeviceId(Integer deviceId);
}
