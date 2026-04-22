package bg.sofia.uni.fmi.mjt.commands;

import bg.sofia.uni.fmi.mjt.commands.concretecommands.*;
import bg.sofia.uni.fmi.mjt.entity.User;

import java.util.List;

public enum CommandEnum {
    DEPOSIT_MONEY("deposit-money") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new DepositCommand(arguments, user);
        }
    },
    LIST_OFFERINGS("list-offerings") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new ListOfferingsCommand(arguments, user);
        }
    },
    BUY("buy") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new BuyCommand(arguments, user);
        }
    },
    SELL("sell") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new SellCommand(arguments, user);
        }
    },
    LOGOUT("logout") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new LogoutCommand(arguments, user);
        }
    },
    GET_WALLET_SUMMARY("get-wallet-summary") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new GetWalletSummaryCommand(arguments, user);
        }
    },
    LOGIN("login") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new LoginCommand(arguments, user);
        }
    },
    REGISTER("register") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new RegisterCommand(arguments, user);
        }
    },
    GET_WALLET_OVERALL_SUMMARY("get-wallet-overall-summary") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new GetWalletOverallSummaryCommand(arguments, user);
        }

    },
    LIST_OFFERING("list-offering") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new ListOfferingCommand(arguments, user);
        }

    },
    CHECK_ALERTS("check-alerts") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new CheckAlertsCommand(arguments, user);
        }

    },
    SET_ALERT("set-alert") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new SetAlertCommand(arguments, user);
        }

    },
    HELP("help") {
        @Override
        public CommandInterface createCommand(List<String> arguments, User user) {
            return new HelpCommand(arguments, user);
        }

    };
    private final String commandName;

    public abstract CommandInterface createCommand(List<String> arguments, User user);

    CommandEnum(String commandName) {
        this.commandName = commandName;
    }

    public String commandName() {
        return commandName;
    }
}
