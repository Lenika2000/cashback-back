package ru.itmo.bllab1.repo

import org.springframework.data.repository.CrudRepository
import ru.itmo.bllab1.model.ERole
import ru.itmo.bllab1.model.Role
import java.util.*

interface RoleRepository : CrudRepository<Role, Long> {
    fun findRoleByName(name: ERole): Optional<Role>
}
