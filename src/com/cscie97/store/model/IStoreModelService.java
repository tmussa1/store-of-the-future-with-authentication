package com.cscie97.store.model;

import java.util.List;
import java.util.Map;

/**
 * This is the Store Model Service Interface which is the only class the outside world knows about
 * @author Tofik Mussa
 */
public interface IStoreModelService {

    Store createAStore(String storeId, String storeName, Address storeAddress, String authToken) throws StoreException;

    Store getStoreById(String storeId, String authToken) throws StoreException;

    Aisle createAisle(String storeId, String aisleNumber, String aisleDescription, String location, String authToken) throws StoreException;

    Aisle getAisleByStoreIdAndAisleNumber(String storeId, String aisleNumber, String authToken) throws StoreException;

    Shelf createAShelf(String storeId, String aisleNumber, String shelfId, String shelfName, String level, String shelfDescription, String temperature, String authToken) throws StoreException;

    Shelf getShelfByStoreIdAisleNumShelfId(String storeId, String aisleNumber, String shelfId, String authToken) throws StoreException;

    Inventory createInventory(String inventoryId, String storeId, String aisleNumber, String shelfId, int capacity, int count, String productId, String authToken) throws StoreException;

    Inventory getInventoryById(String inventoryId, String authToken) throws StoreException;

    int updateInventoryCount(String inventoryId, int difference, String authToken) throws StoreException;

    Product createAProduct(String productId, String productName, String productDescription, int size, String category, int price, String temperature, String authToken);

    Product getProductById(String productId, String authToken) throws StoreException;

    Customer createCustomer(String customerId, String firstName, String lastName, String type, String emailAddress, String accountAddress, String authToken) throws StoreException;

    Customer getCustomerById(String customerId, String authToken) throws StoreException;

    InventoryLocation updateCustomerLocation(String customerId, String storeId, String aisleNumber, String authToken) throws StoreException;

    Basket getBasketOfACustomer(String customerId, String authToken) throws StoreException;

    Basket createBasketForACustomer(String customerId, String basketId, String authToken) throws StoreException;

    Basket addItemToBasket(String basketId, String productId, int count, String authToken) throws StoreException;

    Inventory getInventoryByProductId(String productId, String authToken) throws StoreException;

    Basket removeItemFromBasket(String basketId, String productId, int countReturned, String authToken) throws StoreException;

    Customer clearBasketAndRemoveAssociationWithACustomer(String basketId, String authToken) throws StoreException;

    Map<Product, Integer> getBasketItems(String basketId, String authToken) throws StoreException;

    ISensor createASensor(String sensorId, String sensorName, String sensorType, String storeId, String aisleNumber, String authToken) throws StoreException;

    ISensor getSensorByLocationAndSensorId(String storeId, String aisleNumber, String sensorId, String authToken) throws StoreException;

    IAppliance getApplianceByLocationAndSensorId(String storeId, String aisleNumber, String applianceId, String authToken) throws StoreException;

    String createSensorEvent(String storeId, String aisleNumber, String sensorId, Event event, String authToken) throws StoreException;

    IAppliance createAnAppliance(String applianceId, String applianceName, String applianceType, String storeId, String aisleNumber, String authToken) throws StoreException;

    String createApplianceEvent(String storeId, String aisleNumber, String applianceId, Event event, String authToken) throws StoreException;

    String createApplianceCommand(String storeId, String aisleNumber, String applianceId, Command command, String authToken) throws StoreException;

    IAppliance moveRobot(String storeId, String aisleNumber, String applianceRobotId, String newAisleNumber, String authToken) throws StoreException;

    List<Turnstile> getAllTurnstilesWithinAnAisle(String storeId, String aisleNumber, String authToken) throws StoreException;

    List<Turnstile> openTurnstiles(List<Turnstile> turnstiles, String authToken);

    List<Turnstile> closeTurnstiles(List<Turnstile> turnstiles, String authToken);

    List<Speaker> getAllSpeakersWithinAnAisle(String storeId, String aisleNumber, String authToken) throws StoreException;

    List<Robot> getAllRobotsWithinAnAisle(String storeId, String aisleNumber, String authToken) throws StoreException;

    Customer getCustomerByCustomerName(String customerName, String authToken) throws StoreException;

    Event createAnEvent(Event event);

    List<IObserver> getObservers();
}

