package com.example.card_management.controller;

import com.example.card_management.dto.TransferRequest;
import com.example.card_management.security.AuthUserDetails;
import com.example.card_management.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public Map<String, Object> transfer(@AuthenticationPrincipal AuthUserDetails me,
                                        @RequestBody TransferRequest req) {
        Long id = transferService.transferBetweenMyCards(me.getId(), req.fromId(), req.toId(), req.amount());
        return Map.of("transferId", id, "status", "SUCCESS");
    }
}

