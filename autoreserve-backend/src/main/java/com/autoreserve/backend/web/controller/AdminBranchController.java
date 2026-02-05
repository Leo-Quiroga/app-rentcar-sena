package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Branch;
import com.autoreserve.backend.domain.repository.BranchRepository;
import com.autoreserve.backend.dto.branch.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/branches")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBranchController {

    private final BranchRepository branchRepository;

    public AdminBranchController(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @GetMapping
    public ResponseEntity<List<BranchResponse>> getAllBranches() {
        List<Branch> branches = branchRepository.findAll();
        
        List<BranchResponse> responses = branches.stream()
                .map(branch -> new BranchResponse(
                        branch.getId(),
                        branch.getName(),
                        branch.getAddress(),
                        branch.getCity(),
                        branch.getPhone(),
                        branch.getImage(),
                        branch.getCars() != null ? branch.getCars().size() : 0
                ))
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchResponse> getBranchById(@PathVariable Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));
        
        BranchResponse response = new BranchResponse(
                branch.getId(),
                branch.getName(),
                branch.getAddress(),
                branch.getCity(),
                branch.getPhone(),
                branch.getImage(),
                branch.getCars() != null ? branch.getCars().size() : 0
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createBranch(@Valid @RequestBody BranchRequest request) {
        try {
            Branch branch = new Branch();
            branch.setName(request.getName());
            branch.setAddress(request.getAddress());
            branch.setCity(request.getCity());
            branch.setPhone(request.getPhone());
            branch.setImage(request.getImage());

            Branch saved = branchRepository.save(branch);

            BranchResponse branchData = new BranchResponse(
                    saved.getId(),
                    saved.getName(),
                    saved.getAddress(),
                    saved.getCity(),
                    saved.getPhone(),
                    saved.getImage(),
                    0
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Sede creada exitosamente",
                    "data", branchData
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchResponse> updateBranch(@PathVariable Long id, @Valid @RequestBody BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setPhone(request.getPhone());
        branch.setImage(request.getImage());

        Branch updated = branchRepository.save(branch);

        BranchResponse response = new BranchResponse(
                updated.getId(),
                updated.getName(),
                updated.getAddress(),
                updated.getCity(),
                updated.getPhone(),
                updated.getImage(),
                updated.getCars() != null ? updated.getCars().size() : 0
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable Long id) {
        try {
            Branch branch = branchRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Sede no encontrada con ID: " + id));

            if (branch.getCars() != null && !branch.getCars().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "error", "No se puede eliminar una sede que tiene autos asociados"));
            }

            branchRepository.delete(branch);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Sede eliminada exitosamente",
                    "deletedId", id
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", "Error interno del servidor", "details", e.getMessage()));
        }
    }
}