package amlsim.model.normal;

import amlsim.Account;
import amlsim.AccountGroup;
import amlsim.TargetedTransactionAmount;
import amlsim.model.AbstractTransactionModel;

import java.util.*;

/**
 * Receive money from one of the senders (fan-in)
 */
public class FanInTransactionModel extends AbstractTransactionModel {

    private int index = 0;

    private Random random;
    private TargetedTransactionAmount transactionAmount;

    public FanInTransactionModel(
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
        return "FanIn";
    }

    private boolean isValidStep(long step){
        return (step - startStep) % interval == 0;
    }

    @Override
    public void sendTransactions(long step, Account account) {
        List<Account> origList = this.accountGroup.getMembersInOrigList(account);
        if (origList.isEmpty()) {
            origList = account.getOrigList();
        }
        int numOrigs = origList.size();
        if (!isValidStep(step) || numOrigs == 0) {
            return;
        }
        if (index >= numOrigs) {
            index = 0;
        }

        Account orig = origList.get(index);
        this.transactionAmount = new TargetedTransactionAmount(orig.getBalance(), this.random);
        double amount = this.transactionAmount.doubleValue();

        makeTransaction(step, amount, orig, account);
        index++;
    }
}
