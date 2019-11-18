package com.cscie97.store.model;

import com.cscie97.store.authentication.*;
import com.cscie97.store.controller.StoreControllerServiceException;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This is an implementation of the store model service interface
 * @author Tofik Mussa
 */
public class StoreModelService implements IStoreModelService, ISubject {

    private List<Customer> customers;
    private List<Store> stores;
    private Map<String, Inventory> inventoryMap;
    private Map<String, Product> productMap;
    private List<IObserver> observers;
    private static StoreModelService instance;
    private IAuthenticationService authenticationService;

    Logger logger = Logger.getLogger(StoreModelService.class.getName());

    private StoreModelService() {
        this.customers = new ArrayList<>();
        this.stores = new ArrayList<>();
        this.inventoryMap = new HashMap<>();
        this.productMap = new HashMap<>();
        this.observers = new ArrayList<>();
        this.authenticationService = AuthenticationService.getInstance();
    }

    public static StoreModelService getInstance(){
        if(instance == null){
            instance = new StoreModelService();
        }
        return instance;
    }

    /**
     * Create a new store
     * @param storeId
     * @param storeName
     * @param storeAddress
     * @return a store object
     * @throws StoreException
     */
    @Override
    public Store createAStore(String storeId, String storeName, Address storeAddress, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(storeId, storeName),
                    new Permission(PermissionType.CREATE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        checkValidityOfStoreId(storeId, authToken);
        Store store = new Store(storeId, storeName, storeAddress);
        this.stores.add(store);
        return store;
    }

    /**
     * Checks for duplicate stores
     * @param storeId
     * @throws StoreException
     */
    private void checkValidityOfStoreId(String storeId, String authToken) throws StoreException {
        Optional<Store> duplicateStore = this.stores.stream().filter(store -> store.getStoreId().equalsIgnoreCase(storeId))
                .findAny();
        if(!duplicateStore.isEmpty()){
            throw new StoreException("Duplicate Store with the ID provided exists. Store creation failed");
        }
    }

    /**
     * Gets store by store id
     * @param storeId
     * @return Store
     * @throws StoreException
     */
    @Override
    public Store getStoreById(String storeId, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(storeId, ""),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Store store = stores.stream().filter(astore -> astore.getStoreId().equalsIgnoreCase(storeId))
                .collect(Collectors.toList()).get(0);
        if(store == null){
            throw new StoreException("A store with the requested id doesn't exist");
        }
        return store;
    }

    /**
     * Creates a new aisle
     * @param storeId
     * @param aisleNumber
     * @param aisleDescription
     * @param location
     * @return an aisle object
     * @throws StoreException
     */
    @Override
    public Aisle createAisle(String storeId, String aisleNumber, String aisleDescription, String location, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(aisleNumber, aisleDescription),
                    new Permission(PermissionType.CREATE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Store store = getStoreById(storeId, authToken);
        LocationType locationEnum = StoreUtil.convertLocationToEnum(location);
        Aisle aisle = new Aisle(aisleNumber, aisleDescription,locationEnum);
        store.addAisleToAStore(aisle);
        return aisle;
    }

    /**
     * Gets aisle by store id and aisle number
     * @param storeId
     * @param aisleNumber
     * @return an aisle object
     * @throws StoreException
     */
    @Override
    public Aisle getAisleByStoreIdAndAisleNumber(String storeId, String aisleNumber, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(storeId, aisleNumber),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Store store = getStoreById(storeId, authToken);
        Optional<Aisle> aisle = store.getAisles().stream().filter(anAisle -> anAisle.getAisleNumber().equals(aisleNumber)).findAny();
        if(aisle.isEmpty()){
            throw new StoreException("An aisle with the requested id doesn't exist");
        }
        return aisle.get();
    }

    /**
     * Creates a new shelf object
     * @param storeId
     * @param aisleNumber
     * @param shelfId
     * @param shelfName
     * @param level
     * @param shelfDescription
     * @param temperature
     * @return sehlf object
     * @throws StoreException
     */
    @Override
    public Shelf createAShelf(String storeId, String aisleNumber, String shelfId, String shelfName, String level, String shelfDescription, String temperature, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(shelfId, shelfName),
                    new Permission(PermissionType.CREATE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Aisle aisle = getAisleByStoreIdAndAisleNumber(storeId, aisleNumber, authToken);
        Level levelEnum = StoreUtil.convertLevelToEnum(level);
        Temperature temperatureEnum = StoreUtil.convertTemperatureToEnum(temperature);
        Shelf shelf = new Shelf(shelfId, shelfName,levelEnum,shelfDescription,temperatureEnum);
        aisle.addShelfToAisle(shelf);
        return shelf;
    }

    /**
     * Gets shelf by store id, aisle number and shelf id
     * @param storeId
     * @param aisleNumber
     * @param shelfId
     * @return a shelf object
     * @throws StoreException
     */
    @Override
    public Shelf getShelfByStoreIdAisleNumShelfId(String storeId, String aisleNumber, String shelfId, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(aisleNumber, shelfId),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Aisle aisle = getAisleByStoreIdAndAisleNumber(storeId, aisleNumber, authToken);
        Shelf shelf = aisle.getShelves().stream().filter(aShelf -> aShelf.getShelfId().equals(shelfId)).findAny().get();
        if(shelf == null){
            throw new StoreException("A shelf with the requested id doesn't exist");
        }
        return shelf;
    }

    /**
     * Creates a new inventory object
     * @param inventoryId
     * @param storeId
     * @param aisleNumber
     * @param shelfId
     * @param capacity
     * @param count
     * @param productId
     * @return inventory object
     * @throws StoreException
     */
    @Override
    public Inventory createInventory(String inventoryId, String storeId, String aisleNumber, String shelfId, int capacity, int count, String productId, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(shelfId, productId),
                    new Permission(PermissionType.CREATE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Product product = productMap.get(productId);
        if(product == null){
            throw new StoreException("Inventory can not be created for a product that is not defined");
        }
        InventoryLocation inventoryLocation = new InventoryLocation(storeId, aisleNumber, shelfId);
        Shelf shelf = getShelfByStoreIdAisleNumShelfId(storeId, aisleNumber, shelfId, authToken);
        Inventory inventory = new Inventory(inventoryId, product, count, capacity, inventoryLocation);
        inventoryMap.put(inventoryId, inventory);
        shelf.addInventoryToShelf(inventory);
        return inventory;
    }

    /**
     * Gets inventory by inventory id
     * @param inventoryId
     * @return an inventory object
     * @throws StoreException
     */
    @Override
    public Inventory getInventoryById(String inventoryId, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(inventoryId, ""),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Inventory inventory = inventoryMap.get(inventoryId);
        if(inventory == null){
            throw new StoreException("Requested inventory not found in all of the stores");
        }
        return inventory;
    }

    /**
     * This makes sure the overall inventory map for all stores as well as the inventory in a particular shelf is updated.
     * The same inventory(same inventory id, String authToken) can be split between stores so the count for a particular shelf maybe different
     * from the overall inventory count
     * @param inventoryId
     * @param difference
     * @return inventory count
     * @throws StoreException
     */
    @Override
    public int updateInventoryCount(String inventoryId, int difference, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(inventoryId, String.valueOf(difference)),
                    new Permission(PermissionType.UPADTE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Inventory inventoryFromAnyStore = inventoryMap.get(inventoryId);
        if(inventoryFromAnyStore == null){
            throw new StoreException("The inventory that you are trying to update the count of doesn't exist in the stores");
        }
        Shelf shelf = getShelfByStoreIdAisleNumShelfId(inventoryFromAnyStore.getInventoryLocation().getStoreId(),
                inventoryFromAnyStore.getInventoryLocation().getAisleNumber(),
                inventoryFromAnyStore.getInventoryLocation().getShelfId(), authToken);
        Inventory inventoryInTheShelf = shelf.getInventoryList().stream()
                .filter(inventory -> inventory.getInventoryId().equals(inventoryId)).findAny().get();
        if(inventoryInTheShelf == null){
            throw new StoreException("The inventory is not placed in any of the shelves");
        }
        inventoryInTheShelf.setCount(inventoryInTheShelf.getCount() + difference);
        inventoryFromAnyStore.setCount(inventoryFromAnyStore.getCount() + difference);
        return inventoryFromAnyStore.getCount();
    }

    /**
     * Creates a new product
     * @param productId
     * @param productName
     * @param productDescription
     * @param size
     * @param category
     * @param price
     * @param temperature
     * @return a product object
     */
    @Override
    public Product createAProduct(String productId, String productName, String productDescription, int size, String category, int price, String temperature, String authToken) {
        try {
            authenticationService.checkAccess(authToken, new Resource(productId, productName),
                    new Permission(PermissionType.CREATE.toString()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        double volume = Math.pow(size, 3);
        Product product = new Product(productId, productName, productDescription, category, price, volume,
                StoreUtil.convertTemperatureToEnum(temperature));
        this.productMap.put(productId, product);
        return product;
    }

    /**
     * gets a product by product id
     * @param productId
     * @return product object
     * @throws StoreException
     */
    @Override
    public Product getProductById(String productId, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(productId, ""),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Product product = this.productMap.get(productId);
        if(product == null){
            throw new StoreException("A product with the requested id doesn't exist");
        }
        return product;
    }

    /**
     * Checks for a duplicate customer with the same account address and customer id
     * @param customerId
     * @param accountAddress
     * @throws StoreException
     */
    private void duplicateCustomerValidation(String customerId, String accountAddress, String authToken) throws StoreException {
        Optional<Customer> duplicateCustomer = this.customers.stream()
                .filter(aCustomer -> aCustomer.getCustomerId().equals(customerId)
                        && aCustomer.getAccountAddress().equals(accountAddress))
                .findAny();
        if(!duplicateCustomer.isEmpty()){
            throw new StoreException("A customer with the same id and account address exists");
        }
    }

    /**
     * Creates a new customer
     * @param customerId
     * @param firstName
     * @param lastName
     * @param type
     * @param emailAddress
     * @param accountAddress
     * @return customer object
     * @throws StoreException
     */
    @Override
    public Customer createCustomer(String customerId, String firstName, String lastName, String type, String emailAddress, String accountAddress, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(customerId, firstName),
                    new Permission(PermissionType.CREATE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Customer customer = new Customer(customerId, firstName, lastName,
                StoreUtil.convertCustomerTypeToEnum(type), emailAddress, accountAddress);
        duplicateCustomerValidation(customerId, accountAddress, authToken);
        this.customers.add(customer);
        return customer;
    }

    /**
     * Gets customer by customer id
     * @param customerId
     * @return customer object
     * @throws StoreException
     */
    @Override
    public Customer getCustomerById(String customerId, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(customerId, ""),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Optional<Customer> customer = this.customers.stream().filter(aCustomer ->
                aCustomer.getCustomerId().equals(customerId)).findAny();
        if(customer.isEmpty()){
            throw new StoreException("A customer with the requested id doesn't exist");
        }
        return customer.get();
    }

    /**
     * Finds customer by customer id and updates his location which is store id and aisle number
     * @param customerId
     * @param storeId
     * @param aisleNumber
     * @return inventory location object
     * @throws StoreException
     */
    @Override
    public InventoryLocation updateCustomerLocation(String customerId, String storeId, String aisleNumber, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(customerId, aisleNumber),
                    new Permission(PermissionType.UPADTE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        InventoryLocation customerLocation = new InventoryLocation(storeId, aisleNumber, "");
        Customer customer = getCustomerById(customerId, authToken);
        customer.setCustomerLocation(customerLocation);
        return customer.getCustomerLocation();
    }

    /**
     * Gets basket by customer id
     * @param customerId
     * @return a basket object
     * @throws StoreException
     */
    @Override
    public Basket getBasketOfACustomer(String customerId, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(customerId, ""),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Customer customer = getCustomerById(customerId, authToken);
        Basket basket = customer.getBasket();
        if(basket == null){
            customer.setBasket(createBasketForACustomer(customerId, UUID.randomUUID().toString(), authToken));
        }
        this.customers.add(customer);
        return customer.getBasket();
    }

    /**
     * Finds customer by customer id and creates a new basket for him with the provided basket id
     * @param customerId
     * @param basketId
     * @return
     * @throws StoreException
     */
    @Override
    public Basket createBasketForACustomer(String customerId, String basketId, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(basketId, customerId),
                    new Permission(PermissionType.CREATE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Customer customer = getCustomerById(customerId, authToken);
        Basket basket = new Basket(basketId);
        customer.setBasket(basket);
        this.customers.add(customer);
        return customer.getBasket();
    }

    /**
     * Gets a customer associated with a basket id
     * @param basketId
     * @return customer object
     * @throws StoreException
     */
    private Customer getCustomerAssociatedWithABasket(String basketId, String authToken) throws StoreException {

        Customer customer = findCustomerWithBasketId(basketId, authToken);
        if(customer == null){
            throw new StoreException("There is no customer associated with this basket id");
        }
        return customer;
    }

    /**
     * Returns a customer by name
     * @param customerName
     * @return a customer
     */
    @Override
    public Customer getCustomerByCustomerName(String customerName, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(customerName, customerName),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Customer customer = this.customers.stream()
                .filter(aCustomer -> aCustomer.getFirstName().equals(customerName))
                .findAny().get();
        if(customer == null){
            throw new StoreException("Customer not found ");
        }
        return customer;
    }

    /**
     * Finds a customer with the provided basket id
     * @param basketId
     * @return customer object
     */
    private Customer findCustomerWithBasketId(String basketId, String authToken) {
        for(int i = 0; i < customers.size(); i++){
            if(customers.get(i).getBasket() != null &&
                    customers.get(i).getBasket().getBasketId().equalsIgnoreCase(basketId)){
                return customers.get(i);
            }
        }
        return null;
    }

    /**
     * Gets inventory associated with a product id
     * @param productId
     * @return an inventory object
     * @throws StoreException
     */
    @Override
    public Inventory getInventoryByProductId(String productId, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(productId, ""),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Inventory inventory = inventoryMap.values().stream()
                .filter(anInventory -> anInventory.getProduct().getProductId().equals(productId))
                .findAny().get();
         if(inventory == null){
             throw new StoreException("An inventory with the provided product id doesn't exist");
         }
        return inventory;
    }

    /**
     * The method that gets called to add inventory to a shelf has logic if it is existing inventory and replaces it
     * if it is
     * @param basketId
     * @param productId
     * @param count
     * @return a basket object
     * @throws StoreException
     */
    @Override
    public Basket addItemToBasket(String basketId, String productId, int count, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(basketId, productId),
                    new Permission(PermissionType.UPADTE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Customer customer = getCustomerAssociatedWithABasket(basketId, authToken);
        Product product = this.productMap.get(productId);
        if(product == null){
            throw new StoreException("The product you are trying to pick doesn't exist");
        }
        Basket basket = customer.getBasket();
        basket.addProductToBasket(product, count);
        customer.setBasket(basket);
        Inventory inventoryOverAll = getInventoryByProductId(productId, authToken);
        inventoryOverAll.setCount(inventoryOverAll.getCount() - count);
        Shelf shelf = getShelfByStoreIdAisleNumShelfId(inventoryOverAll.getInventoryLocation().getStoreId(),
                inventoryOverAll.getInventoryLocation().getAisleNumber(),
                inventoryOverAll.getInventoryLocation().getShelfId(), authToken);
        Inventory inventoryInTheShelf = shelf.getInventoryInTheShelfByInventoryId(inventoryOverAll.getInventoryId());
        inventoryInTheShelf.setCount(inventoryInTheShelf.getCount() - count);
        shelf.addInventoryToShelf(inventoryInTheShelf);
        this.inventoryMap.replace(inventoryOverAll.getInventoryId(), inventoryOverAll);
        return customer.getBasket();
    }

    /**
     * The same logic as adding. Inventory with updated count replaces existing. The basket also reflected a decreased
     * count if there are remaining items with the same product id, it gets completely removed from the basket if there
     * are not.
     * @param basketId
     * @param productId
     * @param countReturned
     * @return a basket object
     * @throws StoreException
     */
    @Override
    public Basket removeItemFromBasket(String basketId, String productId, int countReturned, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(basketId, productId),
                    new Permission(PermissionType.UPADTE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Customer customer = getCustomerAssociatedWithABasket(basketId, authToken);
        Product product = this.productMap.get(productId);
        if(product == null){
            throw new StoreException("The product you are trying to put back is not from this store");
        }
        Basket basket = customer.getBasket();
        basket.removeProductFromBasket(product, countReturned);
        customer.setBasket(basket);
        Inventory inventoryOverAll = getInventoryByProductId(productId, authToken);
        inventoryOverAll.setCount(inventoryOverAll.getCount() + countReturned);
        Shelf shelf = getShelfByStoreIdAisleNumShelfId(inventoryOverAll.getInventoryLocation().getStoreId(),
                inventoryOverAll.getInventoryLocation().getAisleNumber(),
                inventoryOverAll.getInventoryLocation().getShelfId(), authToken);
        Inventory inventoryInTheShelf = shelf.getInventoryInTheShelfByInventoryId(inventoryOverAll.getInventoryId());
        inventoryInTheShelf.setCount(inventoryInTheShelf.getCount() + countReturned);
        shelf.addInventoryToShelf(inventoryInTheShelf);
        this.inventoryMap.replace(inventoryOverAll.getInventoryId(), inventoryOverAll);
        return customer.getBasket();
    }

    /**
     * Clears a customer's association with a basket and clears the items currently in basket
     * @param basketId
     * @return a customer object
     * @throws StoreException
     */
    @Override
    public Customer clearBasketAndRemoveAssociationWithACustomer(String basketId, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(basketId, ""),
                    new Permission(PermissionType.DELETE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Customer customer = getCustomerAssociatedWithABasket(basketId, authToken);
        Basket basket = getBasketOfACustomer(customer.getCustomerId(), authToken);
        basket.setProductsMap(null);
        customer.setBasket(null);
        return customer;
    }

    /**
     * Gets products in a basket with their count based on basket id
     * @param basketId
     * @return map of products with counts
     * @throws StoreException
     */
    @Override
    public Map<Product, Integer> getBasketItems(String basketId, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(basketId, ""),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Customer customer = getCustomerAssociatedWithABasket(basketId, authToken);
        Basket basket = getBasketOfACustomer(customer.getCustomerId(), authToken);
        return basket.getProductsMap();
    }

    /**
     * Creates a new sensor object
     * @param sensorId
     * @param sensorName
     * @param sensorType
     * @param storeId
     * @param aisleNumber
     * @return a sensor object
     * @throws StoreException
     */
    @Override
    public ISensor createASensor(String sensorId, String sensorName, String sensorType, String storeId,
                                 String aisleNumber, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(sensorId, sensorName),
                    new Permission(PermissionType.CREATE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        InventoryLocation location = new InventoryLocation(storeId, aisleNumber, "");
        ISensor sensor = SensorApplianceFactory.createSensor(sensorType, sensorId, sensorName, location);
        Aisle aisle = getAisleByStoreIdAndAisleNumber(storeId, aisleNumber, authToken);
        aisle.addSensorToAisle(sensor);
        return sensor;
    }

    /**
     * Gets a sensor by store id, aisle number and sensor id
     * @param storeId
     * @param aisleNumber
     * @param sensorId
     * @return a sensor object
     * @throws StoreException
     */
    @Override
    public ISensor getSensorByLocationAndSensorId(String storeId, String aisleNumber, String sensorId, String authToken)
            throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(sensorId, storeId),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Aisle aisle = getAisleByStoreIdAndAisleNumber(storeId, aisleNumber, authToken);
        ISensor sensor = aisle.getSensorById(sensorId);
        return sensor;
    }

    /**
     * Gets an appliance object by store id, aisle number and appliance id
     * @param storeId
     * @param aisleNumber
     * @param applianceId
     * @return an appliance object
     * @throws StoreException
     */
    @Override
    public IAppliance getApplianceByLocationAndSensorId(String storeId, String aisleNumber, String applianceId, String authToken)
            throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(applianceId, storeId),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Aisle aisle = getAisleByStoreIdAndAisleNumber(storeId, aisleNumber, authToken);
        IAppliance appliance = aisle.getApplianceById(applianceId);
        return appliance;
    }

    /**
     * Creates a new sensor event. Store controller service is not listening to this
     * @param storeId
     * @param aisleNumber
     * @param sensorId
     * @param event
     * @throws StoreException
     * @return sensor event
     */
    @Override
    public String createSensorEvent(String storeId, String aisleNumber, String sensorId, Event event, String authToken)
            throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(storeId, sensorId),
                    new Permission(PermissionType.CREATE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        ISensor sensor = getSensorByLocationAndSensorId(storeId, aisleNumber, sensorId, authToken);
        return sensor.generateSensorEvent(event);
    }

    /**
     * Creates a new appliance object
     * @param applianceId
     * @param applianceName
     * @param applianceType
     * @param storeId
     * @param aisleNumber
     * @return an appliance object
     * @throws StoreException
     */
    @Override
    public IAppliance createAnAppliance(String applianceId, String applianceName, String applianceType,
                                        String storeId, String aisleNumber, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(applianceId, storeId),
                    new Permission(PermissionType.CREATE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        InventoryLocation location = new InventoryLocation(storeId, aisleNumber, "");
        IAppliance appliance = SensorApplianceFactory.createAppliance(applianceType, applianceId,
                applianceName, location);
        Aisle aisle = getAisleByStoreIdAndAisleNumber(storeId, aisleNumber, authToken);
        aisle.addApplianceToAisle(appliance);
        return appliance;
    }

    /**
     * Creates a new appliance event. Store controller service is not listening to this
     * @param storeId
     * @param aisleNumber
     * @param applianceId
     * @param event
     * @return an appliance event
     * @throws StoreException
     */
    @Override
    public String createApplianceEvent(String storeId, String aisleNumber, String applianceId, Event event, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(applianceId, storeId),
                    new Permission(PermissionType.CREATE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Aisle aisle = getAisleByStoreIdAndAisleNumber(storeId, aisleNumber, authToken);
        IAppliance appliance = aisle.getApplianceById(applianceId);
        return appliance.generateApplianceEvent(event);
    }

    /**
     * Creates a new appliance command
     * @param storeId
     * @param aisleNumber
     * @param applianceId
     * @param command
     * @return an appliance command
     * @throws StoreException
     */
    @Override
    public String createApplianceCommand(String storeId, String aisleNumber, String applianceId, Command command, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(applianceId, storeId),
                    new Permission(PermissionType.CREATE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Aisle aisle = getAisleByStoreIdAndAisleNumber(storeId, aisleNumber, authToken);
        IAppliance appliance = aisle.getApplianceById(applianceId);
        return appliance.listenToCommand(command);
    }

    /**
     * Moves the robot by changing its location
     * @param storeId
     * @param aisleNumber
     * @param applianceRobotId
     * @param newAisleNumber
     * @return a robot
     * @throws StoreException
     */
    @Override
    public IAppliance moveRobot(String storeId, String aisleNumber, String applianceRobotId, String newAisleNumber, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(applianceRobotId, storeId),
                    new Permission(PermissionType.UPADTE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Robot robot = (Robot) getApplianceByLocationAndSensorId(storeId,aisleNumber,
                applianceRobotId, authToken);
        InventoryLocation newRobotLocation = new InventoryLocation(storeId, newAisleNumber, "");
        robot.setApplianceLocation(newRobotLocation);
        return robot;
    }

    @Override
    public List<Turnstile> getAllTurnstilesWithinAnAisle(String storeId, String aisleNumber, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(aisleNumber, storeId),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Aisle aisle = getAisleByStoreIdAndAisleNumber(storeId, aisleNumber, authToken);
        List<Turnstile> turnstiles = aisle.getAppliances().stream()
                .filter(anAppliance -> anAppliance instanceof Turnstile)
                .map(anAppliance -> (Turnstile) anAppliance)
                .collect(Collectors.toList());
        return turnstiles;
    }

    @Override
    public List<Turnstile> openTurnstiles(List<Turnstile> turnstiles, String authToken) {
        try {
            authenticationService.checkAccess(authToken, new Resource(turnstiles.get(0).getApplianceId(), ""),
                    new Permission(PermissionType.UPADTE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
         return turnstiles.stream()
                 .map(aTurnstile -> aTurnstile.openTurnstile())
                 .collect(Collectors.toList());
    }

    @Override
    public List<Turnstile> closeTurnstiles(List<Turnstile> turnstiles, String authToken) {
        try {
            authenticationService.checkAccess(authToken, new Resource(turnstiles.get(0).getApplianceId(), ""),
                    new Permission(PermissionType.UPADTE.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        return turnstiles.stream()
                .map(aTurnstile -> aTurnstile.closeTurnstile())
                .collect(Collectors.toList());
    }

    @Override
    public List<Speaker> getAllSpeakersWithinAnAisle(String storeId, String aisleNumber, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(aisleNumber, storeId),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Aisle aisle = getAisleByStoreIdAndAisleNumber(storeId, aisleNumber, authToken);
        List<Speaker> speakers = aisle.getAppliances().stream()
                .filter(anAppliance -> anAppliance instanceof Speaker)
                .map(anAppliance -> (Speaker) anAppliance)
                .collect(Collectors.toList());
        return speakers;
    }

    @Override
    public List<Robot> getAllRobotsWithinAnAisle(String storeId, String aisleNumber, String authToken) throws StoreException {
        try {
            authenticationService.checkAccess(authToken, new Resource(aisleNumber, storeId),
                    new Permission(PermissionType.READ.getPermission()));
        } catch (AccessDeniedException e) {
            logger.warning("Unable to authenticate" + e.getReason() + " : " + e.getFix());
        }
        Aisle aisle = getAisleByStoreIdAndAisleNumber(storeId, aisleNumber, authToken);
        List<Robot> robots = aisle.getAppliances().stream()
                .filter(anAppliance -> anAppliance instanceof Robot)
                .map(anAppliance -> (Robot) anAppliance)
                .collect(Collectors.toList());
        return robots;
    }

    /**
     * This are the events that the store controller service is interested in
     * No need for authentication that SCS listens to. SCS is going to log in, get auth token
     * and perform actions on SMS
     * @param event
     * @return -an Event SCS is interested in
     */
    @Override
    public Event createAnEvent(Event event) {
        notify(event);
        return event;
    }

    /**
     * Attaches an observer
     * @param observer
     */
    @Override
    public void register(IObserver observer) {
        observers.add(observer);
    }

    /**
     * Detaches an observer
     * @param observer
     */
    @Override
    public void deregister(IObserver observer) {
        observers.remove(observer);
    }


    /**
     * List of observers
     * @return list of observers
     */
    @Override
    public List<IObserver> getObservers() {
        return observers;
    }

    /**
     * Notifies all of the observers with an event
     * @param event
     */
    @Override
    public void notify(Event event) {
        CopyOnWriteArrayList<IObserver> observerList = new CopyOnWriteArrayList<>(observers);

        synchronized (observerList) {
            Iterator<IObserver> observerIterator = observerList.iterator();
            while (observerIterator.hasNext()) {
                IObserver observer = observerIterator.next();
                try {
                    observer.update(event);
                } catch (StoreControllerServiceException e) {
                    logger.info("Unable to update observers");
                }
            }
        }
    }

}
