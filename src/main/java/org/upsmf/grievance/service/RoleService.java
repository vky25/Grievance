package org.upsmf.grievance.service;

import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.Role;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

public interface RoleService {

    Role createRole(String roleName);
    Role updateRole(Long roleId, String newRoleName) throws RoleNotFoundException;

    List<Role> getAllRoles();

    Role getRoleById(Long roleId);
}
