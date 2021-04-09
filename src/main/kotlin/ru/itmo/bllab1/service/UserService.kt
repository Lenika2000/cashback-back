package ru.itmo.bllab1.service

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import ru.itmo.bllab1.auth.UserDetailsImpl
import ru.itmo.bllab1.model.ERole
import ru.itmo.bllab1.model.EUser
import ru.itmo.bllab1.repo.CashbackRepository
import ru.itmo.bllab1.repo.UserRepository


@Service
class UserService(
        private val userRepository: UserRepository,
        private val cashbackRepository: CashbackRepository
) {
    fun getCurrentUserId(): Long = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl).id

    fun getUserFromAuth(): EUser = userRepository.findById(getCurrentUserId())
            .orElseThrow { UsernameNotFoundException("User not found - ${getCurrentUserId()}") }

    fun checkClientAuthority(clientId: Long) {
        val accessor = getUserFromAuth()
        if (accessor.roles.any { r -> r.name == ERole.ROLE_ADMIN })
            return
        val client = accessor.client
        if (clientId != client?.id)
            throw IllegalAccessException("Доступ запрещен")
    }

    fun checkShopAuthority(shopId: Long) {
        val accessor = getUserFromAuth()
        if (accessor.roles.any { r -> r.name == ERole.ROLE_ADMIN })
            return
        val shop = accessor.shop
        if (shopId != shop?.id)
            throw IllegalAccessException("Доступ запрещен")
    }

    fun checkShopOrCustomerAuthority(cashbackId: Long) {
        val accessor = getUserFromAuth()
        if (accessor.roles.any { r -> r.name == ERole.ROLE_ADMIN })
            return
        if (accessor.shop != null) {
            if (cashbackRepository.findCashbackByShop(accessor.shop!!).all {c -> c.id != cashbackId})
                throw IllegalAccessException("Доступ запрещен")
        } else {
            if (cashbackRepository.findCashbackByClient(accessor.client!!).all {c -> c.id != cashbackId})
                throw IllegalAccessException("Доступ запрещен")
        }
    }

}
