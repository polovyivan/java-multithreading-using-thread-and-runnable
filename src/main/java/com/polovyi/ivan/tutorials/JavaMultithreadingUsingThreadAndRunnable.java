package com.polovyi.ivan.tutorials;

import com.polovyi.ivan.tutorials.client.CustomerDataClient;
import com.polovyi.ivan.tutorials.client.CustomerPurchaseTransactionClient;
import com.polovyi.ivan.tutorials.dto.CustomerDataResponse;
import com.polovyi.ivan.tutorials.dto.CustomerWithTransactionResponse;
import com.polovyi.ivan.tutorials.dto.PurchaseTransactionResponse;
import com.polovyi.ivan.tutorials.thread.CustomerDataClientThread;
import com.polovyi.ivan.tutorials.thread.CustomerPurchaseTransactionClientThread;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class JavaMultithreadingUsingThreadAndRunnable {

    public static void main(String[] args) throws InterruptedException {

        simpleThread();

        multiThread();

    }

    private static void simpleThread() {
        LocalDateTime startTime = LocalDateTime.now();

        CustomerPurchaseTransactionClient customerPurchaseTransactionClient = new CustomerPurchaseTransactionClient();

        List<PurchaseTransactionResponse> purchaseTransactionResponses = customerPurchaseTransactionClient.fetchByCustomerId(
                1L);

        CustomerDataClient customerDataClient = new CustomerDataClient();
        CustomerDataResponse customerDataResponse = customerDataClient.fetchCustomerById(1L);

        CustomerWithTransactionResponse customerWithTransactionResponse = CustomerWithTransactionResponse.builder()
                .customerDataResponse(customerDataResponse)
                .purchaseTransactionResponse(purchaseTransactionResponses)
                .build();
        log.info("Customer with purchase transaction {} ", customerWithTransactionResponse);
        log.info("Operation duration {} ", Duration.between(startTime, LocalDateTime.now()).toSeconds());
    }

    private static void multiThread() throws InterruptedException {
        LocalDateTime startTime = LocalDateTime.now();

        // Thread I
        CustomerPurchaseTransactionClientThread customerPurchaseTransactionClientThread = new CustomerPurchaseTransactionClientThread(
                1l);
        customerPurchaseTransactionClientThread.setName("customer-purchase-transaction-client-thread");

        // Thread II
        CustomerDataClientThread customerDataClientRunnable = new CustomerDataClientThread(1l);
        Thread customerDataClientThread = new Thread(customerDataClientRunnable);
        customerDataClientThread.setName("customer-data-client-thread");

        customerPurchaseTransactionClientThread.start();
        customerDataClientThread.start();
        customerPurchaseTransactionClientThread.join();
        customerDataClientThread.join();

        List<PurchaseTransactionResponse> purchaseTransactionResponses = customerPurchaseTransactionClientThread.getPurchaseTransactionResponses();
        CustomerDataResponse customerDataResponse = customerDataClientRunnable.getCustomerResponse();

        CustomerWithTransactionResponse customerWithTransactionResponse = CustomerWithTransactionResponse.builder()
                .customerDataResponse(customerDataResponse)
                .purchaseTransactionResponse(purchaseTransactionResponses)
                .build();

        log.info("Customer with purchase transaction {} ", customerWithTransactionResponse);
        log.info("Operation duration {} ", Duration.between(startTime, LocalDateTime.now()).toSeconds());
    }
}
