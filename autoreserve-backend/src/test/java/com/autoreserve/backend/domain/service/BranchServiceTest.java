package com.autoreserve.backend.domain.service;

import com.autoreserve.backend.domain.entity.Branch;
import com.autoreserve.backend.domain.repository.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private BranchService branchService;

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
    void save_ReturnsSavedBranch() {
        when(branchRepository.save(any(Branch.class))).thenReturn(testBranch);

        Branch result = branchService.save(testBranch);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sede Central", result.getName());
        verify(branchRepository, times(1)).save(testBranch);
    }

    @Test
    void findAll_ReturnsAllBranches() {
        when(branchRepository.findAll()).thenReturn(List.of(testBranch));

        List<Branch> result = branchService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sede Central", result.get(0).getName());
        verify(branchRepository, times(1)).findAll();
    }

    @Test
    void findById_ExistingBranch_ReturnsOptional() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(testBranch));

        Optional<Branch> result = branchService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(branchRepository, times(1)).findById(1L);
    }

    @Test
    void findById_NonExistingBranch_ReturnsEmpty() {
        when(branchRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Branch> result = branchService.findById(999L);

        assertFalse(result.isPresent());
        verify(branchRepository, times(1)).findById(999L);
    }

    @Test
    void deleteById_CallsRepository() {
        doNothing().when(branchRepository).deleteById(1L);

        branchService.deleteById(1L);

        verify(branchRepository, times(1)).deleteById(1L);
    }
}
