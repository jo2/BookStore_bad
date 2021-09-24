package de.adesso.bookstore.services;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DummyPaymentTest {

    private LogCaptor logCaptor = LogCaptor.forClass(DummyPayment.class);

   @ParameterizedTest
   @ValueSource(doubles = {0.0, 10.0, 100.0, 49.99})
    public void logInfoAndWarnMessages(double amount) {
        DummyPayment dummyPayment = new DummyPayment();
        dummyPayment.pay(amount);

        assertThat(logCaptor.getInfoLogs()).contains("pay " + amount + " with Dummy.");
    }
}
