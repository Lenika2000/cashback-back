package ru.itmo.bllab1.controller

import org.springframework.web.bind.annotation.*
import ru.itmo.bllab1.repo.CashbackRepository
import ru.itmo.bllab1.repo.CashbackStatus
import ru.itmo.bllab1.service.CashbackService
import javax.persistence.EntityNotFoundException

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RequestMapping("/api/cashback/")
@RestController
class CashbackServiceController(
        private val cashbackService: CashbackService,
        private val cashbackRepository: CashbackRepository,
) {

    @PostMapping("process")
    fun processCashback(): CashbackResponse {
       cashbackService.processCashbacks();
        return CashbackResponse("Завершена обработка выплат кэшбека для покупок, совершенных более 60 дней назад")
    }

    @GetMapping("status/{id}")
    fun getCashbackStatus(@PathVariable id: Long): CashbackStatus = cashbackRepository.findById(id).orElseThrow {
        EntityNotFoundException("Кэшбек с id $id не найден!")
    }.status

}
