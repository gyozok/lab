package dev.gyozok.estore.usersservice.query;

import dev.gyozok.estore.core.model.PaymentDetails;
import dev.gyozok.estore.core.model.User;
import dev.gyozok.estore.core.query.FetchUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserEventsHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(UserEventsHandler.class);

    @QueryHandler
    public User handle(FetchUserPaymentDetailsQuery fetchUserPaymentDetailsQuery) {
        LOGGER.info("User Query arrived");
        PaymentDetails paymentDetails = PaymentDetails.builder()
                .cardNumber("123Card")
                .cvv("123")
                .name("GYOZO KARGOPOLOV")
                .validUntilMonth(12)
                .validUntilYear(2030)
                .build();

        User userRest = User.builder()
                .firstName("Gyozo")
                .lastName("Kargopolov")
                .userId(fetchUserPaymentDetailsQuery.getUserId())
                .paymentDetails(paymentDetails)
                .build();

        return userRest;
    }
}
