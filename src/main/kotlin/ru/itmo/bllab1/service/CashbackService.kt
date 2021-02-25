package ru.itmo.bllab1.service

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.itmo.bllab1.repo.CashbackRepository
import ru.itmo.bllab1.repo.CashbackStatus
import ru.itmo.bllab1.repo.ClientRepository
import java.time.LocalDateTime

@Service
class CashbackService(
        private val cashbackRepository: CashbackRepository,
        private val messageService: MessageService,
        private val clientRepository: ClientRepository,
) {
    @Scheduled(cron = "0 0 15 * * ?")
    fun processCashbacks() {
        cashbackRepository.findByStartDateLessThan(LocalDateTime.now().minusDays(60)).forEach { cashback ->
            if (cashback.status != CashbackStatus.NEW && cashback.isPaid && cashback.isOrderCompleted && cashback.confirmPayment) {
                cashback.status = CashbackStatus.APPROVED
                cashback.client.balance += cashback.cashbackSum * 0.9;
                clientRepository.save(cashback.client);
                messageService.sendNotificationToClient("Кэшбек зачислен", cashback.client )
            } else {
                cashback.status = CashbackStatus.REJECTED;
                messageService.sendNotificationToClient("Кэшбек отклонен", cashback.client )
            }
            cashbackRepository.save(cashback);
        }
    }
}
