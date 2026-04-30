package amlsim.model.normal;

import amlsim.Account;
import amlsim.AccountGroup;
import amlsim.TargetedTransactionAmount;
import amlsim.model.AbstractTransactionModel;

import java.util.*;

/**
 * Send money received from an account to another account in a similar way
 */
public class ForwardTransactionModel extends AbstractTransactionModel {
    private int index = 0;

    private Random random;

    public ForwardTransactionModel(
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
        return "Forward";
    }

    @Override
    public void sendTransactions(long step, Account account) {
        if((step - startStep) % interval != 0){
            return;
        }

        List<Account> origs = this.accountGroup.getMembersInOrigList(account);
        List<Account> dests = this.accountGroup.getMembersInBeneList(account);

        if (!origs.isEmpty() && !dests.isEmpty()) {
            if(index >= dests.size()){
                index = 0;
            }

            Account orig = origs.get(index % origs.size());
            TargetedTransactionAmount upstreamAmount = new TargetedTransactionAmount(orig.getBalance(), random);
            this.makeTransaction(step, upstreamAmount.doubleValue(), orig, account);

            Account dest = dests.get(index);
            TargetedTransactionAmount downstreamAmount = new TargetedTransactionAmount(account.getBalance(), random);
            this.makeTransaction(step, downstreamAmount.doubleValue(), account, dest);
            index++;
            return;
        }

        TargetedTransactionAmount transactionAmount = new TargetedTransactionAmount(account.getBalance(), random);
        dests = account.getBeneList();
        int numDests = dests.size();
        if(numDests == 0){
            return;
        }
        if(index >= numDests){
            index = 0;
        }
        Account dest = dests.get(index);
        this.makeTransaction(step, transactionAmount.doubleValue(), account, dest);
        index++;
    }
}
