package bg.sofia.uni.fmi.mjt.commands;

import bg.sofia.uni.fmi.mjt.commands.concretecommands.*;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.UnknownCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandCreatorTest {

    @Test
    void testCreatesDepositCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("deposit-money 100", "user1");

        assertInstanceOf(DepositCommand.class, cmd ,
            "Expected the creator to have made a DepositCommand");
    }

    @Test
    void testCreatesBuyCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("buy BTC 5", "user1");

        assertInstanceOf(BuyCommand.class, cmd ,
            "Expected the creator to have made a BuyCommand");
    }

    @Test
    void testCreatesLogoutCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("logout", "user1");

        assertInstanceOf(LogoutCommand.class, cmd ,
                "Expected the creator to have made a Logout command");
    }

    @Test
    void testCreatesSellCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("sell ETH 3", "user1");

        assertInstanceOf(SellCommand.class, cmd ,
            "Expected the creator to have made a SellCommand");
    }

    @Test
    void testCreatesLoginCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("login john pass123", "ignored");

        assertInstanceOf(LoginCommand.class, cmd ,
            "Expected the creator to have made a LoginCommand");
    }

    @Test
    void testCreatesRegisterCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("register john pass123", "ignored");

        assertInstanceOf(RegisterCommand.class, cmd ,
            "Expected the creator to have made a RegisterCommand");
    }

    @Test
    void testCreatesGetWalletOverallSummaryCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("get-wallet-overall-summary", "ignored");

        assertInstanceOf(GetWalletOverallSummaryCommand.class, cmd ,
            "Expected the creator to have made a GetWalletOverallSummaryCommand");
    }

    @Test
    void testCreatesGetWalletSummaryCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("get-wallet-summary", "ignored");

        assertInstanceOf(GetWalletSummaryCommand.class, cmd ,
            "Expected the creator to have made a GetWalletSummaryCommand");
    }

    @Test
    void testCreatesListOfferingsCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("list-offerings", "ignored");

        assertInstanceOf(ListOfferingsCommand.class, cmd ,
            "Expected the creator to have made a ListOfferingsCommand");
    }

    @Test
    void testCreatesListOfferingCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("list-offering Test", "user1");

        assertInstanceOf(ListOfferingCommand.class, cmd ,
            "Expected the creator to have made a ListOfferingCommand");
    }

    @Test
    void testCreatesHelpCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("help", "user1");

        assertInstanceOf(HelpCommand.class, cmd ,
            "Expected the creator to have made a HelpCommand");
    }

    @Test
    void testUnknownCommandThrows() {
        assertThrows(UnknownCommand.class,
            () -> CommandCreator.newCommand("not-a-command arg1 arg2", "user1"),
            "Expected to throw an exception when the command is unknown");
    }


}
