package com.cscie97.store.test;

import com.cscie97.store.authentication.*;
import com.cscie97.store.model.*;

import java.security.NoSuchAlgorithmException;

/**
 * Utility class for update functionality
 * @author Tofik Mussa
 */
public class UpdateUtil {

    public static String addVoicePrintToUser(IAuthenticationService authenticationService, String userId,
                                              String voicePrintStr) {
        VoicePrint voicePrint = new VoicePrint(voicePrintStr);
        User user = authenticationService.addCredentialsToUser(userId, voicePrint);
        return DetailsUtil.outputUpdateConfirmation(user.getUserId(), "  Voiceprint added");
    }

    public static String addFacePrintToUser(IAuthenticationService authenticationService, String userId,
                                             String facePrintStr) {
        FacePrint facePrint = new FacePrint(facePrintStr);
        User user = authenticationService.addCredentialsToUser(userId, facePrint);
        return DetailsUtil.outputUpdateConfirmation(user.getUserId(), "  Faceprint added");
    }

    public static String addCredentialsToUser(IAuthenticationService authenticationService, String userId,
                                              String userName, String password) throws NoSuchAlgorithmException {
        UserNamePassword userNamePassword = new UserNamePassword(userName, password);
        User user = authenticationService.addCredentialsToUser(userId, userNamePassword);
        return DetailsUtil.outputUpdateConfirmation(user.getUserId(), " Credentials added");
    }
    /**
     * Updates inventory count
     * @param storeModelService
     * @param inventoryId
     * @param count
     * @return confirmation of inventory updated
     * @throws StoreException
     */
    public static String updateInventoryCount(IStoreModelService storeModelService, String inventoryId,
                                              String count, String authToken) throws StoreException {
        int countInt = parseNumber(count);
        storeModelService.updateInventoryCount(inventoryId, countInt, authToken);
        return DetailsUtil.outputUpdateConfirmation(inventoryId, " inventory count updated to " + countInt );
    }

    /**
     * Updates customer location
     * @param storeModelService
     * @param customerId
     * @param storeId
     * @param aisleNumber
     * @return confirmation of customer's location updated
     * @throws StoreException
     */
    public static String updateCustomerLocation(IStoreModelService storeModelService, String customerId, String storeId,
                                                String aisleNumber, String authToken) throws StoreException {
        InventoryLocation inventoryLocation = storeModelService.
                updateCustomerLocation(customerId, storeId, aisleNumber, authToken);
        return DetailsUtil.outputUpdateConfirmation(customerId,
                " location of customer updated to " + inventoryLocation.getAisleNumber());
    }

    /**
     * Updates item's in a customer's basket
     * @param storeModelService
     * @param basketId
     * @param productId
     * @param count
     * @return confirmation of customer's basket updated
     * @throws StoreException
     */
    public static String addBasketItem(IStoreModelService storeModelService, String basketId, String productId,
                                          String count, String authToken) throws StoreException {
        int countInt = parseNumber(count);
        Basket basket = storeModelService.addItemToBasket(basketId, productId, countInt, authToken);
        return DetailsUtil.outputUpdateConfirmation(basket.getBasketId(),
                " product count in basket updated to " + countInt);
    }

    /**
     * Removes items from a customer's basket
     * @param storeModelService
     * @param basketId
     * @param productId
     * @param countReturned
     * @return confirmation of items removed from basket
     * @throws StoreException
     */
    public static String removeItemFromBasket(IStoreModelService storeModelService, String basketId, String productId,
                                              String countReturned, String authToken) throws StoreException {
        int countReturnedInt = parseNumber(countReturned);
        Basket basket = storeModelService.removeItemFromBasket(basketId, productId, countReturnedInt, authToken);
        return DetailsUtil.outputUpdateConfirmation(basket.getBasketId(), " item with " + productId +
                " removed from basket");
    }

    /**
     * Clears basket and removes association with customer
     * @param storeModelService
     * @param basketId
     * @return confirmation of customer/basket association removed
     * @throws StoreException
     */
    public static String clearBasketAndRemoveCustomerAssociation(IStoreModelService storeModelService,
                                                                 String basketId, String authToken)
            throws StoreException {
        Customer customer = storeModelService.clearBasketAndRemoveAssociationWithACustomer(basketId, authToken);
        return DetailsUtil.outputUpdateConfirmation(customer.getFirstName(), " the customer's association " +
                "to the basket has been cleared");
    }

    /**
     * Parses negative and positive numbers
     * @param count
     * @return a parsed integer
     */
    private static int parseNumber(String count) {
        if(count.charAt(0) == '-'){
            count = count.substring(1);
            return -Integer.parseInt(count);
        }
        return Integer.parseInt(count);
    }
}
