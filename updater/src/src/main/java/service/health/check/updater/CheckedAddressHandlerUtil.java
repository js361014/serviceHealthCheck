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

    /**
     * Checks if we should send email to the first admin. We should send email to first admin if:
     * system was unhealthy for the time set in the target configuration and the email hasn't been sent yet.
     * If the system hasn't been marked as healthy since the registration,
     * we assume it's still being setup and we don't send any notifications.
     *
     * @param address the target of checkup
     * @param now     timestamp representing the current moment in time
     * @return if we should send email to the first admin
     */
    public static boolean shouldSendMailToFirstAdmin(Address address, Timestamp now) {
        if (address.getLastHealthy() == null) {
            return false;
        }
        long lastHealthyMillisecondsAgo = now.getTime() - address.getLastHealthy().getTime();
        log.info("{}:{} last healthy for {}ms ago. First email sent? - {}", address.getHost(), address.getPort(),
                 lastHealthyMillisecondsAgo, formatTimestamp(address.getNotificationSent()));
        return address.getNotificationSent() == null &&
                lastHealthyMillisecondsAgo > SECONDS_TO_MILLIS * address.getSendNotificationAfter();
    }

    /**
     * Checks if we should send email to the second admin. We should send email to first admin if:
     * the first admin hasn't responded to our email for the time set in the target configuration
     * and the email hasn't been sent yet.
     *
     * @param address the target of checkup
     * @param now     timestamp representing the current moment in time
     * @return if we should send email to the second admin
     */
    public static boolean shouldSendMailToSecondAdmin(Address address, Timestamp now) {
        if (address.getNotificationSent() == null) {
            return false;
        }
        long emailToFirstAdminSentMillisecondsAgo = now.getTime() - address.getNotificationSent().getTime();
        log.info("{}:{} email sent to first admin {}ms ago. Second email sent? - {}", address.getHost(),
                 address.getPort(), emailToFirstAdminSentMillisecondsAgo,
                 formatTimestamp(address.getSecondNotificationSent()));
        return address.getSecondNotificationSent() == null &&
                emailToFirstAdminSentMillisecondsAgo > SECONDS_TO_MILLIS * address.getResendNotificationAfter();
    }

    private static String formatTimestamp(Timestamp timestamp) {
        return timestamp != null ?
                timestamp.toString() :
                "false";
    }
}
