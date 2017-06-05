package net.test.repository;

import net.test.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Zbynek Vavros (zbynek.vavros@i.cz)
 */
public interface AddressRepository extends JpaRepository<Address, Long> {
}
