package uk.co.setech.EasyBook.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.co.setech.EasyBook.service.InvoiceService;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {
    private final InvoiceService invoiceService;
    @Scheduled(cron = "${run.interval}")
    public void reminderScheduler(){
        log.info(" ===== Reminder service started =====");
        invoiceService.sendInvoiceReminder();
    }
}
