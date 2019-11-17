package com.cscie97.store.controller;

import com.cscie97.store.authentication.AccessDeniedException;
import com.cscie97.store.authentication.AuthenticationToken;
import com.cscie97.store.model.*;

import java.util.logging.Logger;

/**
 * Similar to product added to basket command, this command is called to keep inventory count in sync and to
 * track items in a basket
 * @author Tofik Mussa
 */
public class ProductRemovedFromBasketCommand extends AbstractCommand {

    private String productId;
    private String customerId;
    private String storeId;
    private String aisleNumber;
    private String shelfId;
    private String userId;

    Logger logger = Logger.getLogger(ProductRemovedFromBasketCommand.class.getName());

    /**
     *
     * @param productId
     * @param customerId
     * @param storeId
     * @param aisleNumber
     * @param shelfId
     */
    public ProductRemovedFromBasketCommand(String productId, String customerId, String storeId, String aisleNumber,
                                           String shelfId, String userId) {
        this.productId = productId;
        this.customerId = customerId;
        this.storeId = storeId;
        this.aisleNumber = aisleNumber;
        this.shelfId = shelfId;
        this.userId = userId;
    }

    /**
     * It is assumed that the customer removes one item at a time. The inventory gets updated to reflect for the count that
     * got put back and then a robot takes the item from floor to store room because it is assumed that the shelf was
     * stocked already
     * @return - customer removed product from basket event
     */
    @Override
    public Event execute() {
        try {
            AuthenticationToken token = this.authenticationService.findValidAuthenticationTokenForAUser(userId);
            Customer customer= this.storeModelService.getCustomerById(customerId, token.getTokenId());
            Basket basket = this.storeModelService.getBasketOfACustomer(customer.getCustomerId(), token.getTokenId());
            Product product = this.storeModelService.getProductById(productId, token.getTokenId());
            basket.removeProductFromBasket(product, 1);
            logger.info("Customer "+ customer.getFirstName() + " removed " +
                    product.getProductName() + " from basket " + basket.getBasketId());
            Inventory inventory = this.storeModelService.getInventoryByProductId(productId, token.getTokenId());
            int updatedCount = this.storeModelService.updateInventoryCount(inventory.getInventoryId(),
                    1, token.getTokenId());
            logger.info("The inventory " + inventory.getInventoryId() + " has been updated to reflect what the " +
                    "customer removed from and the new inventory count for "+ product.getProductName() + " is " +
                    updatedCount);
            Robot robot = this.storeModelService.getAllRobotsWithinAnAisle(inventory.getInventoryLocation().getStoreId(),
                    inventory.getInventoryLocation().getAisleNumber(), token.getTokenId()).get(0);
            Command robotCommand = new Command("Put product  " + product.getProductName() + " back to store room from "+
                    inventory.getInventoryLocation().getShelfId() + " because the customer " +
                    customer.getFirstName() + " no longer wants to purchase the item");
            logger.info(robot.listenToCommand(robotCommand));
            int updatedAgainCount = this.storeModelService.updateInventoryCount(inventory.getInventoryId(),
                    -1, token.getTokenId());
            logger.info("Robot " + robot.getApplianceId() + " has stored " +
                    inventory.getInventoryId() + " in store room and the new count in the shelf is " + updatedAgainCount);
        } catch (StoreException e) {
            logger.warning("Updating customer's basket failed ");
        } catch (AccessDeniedException e) {
            logger.warning("Authentication failed " + e.getReason() + " : " + e.getFix());
        }
        return new Event(ProductAddedToBasketCommand.class.getName());
    }
}
