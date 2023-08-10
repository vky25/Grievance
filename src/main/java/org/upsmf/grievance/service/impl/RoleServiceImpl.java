package org.upsmf.grievance.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.repository.RoleRepository;
import org.upsmf.grievance.service.RoleService;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Override
    public Role createRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Long roleId, String newRoleName) throws RoleNotFoundException {
        Optional<Role> optionalRole = roleRepository.findById(roleId);

        if (optionalRole.isPresent()) {
            Role role = optionalRole.get();
            role.setName(newRoleName);
            return roleRepository.save(role);
        } else {
            // Handle the case where the role with the given ID is not found
            throw new RoleNotFoundException("Role not found with ID: " + roleId);
        }
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }
}
