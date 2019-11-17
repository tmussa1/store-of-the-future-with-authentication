package com.cscie97.store.controller;

import com.cscie97.store.authentication.AccessDeniedException;
import com.cscie97.store.authentication.AuthenticationToken;
import com.cscie97.store.model.Customer;
import com.cscie97.store.model.Event;
import com.cscie97.store.model.InventoryLocation;
import com.cscie97.store.model.StoreException;

import java.util.logging.Logger;

/**
 * This command is called when cameras sense the movement of a customer or through voice recognition
 * and a customer's location need to be updated
 * @author Tofik Mussa
 */
public class CustomerSeenCommand extends AbstractCommand {

    private String customerId;
    private String storeId;
    private String aisleNumber;
    private String userId;

    Logger logger = Logger.getLogger(CustomerSeenCommand.class.getName());

    /**
     *
     * @param customerId
     * @param storeId
     * @param aisleNumber
     */
    public CustomerSeenCommand(String customerId, String storeId,
                               String aisleNumber, String userId) {
        this.customerId = customerId;
        this.storeId = storeId;
        this.aisleNumber = aisleNumber;
        this.userId = userId;
    }

    /**
     * Updates the location of a customer when sensed by camera or through voice recognition
     * @return a customer seen type event
     */
    @Override
    public Event execute() {
        Customer customer = null;
        try {
            AuthenticationToken token = this.authenticationService.findValidAuthenticationTokenForAUser(userId);
            customer = this.storeModelService.getCustomerById(customerId, token.getTokenId());
            InventoryLocation customerLocation = this.storeModelService.updateCustomerLocation(customer.getCustomerId(),
                    storeId, aisleNumber, token.getTokenId());
            logger.info("Customer " + customer.getFirstName() + "'s location updated from "+ aisleNumber + " to "+
                    customer.getCustomerLocation().getAisleNumber());
        } catch (StoreException e) {
           logger.warning("Customer location not updated");
        } catch (AccessDeniedException e) {
            logger.warning("Authentication failed " + e.getReason() + " : " +e.getFix());
        }
        return new Event(CustomerSeenCommand.class.getName());
    }
}
