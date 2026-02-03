package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Branch;
import com.autoreserve.backend.domain.repository.BranchRepository;
import com.autoreserve.backend.dto.branch.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                branch.getCars() != null ? branch.getCars().size() : 0
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<BranchResponse> createBranch(@Valid @RequestBody BranchRequest request) {
        Branch branch = new Branch();
        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setPhone(request.getPhone());

        Branch saved = branchRepository.save(branch);

        BranchResponse response = new BranchResponse(
                saved.getId(),
                saved.getName(),
                saved.getAddress(),
                saved.getCity(),
                saved.getPhone(),
                0
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BranchResponse> updateBranch(@PathVariable Long id, @Valid @RequestBody BranchRequest request) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        branch.setName(request.getName());
        branch.setAddress(request.getAddress());
        branch.setCity(request.getCity());
        branch.setPhone(request.getPhone());

        Branch updated = branchRepository.save(branch);

        BranchResponse response = new BranchResponse(
                updated.getId(),
                updated.getName(),
                updated.getAddress(),
                updated.getCity(),
                updated.getPhone(),
                updated.getCars() != null ? updated.getCars().size() : 0
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBranch(@PathVariable Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada"));

        if (branch.getCars() != null && !branch.getCars().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("No se puede eliminar una sede que tiene autos asociados");
        }

        branchRepository.delete(branch);
        return ResponseEntity.ok("Sede eliminada correctamente");
    }
}