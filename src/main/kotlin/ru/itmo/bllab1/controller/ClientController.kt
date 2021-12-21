package ru.itmo.bllab1.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.model.*
import ru.itmo.bllab1.repo.CashbackRepository
import ru.itmo.bllab1.repo.ClientRepository
import ru.itmo.bllab1.service.UserService
import javax.persistence.EntityNotFoundException

data class MessageIdResponse(
        val message: String,
        val id: Long? = null,
)

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RequestMapping("/api/clients/")
@RestController
class ClientController(
        private val clientRepository: ClientRepository,
        private val userService: UserService,
        private val cashbackRepository: CashbackRepository,
) {

    companion object {
        fun mapCashbackDataForClient (cashback: Cashback): CashbackDataForClient =
                CashbackDataForClient(cashback.id, cashback.creationDate, cashback.shop.name,
                        cashback.productName, cashback.productPrice, cashback.status, cashback.cashbackSum)
    }

    @GetMapping("{clientId}/balance")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    fun getClientBalance(@PathVariable clientId: Long): Double {
        userService.checkClientAuthority(clientId)
        return clientRepository.findById(clientId).orElseThrow {
            EntityNotFoundException("Клиент с id $clientId не найден!")
        }.balance
    }

    @GetMapping("{clientId}/cashback")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    fun getClientCashback(@PathVariable clientId: Long): Iterable<CashbackDataForClient> {
        userService.checkClientAuthority(clientId)
        val client = clientRepository.findById(clientId).orElseThrow {
            EntityNotFoundException("Клиент с id $clientId не найден!")
        }
        return cashbackRepository.findCashbackByClient(client)
                .map { cashback: Cashback -> mapCashbackDataForClient(cashback) };
    }

}
