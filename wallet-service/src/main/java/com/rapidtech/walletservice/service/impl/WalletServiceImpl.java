package com.rapidtech.walletservice.service.impl;

import com.rapidtech.walletservice.dto.WalletRequest;
import com.rapidtech.walletservice.dto.WalletResponse;
import com.rapidtech.walletservice.model.Wallet;
import com.rapidtech.walletservice.repository.WalletRepository;
import com.rapidtech.walletservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    @Override
    public List<WalletResponse> isInActive(List<String> userName) {
        return walletRepository.findByUserNameIn(userName).stream()
                .map(wallet ->
                        WalletResponse.builder()
                                .userName(wallet.getUserName())
                                .isInActive(wallet.getSaldo()>0)
                                .build()).toList();
    }

    @Override
    public List<WalletRequest> getAll() {
        List<Wallet> wallets = walletRepository.findAll();
        List<WalletRequest> walletRequestList = new ArrayList<>();
        for (Wallet wallet : wallets) {
            walletRequestList.add(WalletRequest.builder()
                            .userName(wallet.getUserName())
                            .saldo(wallet.getSaldo())
                    .build());
        }
        return walletRequestList;
    }

    @Override
    public WalletRequest cekSaldo(String userName) {
        Wallet wallet = walletRepository.findByUserName(userName);
        return WalletRequest.builder()
                .userName(wallet.getUserName())
                .saldo(wallet.getSaldo())
                .build();
    }

    @Override
    public void createtWallet(WalletRequest walletRequest) {
        Wallet wallet = new Wallet();
        wallet.setUserName(walletRequest.getUserName());
        wallet.setSaldo(walletRequest.getSaldo());
        walletRepository.save(wallet);
    }
    @Transactional
    @Override
    public void topUp(WalletRequest walletRequest) {
        Wallet wallet = walletRepository.findByUserName(walletRequest.getUserName());
        if (wallet != null) {
            wallet.setSaldo(wallet.getSaldo() + walletRequest.getSaldo());
            walletRepository.save(wallet);
        } else {
            throw new RuntimeException("Username tidak cocok atau belum ada");
        }
    }
}
