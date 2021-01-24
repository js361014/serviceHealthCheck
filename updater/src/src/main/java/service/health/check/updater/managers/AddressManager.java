package service.health.check.updater.managers;

import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import lombok.RequiredArgsConstructor;
import service.health.check.models.Address;
import service.health.check.models.Address_;

@RequiredArgsConstructor
public class AddressManager {

    // dependencies
    private final EntityManager entityManager;

    public Address getAddressByHostPort(String host, String port) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Address> select = cb.createQuery(Address.class);
        Root<Address> root = select.from(Address.class);
        select.select(root);
        select.where(cb.equal(root.get(Address_.host), host),
                     cb.equal(root.get(Address_.port), port));
        Address address = entityManager.createQuery(select).getSingleResult();
        Session session = (Session) entityManager.getDelegate();
        session.refresh(address);
        return address;
    }

    public void updateAddressHealthyByHostPort(Timestamp lastHealthy, String host, String port) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Address> update = cb.createCriteriaUpdate(Address.class);
        Root<Address> root = update.from(Address.class);
        update.set(root.get(Address_.lastHealthy), lastHealthy);
        update.where(cb.equal(root.get(Address_.host), host),
                     cb.equal(root.get(Address_.port), port));
        entityManager.getTransaction().begin();
        entityManager.createQuery(update).executeUpdate();
        entityManager.getTransaction().commit();
    }

    public void recordSendFirstEmailByHostPort(Timestamp emailSent, String host, String port) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Address> update = cb.createCriteriaUpdate(Address.class);
        Root<Address> root = update.from(Address.class);
        update.set(root.get(Address_.notificationSent), emailSent);
        update.set(root.get(Address_.secondNotificationSent), (Timestamp) null);
        update.where(cb.equal(root.get(Address_.host), host),
                     cb.equal(root.get(Address_.port), port));
        entityManager.getTransaction().begin();
        entityManager.createQuery(update).executeUpdate();
        entityManager.getTransaction().commit();
    }

    public void recordSendSecondEmailByHostPort(Timestamp emailSent, String host, String port) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Address> update = cb.createCriteriaUpdate(Address.class);
        Root<Address> root = update.from(Address.class);
        update.set(root.get(Address_.secondNotificationSent), emailSent);
        update.where(cb.equal(root.get(Address_.host), host),
                     cb.equal(root.get(Address_.port), port));
        entityManager.getTransaction().begin();
        entityManager.createQuery(update).executeUpdate();
        entityManager.getTransaction().commit();
    }
}
