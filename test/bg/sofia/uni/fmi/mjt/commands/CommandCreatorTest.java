package bg.sofia.uni.fmi.mjt.commands;

import bg.sofia.uni.fmi.mjt.commands.concretecommands.*;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.UnknownCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandCreatorTest {
    private User user;
    @BeforeEach
    void setUp() throws Exception {
        user = new User("user1", "pass");
    }

    @Test
    void testCreatesDepositCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("deposit-money 100", user);

        assertInstanceOf(DepositCommand.class, cmd ,
            "Expected the creator to have made a DepositCommand");
    }

    @Test
    void testCreatesBuyCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("buy BTC 5", user);

        assertInstanceOf(BuyCommand.class, cmd ,
            "Expected the creator to have made a BuyCommand");
    }

    @Test
    void testCreatesLogoutCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("logout", user);

        assertInstanceOf(LogoutCommand.class, cmd ,
                "Expected the creator to have made a Logout command");
    }

    @Test
    void testCreatesSellCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("sell ETH 3", user);

        assertInstanceOf(SellCommand.class, cmd ,
            "Expected the creator to have made a SellCommand");
    }

    @Test
    void testCreatesLoginCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("login john pass123", user);

        assertInstanceOf(LoginCommand.class, cmd ,
            "Expected the creator to have made a LoginCommand");
    }

    @Test
    void testCreatesCheckAlertsCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("check-alerts", user);

        assertInstanceOf(CheckAlertsCommand.class, cmd ,
                "Expected the creator to have made a CheckAlertsCommand");
    }

    @Test
    void testCreatesSetAlertCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("set-alert btc 123", user);

        assertInstanceOf(SetAlertCommand.class, cmd ,
                "Expected the creator to have made a SetAlertCommand");
    }

    @Test
    void testCreatesRegisterCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("register john pass123", user);

        assertInstanceOf(RegisterCommand.class, cmd ,
            "Expected the creator to have made a RegisterCommand");
    }

    @Test
    void testCreatesGetWalletOverallSummaryCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("get-wallet-overall-summary", user);

        assertInstanceOf(GetWalletOverallSummaryCommand.class, cmd ,
            "Expected the creator to have made a GetWalletOverallSummaryCommand");
    }

    @Test
    void testCreatesGetWalletSummaryCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("get-wallet-summary", user);

        assertInstanceOf(GetWalletSummaryCommand.class, cmd ,
            "Expected the creator to have made a GetWalletSummaryCommand");
    }

    @Test
    void testCreatesListOfferingsCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("list-offerings", user);

        assertInstanceOf(ListOfferingsCommand.class, cmd ,
            "Expected the creator to have made a ListOfferingsCommand");
    }

    @Test
    void testCreatesListOfferingCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("list-offering Test", user);

        assertInstanceOf(ListOfferingCommand.class, cmd ,
            "Expected the creator to have made a ListOfferingCommand");
    }

    @Test
    void testCreatesHelpCommand() throws UnknownCommand {
        CommandInterface cmd = CommandCreator.newCommand("help", user);

        assertInstanceOf(HelpCommand.class, cmd ,
            "Expected the creator to have made a HelpCommand");
    }

    @Test
    void testUnknownCommandThrows() {
        assertThrows(UnknownCommand.class,
            () -> CommandCreator.newCommand("not-a-command arg1 arg2", user),
            "Expected to throw an exception when the command is unknown");
    }


}
