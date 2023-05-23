package uk.co.setech.easybook.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.setech.easybook.service.InvoiceService;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {
    private InvoiceService invoiceService;

    //    @Scheduled(cron = "${run.interval}")
    public void reminderScheduler() {
        log.info(" ===== Reminder service started =====");
        invoiceService.sendInvoiceReminder();
    }
}
