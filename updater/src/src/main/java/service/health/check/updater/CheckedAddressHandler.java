package service.health.check.updater;

import java.sql.Timestamp;

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
        log.info("Updater - target healthy {}:{}. Updating DB entry...", checkedAddress.getHost(),
                 checkedAddress.getPort());
        addressManager.updateAddressHealthyByHostPort(now, checkedAddress.getHost(), checkedAddress.getPort());
    }

    private void handleUnhealthyTarget(CheckedAddress checkedAddress) {
        log.info("Updater - target unhealthy {}:{}. Check if should send email", checkedAddress.getHost(),
                 checkedAddress.getPort());
        Address address = addressManager.getAddressByHostPort(checkedAddress.getHost(), checkedAddress.getPort());
        if (shouldSendMailToFirstAdmin(address, now)) {
            sendMailToFirstAdmin(checkedAddress);
        } else if (shouldSendMailToSecondAdmin(address, now)) {
            sendMailToSecondAdmin(checkedAddress);
        }
    }

    private void sendMailToFirstAdmin(CheckedAddress checkedAddress) {
        log.info("Updater - target unhealthy {}:{} for too long. Sending email...", checkedAddress.getHost(),
                 checkedAddress.getPort());
        // todo: sendmail
        addressManager.recordSendFirstEmailByHostPort(now, checkedAddress.getHost(), checkedAddress.getPort());
    }

    private void sendMailToSecondAdmin(CheckedAddress checkedAddress) {
        log.info("Updater - admin not confirmed issue for {}:{} for too long. Sending email...",
                 checkedAddress.getHost(),
                 checkedAddress.getPort());
        // todo: sendmail
        addressManager.recordSendSecondEmailByHostPort(now, checkedAddress.getHost(), checkedAddress.getPort());
    }
}
