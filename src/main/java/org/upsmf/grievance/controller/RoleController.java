package org.upsmf.grievance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upsmf.grievance.model.Role;
import org.upsmf.grievance.model.reponse.Response;
import org.upsmf.grievance.service.RoleService;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/addrole")
    public ResponseEntity<Role> createRole(@RequestParam String roleName) {
        Role createdRole = null;
        try {
            createdRole = roleService.createRole(roleName);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
        }catch (Exception e){
            return new ResponseEntity(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<Role> updateRole(@PathVariable Long roleId, @RequestParam String newRoleName) throws RoleNotFoundException {
        Role updatedRole = null;
        try {
            updatedRole = roleService.updateRole(roleId, newRoleName);
            return ResponseEntity.ok(updatedRole);
        }catch (Exception e){
            return new ResponseEntity(new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long roleId) {
        Role role = roleService.getRoleById(roleId);
        if (role != null) {
            return ResponseEntity.ok(role);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
