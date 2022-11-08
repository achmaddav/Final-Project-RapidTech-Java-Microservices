package com.rapidtech.walletservice.service;

import com.rapidtech.walletservice.dto.WalletRequest;
import com.rapidtech.walletservice.dto.WalletResponse;

import java.util.List;

public interface WalletService {
    List<WalletResponse> isInActive(List<String> userName);
    List<WalletRequest> getAll();
    WalletRequest cekSaldo(String userName);
    void createtWallet(WalletRequest walletRequest);
    void topUp(WalletRequest walletRequest);
    void decreaseSaldo(WalletRequest walletRequest);
}
