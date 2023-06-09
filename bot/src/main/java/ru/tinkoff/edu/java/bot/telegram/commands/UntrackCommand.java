package ru.tinkoff.edu.java.bot.telegram.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ForceReply;
import com.pengrad.telegrambot.request.SendMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.tinkoff.edu.java.bot.telegram.interfaces.Command;

@Component
public class UntrackCommand implements Command {
    private final String COMMAND = "untrack";
    private final String DESCRIPTION = "Stop tracking link";
    private final String ANSWER = "Enter the link to the repository or the question "
        + "from which updates you want to unsubscribe:";
    private final String PLACEHOLDER = "https://github.com/user_name/repository_name";

    @Override
    public String command() {
        return COMMAND;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public SendMessage handle(@NotNull Update update) {
        return new SendMessage(update.message().chat().id(), ANSWER)
                .replyMarkup(new ForceReply().inputFieldPlaceholder(PLACEHOLDER));
    }
}
