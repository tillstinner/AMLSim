package amlsim.model.normal;

import amlsim.*;
import amlsim.model.AbstractTransactionModel;

import java.util.List;
import java.util.Random;

/**
 * Return money to one of the previous senders
 */
public class MutualTransactionModel extends AbstractTransactionModel {
    private static final double GROUP_MEMBER_BIAS = 0.75;


    private Random random;

    public MutualTransactionModel(
        AccountGroup accountGroup,
        Random random
    ) {
        this.accountGroup = accountGroup;
        this.random = random;
    }

    public void setParameters(int interval, long start, long end){
        super.setParameters(interval, start, end);
        if(this.startStep < 0){  // decentralize the first transaction step
            this.startStep = generateStartStep(interval);
        }
    }

    @Override
    public String getModelName() {
        return "Mutual";
    }

    @Override
    public void sendTransactions(long step, Account account) {
        if((step - this.startStep) % interval != 0)return;

        Account counterpart = null;
        List<Account> origMembers = this.accountGroup.getMembersInOrigList(account);
        if (!origMembers.isEmpty() && this.random.nextDouble() < GROUP_MEMBER_BIAS) {
            counterpart = origMembers.get(this.random.nextInt(origMembers.size()));
        }
        if(counterpart == null){
            List<Account> groupMembers = this.accountGroup.getMembersExcluding(account);
            if(!groupMembers.isEmpty()) {
                counterpart = groupMembers.get(this.random.nextInt(groupMembers.size()));
            }
        }
        if(counterpart == null){
            counterpart = account.getPrevOrig();
        }
        if(counterpart == null){
            List<Account> origs = account.getOrigList();
            if(origs.isEmpty()) {
                return;
            }else{
                counterpart = origs.get(this.random.nextInt(origs.size()));
            }
        }

        TargetedTransactionAmount transactionAmount = new TargetedTransactionAmount(account.getBalance(), random);

        if(!account.getBeneList().contains(counterpart)) {
            account.addBeneAcct(counterpart);    // Add a new destination
        }

        makeTransaction(step, transactionAmount.doubleValue(), account, counterpart);
    }
}
