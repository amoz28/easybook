package uk.co.setech.EasyBook.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.co.setech.EasyBook.service.InvoiceService;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private InvoiceService invoiceService;
    @Scheduled(cron = "0 0 0 * * ?")
    public void reminderScheduler(){
        invoiceService.sendInvoiceReminder();
    }
}
