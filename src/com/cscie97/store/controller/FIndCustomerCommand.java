package com.cscie97.store.controller;

import com.cscie97.store.authentication.AccessDeniedException;
import com.cscie97.store.authentication.AuthenticationToken;
import com.cscie97.store.model.*;

import java.util.logging.Logger;

/**
 * Locates missing person in the store
 * @author Tofik Mussa
 */
public class FIndCustomerCommand extends AbstractCommand {

    private String firstName;
    private String userId;

    Logger logger = Logger.getLogger(FIndCustomerCommand.class.getName());

    /**
     *
     * @param customerName
     */
    public FIndCustomerCommand(String customerName, String userId) {
        this.firstName = customerName;
        this.userId = userId;
    }

    /**
     * Locates customer by name. The assumption is that it is highly unlikely to have two customers with the same given
     * name at any given time. If it turns out that there is more than one person with the given first name and the
     * customer looking for his mate is dissatisfied, another command shall be sent to find missing person until he is
     * found
     * @return a find customer type event
     */
    @Override
    public Event execute(){
        try {
            AuthenticationToken token = this.authenticationService.findValidAuthenticationTokenForAUser(userId);
            Customer customer = this.storeModelService.getCustomerByCustomerName(firstName, token.getTokenId());
            Speaker speaker = this.storeModelService.getAllSpeakersWithinAnAisle(customer.getCustomerLocation().getStoreId(),
                    customer.getCustomerLocation().getAisleNumber(), token.getTokenId()).get(0);
            Command speakerCommand = new Command("Customer " + customer.getFirstName() + " found in "+
                    customer.getCustomerLocation().getAisleNumber());
            logger.info(speaker.echoAnnouncement(speakerCommand));
        } catch (StoreException e) {
            logger.warning("Finding missing person not successful");
        } catch (AccessDeniedException e) {
            logger.warning("Authentication failed " + e.getReason() + " : " + e.getFix());
        }
        return new Event(FIndCustomerCommand.class.getName());
    }
}
