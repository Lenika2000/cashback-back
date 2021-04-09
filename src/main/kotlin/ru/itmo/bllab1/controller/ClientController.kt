package ru.itmo.bllab1.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.model.Cashback
import ru.itmo.bllab1.model.Client
import ru.itmo.bllab1.repo.CashbackRepository
import ru.itmo.bllab1.repo.ClientRepository
import ru.itmo.bllab1.service.UserService
import javax.persistence.EntityNotFoundException

data class MessageIdResponse(
        val message: String,
        val id: Long? = null,
)

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RequestMapping("/api/client/")
@RestController
class ClientController(
        private val clientRepository: ClientRepository,
        private val userService: UserService
) {

    @GetMapping("balance/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    fun getClientBalance(@PathVariable id: Long): Double {
        userService.checkClientAuthority(id)
        return clientRepository.findById(id).orElseThrow {
            EntityNotFoundException("Клиент с id $id не найден!")
        }.balance
    }

}
