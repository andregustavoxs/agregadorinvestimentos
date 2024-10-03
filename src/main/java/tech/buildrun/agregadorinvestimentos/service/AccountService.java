package tech.buildrun.agregadorinvestimentos.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.buildrun.agregadorinvestimentos.controller.dto.AssociateAccountStockDto;
import tech.buildrun.agregadorinvestimentos.entity.AccountStock;
import tech.buildrun.agregadorinvestimentos.entity.AccountStockId;
import tech.buildrun.agregadorinvestimentos.repository.AccountRepository;
import tech.buildrun.agregadorinvestimentos.repository.AccountStockRepository;
import tech.buildrun.agregadorinvestimentos.repository.StockRepository;

import java.util.UUID;

@Service
public class AccountService {
    private AccountRepository accountRepository;
    private StockRepository stockRepository;
    private AccountStockRepository accountStockRepository;

    public AccountService(AccountRepository accountRepository, StockRepository stockRepository, AccountStockRepository accountStockRepository) {
        this.accountRepository = accountRepository;
        this.stockRepository = stockRepository;
        this.accountStockRepository = accountStockRepository;
    }

    public void associateStock(String accountId, AssociateAccountStockDto associateAccountStockDto) {
        var account = accountRepository.findById(UUID.fromString(accountId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var stock = stockRepository.findById(associateAccountStockDto.stockId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // DTO -> ENTITY
        var id = new AccountStockId(account.getAccountId(), stock.getStockId());
        var entity = new AccountStock(
                id,
                account,
                stock,
                associateAccountStockDto.quantity()
        );

        accountStockRepository.save(entity);
    }
}
