package org.upsmf.grievance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.upsmf.grievance.model.Role;
@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String name);
}
