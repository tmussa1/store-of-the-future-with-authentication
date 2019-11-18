package com.cscie97.store.test;

import com.cscie97.store.authentication.*;
import com.cscie97.store.controller.StoreControllerService;
import com.cscie97.store.model.Event;
import com.cscie97.store.model.IStoreModelService;
import com.cscie97.store.model.StoreException;
import com.cscie97.store.model.StoreModelService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class reads script from file and processes them
 * @author Tofik Mussa
 */
public class CommandProcessor {


    private Map<String, AuthenticationToken> userIdToAuthTokenMap = new HashMap<>();
    private static String cmdProcessorUserId;

    /**
     * Interprets commands from the scripts
     * @param storeModelService
     * @param command
     * @param lineNumber
     * @return
     */
    private String processCommand(IStoreModelService storeModelService, String command, int lineNumber,
                                  IAuthenticationService authenticationService)  {
        String [] commandWords = command.split(" ");

        switch(commandWords[0].toLowerCase()){
            case "register-cmd":
                cmdProcessorUserId = commandWords[2];
                User userCMD = authenticationService.createUser(cmdProcessorUserId);
                return userCMD.getUserId() + " command processor admin has been created";
            case "register-user":
                 return CreateUtil.createUser(authenticationService, commandWords[2]);
            case "add-credentials":
                try {
                    return UpdateUtil.addCredentialsToUser(authenticationService,
                            commandWords[2], commandWords[4], commandWords[6]);
                } catch (NoSuchAlgorithmException e) {
                    return ExceptionUtil.outputException(lineNumber, "Error adding credentials to user", e);
                }
            case "create-permission":
                Permission permission = authenticationService
                        .createPermission(commandWords[2], commandWords[4], commandWords[6]);
                return permission.getPermissionId() + " permission has been created";
            case "create-role":
                Role role = authenticationService.createRole(commandWords[2], commandWords[4], commandWords[6]);
                return role.getEntitlementId() + " role has been added";
            case "add-permission-to-role":
                try {
                    Role roleWithPerm = authenticationService.addPermissionToRole(commandWords[2], commandWords[4]);
                    return roleWithPerm.getEntitlementId() + " role had permission added";
                } catch (AuthenticationServiceException e) {
                    ExceptionUtil.outputException(lineNumber, "Failed to added permission to role ", e);
                }
            case "add-child-role-to-parent-role":
                Role roleWithChild = authenticationService.addChildRoleToParentRole(commandWords[2],
                        commandWords[4]);
                return roleWithChild.getEntitlementId() + " had a child role added";
            case "get-role-by-id":
                try {
                    Role roleById = authenticationService.getRoleById(commandWords[2]);
                    return roleById.getEntitlementId() + " role has been found";
                } catch (AuthenticationServiceException e) {
                    ExceptionUtil.outputException(lineNumber, "Role not found", e);
                }
            case "get-permission-by-id":
                try {
                    Permission permissionById = authenticationService.getPermissionById(commandWords[1]);
                    return permissionById.getPermissionId() + " permission has been found";
                } catch (AuthenticationServiceException e) {
                    ExceptionUtil.outputException(lineNumber, "Permission not found", e);
                }
            case "get-user-by-id":
                try {
                    User userByUserId = authenticationService.getUserByUserId(commandWords[2]);
                    return userByUserId.getUserId() + " user has been found";
                } catch (AuthenticationServiceException e) {
                    ExceptionUtil.outputException(lineNumber, "User not found", e);
                }
            case "add-entitlement-to-user":
                try {
                    Entitlement entitlement = authenticationService.getEntitlementByEntitlementId(commandWords[4]);
                    User user = authenticationService.addEntitlementToUser(commandWords[2], entitlement);
                    return user.getUserId() + " user had an entitlement added";
                } catch (AuthenticationServiceException e) {
                    ExceptionUtil.outputException(lineNumber, "Unable to add entitlement to user", e);
                }
            case "get-authentication-inventory-print":
                return authenticationService.getInventoryPrint();
            case "create-resource":
                Resource resource = authenticationService.createResource(commandWords[2], commandWords[4]);
                return resource.getResourceId() + " resource has been created";
            case "get-resource-by-id":
                try {
                    Resource resourceByResourceId = authenticationService.getResourceByResourceId(commandWords[1]);
                    return resourceByResourceId.getResourceId() + " resource has been found";
                } catch (AuthenticationServiceException e) {
                   ExceptionUtil.outputException(lineNumber, "Resource not found", e);
                }
            case "create-resource-role":
                ResourceRole resourceRole = authenticationService.createResourceRole(commandWords[2], commandWords[4],
                        commandWords[6], commandWords[8]);
                return resourceRole.getEntitlementId() + " ResourceRole has been created";
            case "add-entitlement-to-resource-role":
                ResourceRole resourceRoleWithEntitle = authenticationService
                        .addEntitlementsToResourceRole(commandWords[2], commandWords[4]);
                return resourceRoleWithEntitle.getEntitlementId() + " ResourceRole had been updated with entitlement";
            case "add-resource-to-resource-role":
                ResourceRole resourceRoleWithResource = authenticationService
                        .addResourcesToResourceRole(commandWords[4], commandWords[2]);
                return resourceRoleWithResource.getEntitlementId() + " ResourceRole had resource added";
            case "add-resource-role-to-user":
                User user = authenticationService.
                        addResourceRoleToUser(commandWords[2], commandWords[4]);
                return user.getUserId() + " user had ResourceRole added";
            case "get-resource-role-by-id":
                try {
                    ResourceRole resourceRoleById= authenticationService
                            .getResourceRoleByResourceRoleId(commandWords[1]);
                    return resourceRoleById.getEntitlementId() + " resource has been found";
                } catch (AuthenticationServiceException e) {
                    ExceptionUtil.outputException(lineNumber, "ResourceRole not found", e);
                }
            case "get-entitlement-by-id":
                try {
                    Entitlement entitlement = authenticationService
                            .getEntitlementByEntitlementId(commandWords[1]);
                    return entitlement.getEntitlementId() + " entitlement has been found";
                } catch (AuthenticationServiceException e) {
                    ExceptionUtil.outputException(lineNumber, "Entitlement not found", e);
                }
            case "add-child-resource-role-to-resource-role":
                Role roleWithChildResRole = authenticationService.
                        addChildResourceRoleToParentRole(commandWords[2], commandWords[4]);
                return roleWithChildResRole.getEntitlementId() + " ResourceRole had a child ResourceRole added";
            case "add-voiceprint":
                return UpdateUtil.addVoicePrintToUser(authenticationService, commandWords[2], commandWords[3]);
            case "add-faceprint":
                return UpdateUtil.addFacePrintToUser(authenticationService, commandWords[2], commandWords[3]);
            case "log-in":
                    AuthenticationToken authenticationToken = authenticationService
                            .generateToken(commandWords[2], commandWords[4], commandWords[6]);
                    userIdToAuthTokenMap.put(commandWords[2], authenticationToken);
                    return authenticationToken.getTokenId() + " token has been assigned to user";
            case "log-in-face":
            case "log-in-voice":
                AuthenticationToken authenticationTokenVF = authenticationService
                        .generateToken(commandWords[2], commandWords[3]);
                userIdToAuthTokenMap.put(commandWords[2], authenticationTokenVF);
                return authenticationTokenVF.getTokenId() + " token has been assigned to user";
            case "log-out":
                State state = authenticationService.logOut(commandWords[2]);
                return "Successfully logged out " + commandWords[2] + " and the state of token is expired";
            case "define-store":
                   try {
                       return CreateUtil.createStore(storeModelService, commandWords[1], commandWords[3],
                               commandWords[5], commandWords[6], commandWords[7],
                               userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                   } catch (StoreException e) {
                       return ExceptionUtil.outputException(lineNumber, "Store created failed" , e);
                   }
            case "show-store":
                try {
                    return ShowUtil.showStoreDetails(storeModelService ,commandWords[1],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Store not found", e);
                }
            case "define-aisle":
                try{
                    String [] storeAisle = commandWords[1].split(":");
                    return CreateUtil.createAisle(storeModelService, storeAisle[0], storeAisle[1],
                            commandWords[3], commandWords[5],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Aisle creation failed", e);
                }
            case "show-aisle":
                try {
                    String [] storeAisle = commandWords[1].split(":");
                    return ShowUtil.showAisleDetails(storeModelService, storeAisle[0], storeAisle[1],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Aisle not found", e);
                }
            case "define-shelf":
                try {
                    String [] storeAisleShelf = commandWords[1].split(":");
                    return CreateUtil.createShelf(storeModelService, storeAisleShelf[0], storeAisleShelf[1],
                            storeAisleShelf[2], commandWords[3], commandWords[5], commandWords[7],
                            commandWords[9], userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Shelf not created", e);
                }
            case "show-shelf":
                try {
                    String [] storeAisleShelf = commandWords[1].split(":");
                    return ShowUtil.showShelfDetails(storeModelService, storeAisleShelf[0], storeAisleShelf[1],
                            storeAisleShelf[2], userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Shelf not found", e);
                }
            case "define-product":
                    return CreateUtil.createProduct(storeModelService, commandWords[1], commandWords[3],
                            commandWords[5],commandWords[7],commandWords[9], commandWords[11],
                            commandWords[13], userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
            case "show-product":
                try {
                    return ShowUtil.showProductDetails(storeModelService, commandWords[1],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Product not found", e);
                }
            case "define-inventory":
                try {
                    String [] storeAisleShelf = commandWords[3].split(":");
                    return CreateUtil.createInventory(storeModelService, commandWords[1], storeAisleShelf[0],
                            storeAisleShelf[1], storeAisleShelf[2],  commandWords[5], commandWords[7], commandWords[9],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Inventory creation failed", e);
                }
            case "show-inventory":
                try {
                    return ShowUtil.showInventoryDetails(storeModelService, commandWords[1],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Inventory with the id not found", e);
                }
            case "update-inventory":
                try{
                    return UpdateUtil.updateInventoryCount(storeModelService, commandWords[1],
                            commandWords[3], userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Inventory count not updated", e);
                }
            case "define-customer":
                try{
                    authenticationService.createUser(commandWords[13]);
                    return CreateUtil.createCustomer(storeModelService, commandWords[1], commandWords[3],
                            commandWords[3], commandWords[5], commandWords[7],
                            commandWords[9], userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Customer not created", e);
                }
            case "update-customer":
                try {
                    String [] storeAisle = commandWords[3].split(":");
                    return UpdateUtil.updateCustomerLocation(storeModelService, commandWords[1], storeAisle[0],
                            storeAisle[1], userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Customer location not updated", e);
                }
            case "show-customer":
                try {
                    return ShowUtil.showCustomerDetails(storeModelService, commandWords[1],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Customer not found", e);
                }
            case "define-basket":
                try {
                    return CreateUtil.createBasketForACustomer(storeModelService, commandWords[3],
                            commandWords[1], userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Basket not created for a customer", e);
                }
            case "get-customer-basket":
                try {
                    return ShowUtil.showBasketOfACustomer(storeModelService, commandWords[1],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Basket of a customer not found", e);
                }
            case "add-basket-item":
                try {
                    return UpdateUtil.addBasketItem(storeModelService, commandWords[1],
                            commandWords[3], commandWords[5],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Item not added to basket", e);
                }
            case "remove-basket-item":
                try {
                    return UpdateUtil.removeItemFromBasket(storeModelService, commandWords[1],
                            commandWords[3], commandWords[5],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Item not removed from basket", e);
                }
            case "clear-basket":
                try {
                    return UpdateUtil.clearBasketAndRemoveCustomerAssociation(storeModelService, commandWords[1],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Customer's association to basket not removed", e);
                }
            case "show-basket-items":
                try{
                    return ShowUtil.showBasketItems(storeModelService, commandWords[1],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Basket not found", e);
                }
            case "define-sensor":
                try {
                    String [] storeAisle = commandWords[7].split(":");
                    return CreateUtil.createSensor(storeModelService, commandWords[1], commandWords[3],
                            commandWords[5], storeAisle[0], storeAisle[1],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Sensor creation failed", e);
                }
            case "show-sensor":
                try{
                    String [] storeAisle = commandWords[3].split(":");
                    return ShowUtil.showSensor(storeModelService, storeAisle[0], storeAisle[1],
                            commandWords[1], userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                }catch(StoreException e){
                    return ExceptionUtil.outputException(lineNumber, "Sensor not found", e);
                }
            case "create-sensor-event":
                try{
                    String [] storeAisle = commandWords[3].split(":");
                    return CreateUtil.createSensorEvent(storeModelService,storeAisle[0], storeAisle[1], commandWords[1],
                            command, userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Sensor event not created", e);
                }
            case "define-appliance":
                try{
                    String [] storeAisle = commandWords[7].split(":");
                    return CreateUtil.createAnAppliance(storeModelService, commandWords[1], commandWords[3],
                            commandWords[5], storeAisle[0], storeAisle[1],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Appliance not created", e);
                }
            case "show-appliance":
                try{
                    String [] storeAisle = commandWords[3].split(":");
                    return ShowUtil.showAppliance(storeModelService, commandWords[1], storeAisle[0],
                            storeAisle[1], userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Appliance not found", e);
                }
            case "create-appliance-event":
                try{
                    String [] storeAisle = commandWords[5].split(":");
                    return CreateUtil.createApplianceEvent(storeModelService, commandWords[1],
                            commandWords[3], storeAisle[0], storeAisle[1],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Appliance event not created", e);
                }
            case "create-appliance-command":
                try{
                    String [] storeAisle = commandWords[5].split(":");
                    return CreateUtil.createApplianceCommand(storeModelService, storeAisle[0], storeAisle[1],
                            commandWords[1], commandWords[3],
                            userIdToAuthTokenMap.get(cmdProcessorUserId).getTokenId());
                } catch (StoreException e) {
                    return ExceptionUtil.outputException(lineNumber, "Appliance command not sent", e);
                }
            case "create-controller":
                StoreControllerService controller = new StoreControllerService(commandWords[1]);
                storeModelService.getObservers().add(controller);
                return CreateUtil.createAnObserver(storeModelService, controller);
            case "invoke-commands":
            case "entering":
            case "emergency":
            case "removes":
            case "approached_turnstile":
            case "weight_assistance":
            case "basket_value":
            case "adds":
            case "dropped":
            case "broken_glass":
            case "find_customer":
            case "wants":
            case "seen_at":
            case "create-ledger":
            case "create-account":
            case "process-transaction":
                Event event = new Event(command);
                return CreateUtil.createEventSCSListensTo(storeModelService, event);
            default:
                return DetailsUtil.beginOfScript();
        }

    }


    /**
     * Accepts a file and processes line by line
     * @param file
     * @throws CommandProcessorException
     */
    public void processCommandFile(String file) throws CommandProcessorException {
        try {
            File storeFile = new File(file);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(storeFile));
            int lineNumber = 0;
            IStoreModelService storeModelService = StoreModelService.getInstance();
            IAuthenticationService authenticationService = AuthenticationService.getInstance();
            String command;
            while((command = bufferedReader.readLine()) != null){
                lineNumber++;
                System.out.println(processCommand(storeModelService, command, lineNumber, authenticationService));
            }
        }  catch (IOException e) {
            throw new CommandProcessorException("Error reading", "Command can not be processed ", 1);
        }
    }

}
