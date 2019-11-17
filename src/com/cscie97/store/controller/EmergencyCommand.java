package com.cscie97.store.controller;

import com.cscie97.store.authentication.AccessDeniedException;
import com.cscie97.store.authentication.AuthenticationToken;
import com.cscie97.store.model.*;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * This command gets called during emergency. Robots address the emergency
 * @author Tofik Mussa
 */
public class EmergencyCommand extends AbstractCommand{

    private String emergencyType;
    private String storeId;
    private String aisleNumber;
    private String userId;

    Logger logger = Logger.getLogger(EmergencyCommand.class.getName());

    /**
     *
     * @param emergencyType
     * @param storeId
     * @param aisleNumber
     */
    public EmergencyCommand(String emergencyType, String storeId,
                            String aisleNumber, String userId) {
        this.emergencyType = emergencyType;
        this.storeId = storeId;
        this.aisleNumber = aisleNumber;
        this.userId = userId;
    }

    /**
     * The sequence of events than happen during emergency are as follows
     * 1 - ALl of the turnstiles around the area are opened
     * 2 - The speaker urges customers to leave the store
     * 3 - A randomly picked robot from nearby addresses the emergency
     * 4 - The rest of the robots assist customers to leave the store
     * @return - an emergency type event
     */
    @Override
    public Event execute() {
        List<Turnstile> turnstiles;
        List<Speaker> speakers;
        List<Robot> robots;
        Store store;

        try {
            AuthenticationToken token = this.authenticationService.findValidAuthenticationTokenForAUser(userId);
            store = this.storeModelService.getStoreById(storeId, token.getTokenId());
            turnstiles = this.storeModelService.getAllTurnstilesWithinAnAisle(storeId,
                    aisleNumber, token.getTokenId());
            List<Turnstile> turnstilesOpened = this.storeModelService.openTurnstiles(turnstiles,
                    token.getTokenId());
            turnstilesOpened.stream().forEach(turnstile ->
                    logger.info("Turnstile " + turnstile.getApplianceId() + " opened for emergency"));
            speakers = this.storeModelService.getAllSpeakersWithinAnAisle(storeId, aisleNumber,
                    token.getTokenId());

            speakers.stream()
                    .forEach(speaker -> logger.info(speaker.echoAnnouncement(new Command("There is emergency " +
                            emergencyType + " in " +
                            aisleNumber + " please leave store " + store.getStoreName() + " immediately"))));
            robots = this.storeModelService.getAllRobotsWithinAnAisle(storeId, aisleNumber, token.getTokenId());
            Command commandForOneRobot = new Command("Address " +
                    emergencyType + " emergency in " + aisleNumber);
            Command commandForRemainingRobots = new Command("Assist customers leaving store " + storeId);
            int rand = new Random().nextInt(robots.size());
            logger.info(robots.get(rand).listenToCommand(commandForOneRobot));
            robots.stream().filter(robot -> robot != robots.get(rand))
                    .forEach(robot -> logger.info(robot.listenToCommand(commandForRemainingRobots)));
        } catch (StoreException e) {
            logger.warning("Error executing emergency commands");
        } catch (AccessDeniedException e) {
            logger.warning("Authentication failed " +e.getReason() + " : " + e.getFix());
        }
        return new Event(emergencyType);
    }
}
