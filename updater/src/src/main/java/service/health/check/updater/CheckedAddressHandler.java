package service.health.check.updater;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;

import javax.mail.MessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.health.check.messages.CheckedAddress;
import service.health.check.models.Address;
import static service.health.check.updater.CheckedAddressHandlerUtil.shouldSendMailToFirstAdmin;
import static service.health.check.updater.CheckedAddressHandlerUtil.shouldSendMailToSecondAdmin;
import service.health.check.updater.managers.AddressManager;
import service.health.check.updater.time.TimeMachine;

@Slf4j
@RequiredArgsConstructor
public class CheckedAddressHandler {

    // constants
    private static final String EMAIL_SUBJECT = "System %s:%s is dead";
    private static final String EMAIL_BODY = "Your system %s:%s is dead. Check what's going on and mark "
            + "that you're responding to the issue at %s?id=%d";
    private static final String FRONT_LINK = System.getProperty("front.link.confirm", "http://localhost:8090/");

    // dependencies
    private final AddressManager addressManager;

    private Timestamp now;

    /**
     * Handle checked address (target) depending on whether it's healthy or not.
     * <p>
     * Healthy targets are marked as healthy in the database
     * and to admins of the unhealthy targets emails are sent if necessary.
     *
     * @param checkedAddress target that has been checked
     */
    public void handle(CheckedAddress checkedAddress) {
        now = TimeMachine.nowTimestamp();
        if (checkedAddress.getHealthy()) {
            handleHealthyTarget(checkedAddress);
        } else {
            handleUnhealthyTarget(checkedAddress);
        }
        now = null;
    }

    private void handleHealthyTarget(CheckedAddress checkedAddress) {
        log.info("Updater - {}:{} healthy. Updating DB entry...", checkedAddress.getHost(),
                 checkedAddress.getPort());
        addressManager.updateAddressHealthyByHostPort(now, checkedAddress.getHost(), checkedAddress.getPort());
    }

    private void handleUnhealthyTarget(CheckedAddress checkedAddress) {
        log.info("Updater - {}:{} unhealthy. Check if should send email", checkedAddress.getHost(),
                 checkedAddress.getPort());
        Address address = addressManager.getAddressByHostPort(checkedAddress.getHost(), checkedAddress.getPort());
        if (shouldSendMailToFirstAdmin(address, now)) {
            sendMailToFirstAdmin(address);
        } else if (shouldSendMailToSecondAdmin(address, now)) {
            sendMailToSecondAdmin(address);
        }
    }

    private void sendMailToFirstAdmin(Address address) {
        log.info("Updater - {}:{} unhealthy for too long. Sending email...", address.getHost(),
                 address.getPort());
        try {
            EmailUtil.sendEmail(address.getFirstAdmin(), getEmailSubject(address), getEmailBody(address));
            addressManager.recordSendFirstEmailByHostPort(now, address.getHost(), address.getPort());
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendMailToSecondAdmin(Address address) {
        log.info("Updater - {}:{} admin didn't confirm issue for too long. Sending email...", address.getHost(),
                 address.getPort());
        try {
            EmailUtil.sendEmail(address.getSecondAdmin(), getEmailSubject(address), getEmailBody(address));
            addressManager.recordSendSecondEmailByHostPort(now, address.getHost(), address.getPort());
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String getEmailSubject(Address address) {
        return String.format(EMAIL_SUBJECT, address.getHost(), address.getPort());
    }

    private static String getEmailBody(Address address) {
        return String.format(EMAIL_BODY, address.getHost(), address.getPort(), FRONT_LINK, address.getId());
    }
}
