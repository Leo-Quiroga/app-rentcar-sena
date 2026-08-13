package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Branch;
import com.autoreserve.backend.domain.repository.BranchRepository;
import com.autoreserve.backend.dto.branch.BranchRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
class AdminBranchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BranchRepository branchRepository;

    private Branch testBranch;

    @BeforeEach
    void setUp() {
        testBranch = new Branch();
        testBranch.setId(1L);
        testBranch.setName("Sede Central");
        testBranch.setAddress("Av. Principal 123");
        testBranch.setCity("Lima");
        testBranch.setPhone("987654321");
        testBranch.setCars(new ArrayList<>());
    }

    @Test
    void getAllBranches_ReturnsAllBranches() throws Exception {
        when(branchRepository.findAll()).thenReturn(List.of(testBranch));

        mockMvc.perform(get("/api/admin/branches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sede Central"));
    }

    @Test
    void getBranchById_ExistingBranch_ReturnsBranch() throws Exception {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

        mockMvc.perform(get("/api/admin/branches/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.city").value("Lima"));
    }

    @Test
    void getBranchById_NonExisting_ThrowsException() throws Exception {
        when(branchRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/branches/999"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createBranch_ValidData_ReturnsSuccess() throws Exception {
        BranchRequest request = new BranchRequest();
        request.setName("Sede Norte");
        request.setAddress("Av. Norte 456");
        request.setCity("Lima");
        request.setPhone("123456789");

        when(branchRepository.save(any(Branch.class))).thenReturn(testBranch);

        mockMvc.perform(post("/api/admin/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateBranch_ExistingBranch_ReturnsUpdated() throws Exception {
        BranchRequest request = new BranchRequest();
        request.setName("Sede Central Actualizada");
        request.setAddress("Av. Principal 999");
        request.setCity("Lima");
        request.setPhone("987654321");

        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));
        when(branchRepository.save(any(Branch.class))).thenReturn(testBranch);

        mockMvc.perform(put("/api/admin/branches/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBranch_WithNoCars_ReturnsSuccess() throws Exception {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

        mockMvc.perform(delete("/api/admin/branches/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteBranch_WithCars_ReturnsBadRequest() throws Exception {
        Branch branchWithCars = new Branch();
        branchWithCars.setId(2L);
        branchWithCars.setName("Sede Sur");
        branchWithCars.setCars(List.of(new com.autoreserve.backend.domain.entity.Car()));

        when(branchRepository.findById(2L)).thenReturn(Optional.of(branchWithCars));

        mockMvc.perform(delete("/api/admin/branches/2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void deleteBranch_NonExisting_ReturnsBadRequest() throws Exception {
        when(branchRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/admin/branches/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
