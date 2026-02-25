package com.autoreserve.backend.web.controller;

import com.autoreserve.backend.domain.entity.Branch;
import com.autoreserve.backend.domain.repository.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BranchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
    }

    @Test
    void getAllBranches_ReturnsAllBranches() throws Exception {
        when(branchRepository.findAll()).thenReturn(List.of(testBranch));

        mockMvc.perform(get("/api/branches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sede Central"));
    }

    @Test
    void getBranchById_ExistingBranch_ReturnsBranch() throws Exception {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

        mockMvc.perform(get("/api/branches/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Sede Central"));
    }

    @Test
    void getBranchById_NonExistingBranch_ThrowsException() throws Exception {
        when(branchRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/branches/999"))
                .andExpect(status().is4xxClientError());
    }
}
