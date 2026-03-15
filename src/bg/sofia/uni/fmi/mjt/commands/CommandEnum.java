package bg.sofia.uni.fmi.mjt.commands;

import bg.sofia.uni.fmi.mjt.commands.concretecommands.BuyCommand;
import bg.sofia.uni.fmi.mjt.commands.concretecommands.DepositCommand;
import bg.sofia.uni.fmi.mjt.commands.concretecommands.GetWalletOverallSummaryCommand;
import bg.sofia.uni.fmi.mjt.commands.concretecommands.GetWalletSummaryCommand;
import bg.sofia.uni.fmi.mjt.commands.concretecommands.HelpCommand;
import bg.sofia.uni.fmi.mjt.commands.concretecommands.ListOfferingCommand;
import bg.sofia.uni.fmi.mjt.commands.concretecommands.ListOfferingsCommand;
import bg.sofia.uni.fmi.mjt.commands.concretecommands.LoginCommand;
import bg.sofia.uni.fmi.mjt.commands.concretecommands.LogoutCommand;
import bg.sofia.uni.fmi.mjt.commands.concretecommands.RegisterCommand;
import bg.sofia.uni.fmi.mjt.commands.concretecommands.SellCommand;

import java.util.List;

public enum CommandEnum {
    DEPOSIT_MONEY("deposit-money") {
        @Override
        public CommandInterface createCommand(List<String> arguments, String user) {
            return new DepositCommand(arguments, user);
        }
    },
    LIST_OFFERINGS("list-offerings") {
        @Override
        public CommandInterface createCommand(List<String> arguments, String user) {
            return new ListOfferingsCommand(arguments, user);
        }
    },
    BUY("buy") {
        @Override
        public CommandInterface createCommand(List<String> arguments, String user) {
            return new BuyCommand(arguments, user);
        }
    },
    SELL("sell") {
        @Override
        public CommandInterface createCommand(List<String> arguments, String user) {
            return new SellCommand(arguments, user);
        }
    },
    LOGOUT("logout") {
        @Override
        public CommandInterface createCommand(List<String> arguments, String user) {
            return new LogoutCommand(arguments, user);
        }
    },
    GET_WALLET_SUMMARY("get-wallet-summary") {
        @Override
        public CommandInterface createCommand(List<String> arguments, String user) {
            return new GetWalletSummaryCommand(arguments, user);
        }
    },
    LOGIN("login") {
        @Override
        public CommandInterface createCommand(List<String> arguments, String user) {
            return new LoginCommand(arguments, user);
        }
    },
    REGISTER("register") {
        @Override
        public CommandInterface createCommand(List<String> arguments, String user) {
            return new RegisterCommand(arguments, user);
        }
    },
    GET_WALLET_OVERALL_SUMMARY("get-wallet-overall-summary") {
        @Override
        public CommandInterface createCommand(List<String> arguments, String user) {
            return new GetWalletOverallSummaryCommand(arguments, user);
        }

    },
    LIST_OFFERING("list-offering") {
        @Override
        public CommandInterface createCommand(List<String> arguments, String user) {
            return new ListOfferingCommand(arguments, user);
        }

    },
    HELP("help") {
        @Override
        public CommandInterface createCommand(List<String> arguments, String user) {
            return new HelpCommand(arguments, user);
        }

    };
    private final String commandName;

    public abstract CommandInterface createCommand(List<String> arguments, String user);

    CommandEnum(String commandName) {
        this.commandName = commandName;
    }

    public String commandName() {
        return commandName;
    }
}
