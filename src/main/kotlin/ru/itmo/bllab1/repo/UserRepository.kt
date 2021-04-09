package ru.itmo.bllab1.repo

import org.springframework.data.repository.CrudRepository
import ru.itmo.bllab1.model.EUser
import java.util.*

interface UserRepository : CrudRepository<EUser, Long> {
    fun findByLogin(login: String): Optional<EUser>
}
