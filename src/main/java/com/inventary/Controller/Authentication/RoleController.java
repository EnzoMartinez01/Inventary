package com.inventary.Controller.Authentication;

import com.inventary.Model.Authentication.Security.Role;
import com.inventary.Services.Authentication.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    //Get all Roles
    @GetMapping("/api/v1/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Role> getAllRoles() {
        return roleService.getRoles();
    }

    //Create a new role
    @PostMapping("/api/v1/roles/create")
    @PreAuthorize("hasRole('ADMIN')")
    public Role createRole(@RequestBody Role role) {
        return roleService.addRole(role);
    }

    //Update Role
    @PutMapping("/api/v1/roles/update/{idRole}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Role> updateRole(@PathVariable Long idRole, @RequestBody Role roleDetails) {
        try {
            Role updatedRole = roleService.updateRole(idRole, roleDetails);
            return ResponseEntity.ok(updatedRole);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //Delete role
    @DeleteMapping("/api/v1/roles/delete/{idRole}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long idRole) {
        try {
            roleService.deleteRole(idRole);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
