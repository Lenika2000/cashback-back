package ru.itmo.bllab1.controller

import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.repo.Client
import ru.itmo.bllab1.repo.ClientRepository
import javax.persistence.EntityNotFoundException

data class RegisterUserRequest(
        val firstName: String,
        val lastName: String,
)

data class MessageIdResponse(
        val message: String,
        val id: Long? = null,
)

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RequestMapping("/api")
@RestController
class ClientController(
        private val clientRepository: ClientRepository,
) {

    @GetMapping("/client/{id}")
    fun getClientData(@PathVariable id: Long): Client = clientRepository.findById(id).orElseThrow {
        EntityNotFoundException("Клиент с id $id не найден!")
    }

    @GetMapping("/client/balance/{id}")
    fun getClientBalance(@PathVariable id: Long): Double = clientRepository.findById(id).orElseThrow {
        EntityNotFoundException("Клиент с id $id не найден!")
    }.balance

    @GetMapping("/clients")
    fun getClientsData(): Iterable<Client> = clientRepository.findAll()

    @PostMapping("/client/register")
    fun registerClient(@RequestBody payload: RegisterUserRequest): MessageIdResponse {
        val client = Client(0, payload.firstName, payload.lastName)
        clientRepository.save(client)
        return MessageIdResponse("Пользователь успешно зарегистрирован", client.id)
    }
}
