package amlsim;

import amlsim.model.AbstractTransactionModel;

import java.util.*;

public class AccountGroup {
    
    private long accountGroupId;
    private List<Account> members;  // Accounts involved in this alert
    private Account mainAccount;   // Main account of this alert
    private AbstractTransactionModel model;    // Transaction model
    private AMLSim amlsim;  // AMLSim main object

    AccountGroup(long accountGroupId, AMLSim sim) {
        this.accountGroupId = accountGroupId;
        this.members = new ArrayList<>();
        this.mainAccount = null;
        this.amlsim = sim;
    }


    void setModel(AbstractTransactionModel model) {
        this.model = model;
    }

    /**
     * Add transactions
     * 
     * @param step Current simulation step
     */
    void registerTransactions(long step, Account acct) {
        // maybe add is valid step.
        model.sendTransactions(step, acct);
    }

    /**
     * Involve an account in this alert
     * 
     * @param acct Account object
     */
    void addMember(Account acct) {
        this.members.add(acct);
    }

    /**
     * Get main AMLSim object
     * @return AMLSim object
     */
    public AMLSim getSimulator(){
        return amlsim;
    }

    /**
     * Get account group identifier as long type
     * @return Account group identifier
     */
    public long getAccoutGroupId(){
        return this.accountGroupId;
    }

    /**
     * Get member list of the alert
     * @return Alert account list
     */
    public List<Account> getMembers(){
        return members;
    }

    public List<Account> getMembersExcluding(Account account) {
        List<Account> others = new ArrayList<>();
        for (Account member : this.members) {
            if (member != account) {
                others.add(member);
            }
        }
        return others;
    }

    public List<Account> getMembersInBeneList(Account account) {
        List<Account> candidates = new ArrayList<>();
        List<Account> beneList = account.getBeneList();
        for (Account member : this.members) {
            if (member != account && beneList.contains(member)) {
                candidates.add(member);
            }
        }
        return candidates;
    }

    public List<Account> getMembersInOrigList(Account account) {
        List<Account> candidates = new ArrayList<>();
        List<Account> origList = account.getOrigList();
        for (Account member : this.members) {
            if (member != account && origList.contains(member)) {
                candidates.add(member);
            }
        }
        return candidates;
    }

    /**
     * Get the main account
     * @return The main account if exists.
     */
    public Account getMainAccount(){
        return mainAccount;
    }

    /**
     * Set the main account
     * @param account Main account object
     */
    void setMainAccount(Account account){
        this.mainAccount = account;
    }

    public AbstractTransactionModel getModel() {
        return model;
    }

    public boolean isSAR() {
        return this.mainAccount != null && this.mainAccount.isSAR();
    }
}
