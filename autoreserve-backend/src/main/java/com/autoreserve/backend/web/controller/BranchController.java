package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Branch;
import com.autoreserve.backend.domain.repository.BranchRepository;
import com.autoreserve.backend.dto.branch.BranchResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
public class BranchController {

    private final BranchRepository branchRepository;

    public BranchController(BranchRepository branchRepository) {
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
}