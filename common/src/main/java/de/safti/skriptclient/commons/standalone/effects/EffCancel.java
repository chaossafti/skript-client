package de.safti.skriptclient.commons.standalone.effects;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.SecurityLevel;
import de.safti.skriptclient.api.synatxes.generated.GeneratedResultingEvent;
import de.safti.skriptclient.commons.standalone.events.CancellableSkriptEvent;
import de.safti.skriptclient.commons.standalone.events.CancellableTriggerContext;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EffCancel extends Effect {

    private static final Logger log = LoggerFactory.getLogger(EffCancel.class);

    static {
        // using parser API here, because we don't need any arguments.
        SkriptClient.INSTANCE.getRegistry()
                .newEffect(EffCancel.class, "[1:unu]cancel [the] [event]")
                .addData(SecurityLevel.SECURITY_LEVEL_DATA_STRING, SecurityLevel.ENHANCED)
                .register();
    }

    private boolean cancelState;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull ParseContext parseContext) {
        SkriptLogger logger = parseContext.getLogger();
        SkriptEvent currentEvent = parseContext.getParserState().getCurrentEvent();
        cancelState = parseContext.getNumericMark() != 1;


        if(currentEvent == null) {
            logger.error("No event has been initialized at this stage. You cannot cancel anything.", ErrorType.STRUCTURE_ERROR);
            return false;
        }

        // TODO: resulting event interface to mark these kinds of events to not limit them to GeneratedEvents
        if(currentEvent instanceof GeneratedResultingEvent<?>) {
            String eventString = currentEvent.toString(null, logger.isDebug());
            logger.error(eventString + " is a resulting event! use 'set event-result to {-NOTHING}' to cancel it.", ErrorType.SEMANTIC_ERROR);
            return false;
        }

        if(!(currentEvent instanceof CancellableSkriptEvent cancellableSkriptEvent && cancellableSkriptEvent.isCancellable())) {
            String eventString = currentEvent.toString(null, logger.isDebug());
            logger.error(eventString + " is not cancellable!", ErrorType.SEMANTIC_ERROR);
            return false;
        }

        return true;
    }

    @Override
    protected void execute(@NotNull TriggerContext triggerContext) {
        if(!(triggerContext instanceof CancellableTriggerContext cancellableTriggerContext)) {
            log.warn("Tried cancelling {}, but trigger context isn't cancellable.", triggerContext.getName());
            return;
        }

        cancellableTriggerContext.setCancelled(cancelState);

    }

    @Override
    public String toString(@Nullable TriggerContext triggerContext, boolean b) {
        return "cancel current event";
    }
}
