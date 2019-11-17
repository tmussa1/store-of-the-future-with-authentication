package com.cscie97.store.controller;

import com.cscie97.ledger.Ledger;
import com.cscie97.ledger.LedgerException;
import com.cscie97.store.authentication.AuthenticationService;
import com.cscie97.store.authentication.IAuthenticationService;
import com.cscie97.store.model.Event;
import com.cscie97.store.model.IStoreModelService;
import com.cscie97.store.model.StoreModelService;

import java.util.concurrent.Callable;

/**
 * Establishes some common features for all commands
 * @author Tofik Mussa
 */
public abstract class AbstractCommand implements Callable<Event>, ICommand{

    IStoreModelService storeModelService;
    Ledger ledger;
    IAuthenticationService authenticationService;

    /**
     * Initializes ledger
     * @param ledgerName
     * @param ledgerDescription
     * @param ledgerSeed
     * @throws LedgerException
     */
    public AbstractCommand(String ledgerName, String ledgerDescription, String ledgerSeed)
            throws LedgerException {
        this.ledger = createLedger(ledgerName, ledgerDescription, ledgerSeed);
        this.storeModelService = StoreModelService.getInstance();
        this.authenticationService = AuthenticationService.getInstance();
    }

    private Ledger createLedger(String ledgerName, String ledgerDescription, String ledgerSeed) throws LedgerException {
        if(ledger == null){
            ledger = new Ledger(ledgerName, ledgerDescription, ledgerSeed);
        }
        return ledger;
    }

    /**
     * Overloaded constructor to initialize SMS
     */
    public AbstractCommand() {
        this.storeModelService = StoreModelService.getInstance();
        this.authenticationService = AuthenticationService.getInstance();
    }

    /**
     * Gets called when the thread executes
     * @return
     * @throws Exception
     */
    @Override
    public Event call() throws Exception {
        return this.execute();
    }

}
