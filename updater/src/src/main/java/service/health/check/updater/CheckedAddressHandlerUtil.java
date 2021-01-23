package service.health.check.updater;

import java.sql.Timestamp;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import service.health.check.models.Address;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckedAddressHandlerUtil {

    // constants
    private static final long SECONDS_TO_MILLIS = 1000;

    public static boolean shouldSendMailToFirstAdmin(Address address, Timestamp now) {
        if (address.getLastHealthy() == null) {
            return false;
        }
        long lastHealthyMillisecondsAgo = now.getTime() - address.getLastHealthy().getTime();
        log.info("Last healthy for {}:{} was {}ms ago. Email sent? - {}", address.getHost(), address.getPort(),
                  lastHealthyMillisecondsAgo, address.getNotificationSent() != null);
        return address.getNotificationSent() == null &&
                lastHealthyMillisecondsAgo > SECONDS_TO_MILLIS * address.getSendNotificationAfter();
    }

    public static boolean shouldSendMailToSecondAdmin(Address address, Timestamp now) {
        if (address.getNotificationSent() == null) {
            return false;
        }
        long emailToFirstAdminSentMillisecondsAgo = now.getTime() - address.getNotificationSent().getTime();
        log.info("Email sent to first admin for {}:{} was {}ms ago. Email sent? - {}", address.getHost(), address.getPort(),
                  emailToFirstAdminSentMillisecondsAgo, address.getSecondNotificationSent());
        return !address.getSecondNotificationSent() &&
                emailToFirstAdminSentMillisecondsAgo > SECONDS_TO_MILLIS * address.getResendNotificationAfter();
    }
}
