package com.cmc.classhub.admin.controller;

import com.cmc.classhub.admin.dto.InstructorAdminResponse;
import com.cmc.classhub.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

  private final AdminService adminService;

  @GetMapping("/instructors")
  public ResponseEntity<List<InstructorAdminResponse>> getAllInstructors() {
    return ResponseEntity.ok(adminService.getAllInstructorsInfo());
  }

  @PostMapping("/instructors/{instructorId}/reset-password")
  public ResponseEntity<Void> resetInstructorPassword(@PathVariable Long instructorId) {
    adminService.resetInstructorPassword(instructorId);
    return ResponseEntity.ok().build();
  }
}
