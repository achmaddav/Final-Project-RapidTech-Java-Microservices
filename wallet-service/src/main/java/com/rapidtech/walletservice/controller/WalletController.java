package com.rapidtech.walletservice.controller;

import com.rapidtech.walletservice.dto.WalletRequest;
import com.rapidtech.walletservice.dto.WalletResponse;
import com.rapidtech.walletservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WalletResponse> isInActive(@RequestParam List<String> userName) {
        return walletService.isInActive(userName);
    }

    @GetMapping("/getAllWallets")
    @ResponseStatus(HttpStatus.OK)
    public List<WalletRequest> getAll() {
        return walletService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createWallet(@RequestBody WalletRequest walletRequest) {
        walletService.createtWallet(walletRequest);
        return "Data wallet added";
    }

    @GetMapping("/ceksaldo")
    public WalletRequest cekSaldo(@RequestParam String userName){
        return walletService.cekSaldo(userName);
    }

    @PostMapping("/topup")
    public String topUp(@RequestBody WalletRequest walletRequest) {
        walletService.topUp(walletRequest);
        return "TopUp is succeed";
    }

    @PostMapping("/decrease")
    public void decrease(@RequestBody WalletRequest walletRequest) {
        walletService.decreaseSaldo(walletRequest);
    }
}
