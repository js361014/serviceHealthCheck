package service.health.check.front.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import service.health.check.front.repository.AddressRepository;
import service.health.check.models.Address;

@RestController
public class DutyController {
    @Autowired
    private AddressRepository addressRepository;

    @GetMapping("/duty-confirm")
    public String createAddress(@Valid @RequestParam(name = "id") Integer addressId) {
        Address address = addressRepository.getOne(addressId);
        address.setLastHealthy(null);
        address.setNotificationSent(null);
        address.setSecondNotificationSent(null);
        addressRepository.save(address);
        return "OK - Confirmed";
    }
}
