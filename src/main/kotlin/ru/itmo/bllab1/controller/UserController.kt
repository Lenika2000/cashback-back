package ru.itmo.bllab1.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.auth.JwtUtils
import ru.itmo.bllab1.auth.UserDetailsImpl
import ru.itmo.bllab1.model.*
import ru.itmo.bllab1.repo.*
import ru.itmo.bllab1.service.UserService
import java.util.stream.Collectors
import javax.persistence.EntityNotFoundException

data class JwtResponse(
        val login: String,
        val id: Long,
        val roles: Collection<String>,
        val accessToken: String,
)

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RequestMapping("/api")
@RestController
class UserController(
        private val authenticationManager: AuthenticationManager,
        private val jwtUtils: JwtUtils,
        private val encoder: PasswordEncoder,
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository,
        private val clientRepository: ClientRepository,
        private val shopRepository: ShopRepository,
) {

    companion object {
        fun mapClientData(client: Client): ClientData =
                ClientData(client.id, client.firstName, client.lastName, client.eUser.login, client.balance)

        fun mapShopData(shop: Shop): ShopData =
                ShopData(shop.id, shop.name, shop.eUser.login)
    }

    @PostMapping("/signin")
    fun authenticateUser(@RequestBody loginRequest: LoginRequest): ResponseEntity<*>? {
        val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.login, loginRequest.password)
        )
        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtUtils.generateJwtToken(authentication)
        val userDetails = authentication.principal as UserDetailsImpl
        val user = userRepository.findByLogin(userDetails.username)
                .orElseThrow { EntityNotFoundException("Пользователь не найден") }
        val id = if (user.client !== null) user.client!!.id else if (user.shop !== null) user.shop!!.id else user.admin!!.id;
        return ResponseEntity.ok(JwtResponse(
                user.login,
                id,
                userDetails.authorities.stream()
                        .map { v -> v.authority }
                        .collect(Collectors.toList()),
                jwt,
        ))
    }

    @GetMapping("/clients")
    @PreAuthorize("hasAnyRole('ADMIN')")
    fun getClientsData(): Iterable<ClientData> = clientRepository.findAll()
            .map { client: Client -> mapClientData(client) }

    @GetMapping("/shops")
    @PreAuthorize("hasAnyRole('ADMIN')")
    fun getShopsData(): Iterable<ShopData> = shopRepository.findAll()
            .map { shop: Shop -> mapShopData(shop) }

    @PostMapping("/client/register")
    fun registerClient(@RequestBody payload: RegisterUserRequest): ResponseEntity<MessageIdResponse> {
        if (userRepository.findByLogin(payload.login).isPresent && userRepository.findByLogin(payload.login).get().client != null) {
            if (userRepository.findByLogin(payload.login).get().registrationFromWebSite)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageIdResponse("Клиент с таким логином уже зарегистрирован", 0))
            val user = userRepository.findByLogin(payload.login).get();
            user.client!!.firstName = payload.firstName;
            user.client!!.lastName = payload.lastName;
            user.registrationFromWebSite = true;
            user.password = encoder.encode(payload.password);
            userRepository.save(user);
            return ResponseEntity.ok().body(MessageIdResponse("Регистрация клиента прошла успешно", user.client!!.id))
        } else {
            val client = Client(0, payload.firstName, payload.lastName)
            val user = EUser(
                    0, payload.login, encoder.encode(payload.password), client, null, null,
                    setOf(roleRepository.findRoleByName(ERole.ROLE_CLIENT).get()), true
            )
            client.eUser = user
            userRepository.save(user)
            clientRepository.save(client)
            return ResponseEntity.ok().body(MessageIdResponse("Регистрация клиента прошла успешно", client.id))
        }
    }

    @PostMapping("/shop/register")
    fun registerShop(@RequestBody payload: RegisterShopRequest): ResponseEntity<MessageIdResponse> {
        if (userRepository.findByLogin(payload.login).isPresent && userRepository.findByLogin(payload.login).get().shop != null) {
            if (userRepository.findByLogin(payload.login).get().registrationFromWebSite)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MessageIdResponse("Магазин с таким логином уже зарегистрирован", 0))
            val user = userRepository.findByLogin(payload.login).get();
            user.shop!!.name = payload.name;
            user.registrationFromWebSite = true;
            user.password = encoder.encode(payload.password);
            userRepository.save(user);
            return ResponseEntity.ok().body(MessageIdResponse("Регистрация магазина прошла успешно", user.shop!!.id))
        } else {
            val shop = Shop(0, payload.name)
            val user = EUser(
                    0, payload.login, encoder.encode(payload.password), null, shop, null,
                    setOf(roleRepository.findRoleByName(ERole.ROLE_SHOP).get()), true
            )
            shop.eUser = user
            userRepository.save(user)
            shopRepository.save(shop)
            return ResponseEntity.ok().body(MessageIdResponse("Регистрация магазина прошла успешно", shop.id))
        }
    }
}
