import java.util.*;
import java.math.*;
/*
 * TO DO: ADD OVERDRAFT SYSTEM
 * 
 * This program is meant to simulate the behavior of a rudimentary banking system by allowing the creation of an account and a method for allowing deposits, withdrawals, and tranfers
 * while keeping a simple log of the transactions of an account both incoming and outgoing. I do this by creating a class called BankAccount that keeps track of these variables:
 * owner (the name attached to the account), accnum (the account number), acclog (an arraylist of strings that is used as the transaction log for this program), and balance (the money
 * in the account). I used the Java library BigDecimal for the balance variable because floating point numbers are not exact and when it comes to financial applications, exact values
 * are incredibly important and BigDecimal allows me to use decimal places (for cents) without having to worry about floating point inaccuracies. I used a HashMap to store the accounts 
 * because I felt the nature of the <key,value> system of maps perfectly suited this program in lieu of an actual SQL database.
 * 
 * The BankAccount class has 5 methods and a constructor. The constructor requires two strings, the first which would be whats used as the owner of the account, while the second would 
 * be used as the accnum for the account it creates a new acclog and says that the account was created and also sets the balance to be $0 and puts this newly created object in the 
 * HashMap with the value being itself, and the key being the accnum. There is then the CheckBalance method which returns the balance of the account. There is the Deposit method that, 
 * due to how BigDecimal works with floating point numbers, takes a string, converts that to a BigDecimal which is then checked that you are not depositing no money or negative money. 
 * I do this by comparing it to BigDecimal.ZERO (a default value of BigDecimal that is equal to zero) this comparison returns a -1 if the value being compares is less than what it is 
 * being compared to (in this case 0), returns a 0 in case the values are the same, and returns a 1 if the value is higher than what it is being compared to so for the deposit method 
 * it checks if what this comparion returns is greater than 0. if it is greater, the method adds the value to the existing balance, prints out how much was deposited, what the new 
 * balance is, and logs the deposit in the acclog. There is then the Withdraw method which like the Deposit method takes a string, converts it to BigDecimal, and then checks if its
 * greater than zero. However, we also have an additional check where the balance is compared to the withdrawal value and we check if the balace if greater than or equal to the 
 * withdrawal. If the withdrawal is greater than the balance we have it send a message saying the withdrawal is cancelled due to insuffienct funds, otherwise, it subtracts the value
 * from the balance, prints the amount withdrawn and the remaining balance of the account. Next there is the Transfer method. this method takes 2 String values, the first being the 
 * value that we want transferred, and the second is the key we use to find the recipient account in the hashmap. Due to the nature of transfers, this method's code needs to be done 
 * in a try-catch block with exception handling so that if there is an issue, we can throw an exception and essentially cancel the transfer by not executing the code. For transfers
 * the major causes for concern would be if the recipient account does not exist, and what if the sender is trying to send more money than whats in the account. For the first issue, 
 * we have the HashMap check if the value provided as the account number is an existing key on the map, if yes, we allow it to continue to the check, otherwise it aborts the tranfer. 
 * The second check, much like by the Withdraw method, checks if the existing balance for the sender is greater than or equal to the amount thats being tranffered out of it. If it 
 * passes both checks, the program calls a Withdraw method on the sender account, and uses the key given to find the recipient account and performs a deposit method on that account 
 * and writes for both account's acclog that a transfer happened (and says who the recipient was for the sender and vice versa). The last method, Printlog, prints the acclog as a 
 * String as a record of all transactions that happened on this account.
 * 
 */
public class Main {
    //since this is a self contained program I am using hashmaps to store the data for the accounts and normally this would instead be pulled from an SQL or other such database using java APIs
    final private static HashMap<String, BankAccount> Accounts = new HashMap<String, BankAccount>();
    public static void main(String[] args) {
    //since this is mainly for testing we simply create the accounts this way, but this can be improved by having the 2 variables for the BankAccount constructor be provided by user input
    BankAccount A = new BankAccount("Michael", "1001001");
    BankAccount B = new BankAccount("John", "1010101");
    A.Deposit("77.65");
    A.Withdraw("25.00");
    A.Withdraw("-25.00");
    //test for the exception that gets thrown when transferring more money than the sender has in their account
    A.Transfer("70.00", "1010101");
    //test for the exception that gets thrown when transferring money to an account that does not exist
    A.Transfer("25.50", "1111000");
    //tests if the transfer function works when an exception wouldnt be thrown
    A.Transfer("50.00", "1010101");
    A.CheckBalance();
    B.CheckBalance();
    A.Printlog();
    B.Printlog();
  }
static class BankAccount{
    String owner;
    String accnum;
    //acclog keeps a list of all transactions meant to be printed out as a rudimentary transaction history
    ArrayList<String> acclog;
    BigDecimal balance;
    //constructor
    BankAccount(String name, String number){
        Accounts.put(number, this);
        this.accnum = number;
        this.owner = name;
        acclog = new ArrayList<String>();
        acclog.add("Transaction log for account number "+this.accnum);
        acclog.add("Account Created");
        balance = BigDecimal.ZERO;
    }
    void CheckBalance(){
        System.out.println("$"+this.balance);
    }
    //checks if the argument is greater than zero and if yes adds it to the balance
    void Deposit(String deposit){
        BigDecimal money = new BigDecimal(deposit);
        if(money.compareTo(BigDecimal.ZERO)>0){
            balance = balance.add(money);
            System.out.println("Amount Deposited: $" + money + " to " + accnum);
            System.out.println("Current Balance for account number " + accnum +": $" + balance);
            acclog.add("Amount Deposited: $" + money);
        }
    }
    //checks if the argument is greater than zero and also if theres more money in the account than is being withdrawn otherwise doesnt do the withdrawal and sends a message saying so
    void Withdraw(String withdraw){
        BigDecimal money = new BigDecimal(withdraw);
        if(money.compareTo(BigDecimal.ZERO)>0){
            if(balance.compareTo(money)>=0){
            balance = balance.subtract(money);
            System.out.println("Amount Withdrawn: $" + money + " from " + accnum);
            System.out.println("Current Balance for account number " + accnum +": $" + balance);
            acclog.add("Amount Withdrawn: $" + money);
            }else{
                System.out.println("Insufficient Funds");
                acclog.add("Withdrawal cancelled: Insufficient Funds");
            }
        }else{
            System.out.println("Withdrawal cancelled: all withdrawals must be a positive value");
        }
    }
    void Transfer(String transfer, String number){
        //tranfer method needs to be  done withing a try-catch block in case of issues where something might go wrong in the transfer (like putting in a recipient that does not exist)
        //and have exceptions get thrown when this happens so that the code that is in the try-catch block doesnt happen and that if there is no exceptions, do the transfer
        BigDecimal money = new BigDecimal(transfer);
        try{
            //if recipient account does not exist throws an exception that would cancel the transfer
            if(!Accounts.containsKey(number)){
                throw (new Exception("recipient account does not exist"));
            }
            //if sender account does not have enough money this will throw an exception that cancels the transfer
            if(balance.compareTo(money)<0){
                throw (new Exception("Insufficient Funds, Transfer Cancelled"));
            }
            BankAccount recipient = Accounts.get(number);
            acclog.add("Outgoing Tranfer to "+ number);
            this.Withdraw(transfer);
            recipient.acclog.add("Incoming Transfer from "+ this.accnum);
            recipient.Deposit(transfer);
        }
        //catches the exception and prints 
        catch(Exception e){
            System.out.println(e);
        }
    }
    void Printlog(){
        //adds the remaining balance to the end of the list and then prints the list as a String
        acclog.add("Remaining balance: $" + this.balance);
        System.out.println(acclog.toString());
    }
}
}