package ru.itmo.bllab1.repo

import org.springframework.data.repository.CrudRepository
import ru.itmo.bllab1.model.Admin

interface AdminRepository : CrudRepository<Admin, Long>
