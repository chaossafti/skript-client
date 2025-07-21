package de.safti.skriptclient.commons.standalone.effects;


import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.docs.annotations.Author;
import de.safti.skriptclient.api.docs.annotations.AvailableSince;
import de.safti.skriptclient.api.synatxes.expression.Deletable;
import de.safti.skriptclient.api.synatxes.expression.Resettable;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import io.github.syst3ms.skriptparser.registration.PatternInfos;
import io.github.syst3ms.skriptparser.types.TypeManager;
import io.github.syst3ms.skriptparser.types.changers.ChangeMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

@Author("notSafti")
@AvailableSince("1.0.0")
// still uses the old api, because Pattern doesn't support multiple patterns yet.
// TODO: replace with syntax api once pattern supports multiple values.
public class EffChange extends Effect {
    public static final PatternInfos<ChangeMode> PATTERNS = new PatternInfos<>(new Object[][]{
            {"set %~objects% to %objects%", ChangeMode.SET},
            {"%~objects% = %objects%", ChangeMode.SET},
            {"add %objects% to %~objects%", ChangeMode.ADD},
            {"%~objects% += %objects%", ChangeMode.ADD},
            {"remove %objects% from %~objects%", ChangeMode.REMOVE},
            {"%~objects% -= %objects%", ChangeMode.REMOVE},
            {"remove (all|every) %objects% from %~objects%", ChangeMode.REMOVE_ALL},
            {"(delete|clear) %~objects%", ChangeMode.DELETE},
            {"reset %~objects%", ChangeMode.RESET}
    });

    static {
        SkriptClient.INSTANCE.getRegistry()
                        .addEffect(EffChange.class, PATTERNS.getPatterns());
    }

    private Expression<?> toChange;
    @Nullable
    private Expression<?> changeWith;
    private ChangeMode mode;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
        ChangeMode mode = PATTERNS.getInfo(matchedPattern);
        if (mode == ChangeMode.RESET || mode == ChangeMode.DELETE) {
            toChange = expressions[0];
        } else if ((matchedPattern & 1) == 1 || mode == ChangeMode.SET) {
            // Notice the difference in the order of expressions
            toChange = expressions[0];
            changeWith = expressions[1];
        } else {
            toChange = expressions[1];
            changeWith = expressions[0];
        }
        this.mode = mode;

        var logger = parseContext.getLogger();
        String toChangeString = toChange.toString(TriggerContext.DUMMY, logger.isDebug());

        // validate the expression accepting the change


        // delete won't need any arguments, so toChange won't ever return on it.
        if(mode == ChangeMode.DELETE) {
            boolean isValid = toChange instanceof Deletable;
            if(!isValid) {
                logger.error(toChangeString + " cannot be deleted!", ErrorType.SEMANTIC_ERROR);
                return false;
            }
            return true;
        }

        // the same goes for reset
        if(mode == ChangeMode.RESET) {
            boolean isValid = toChange instanceof Resettable;
            if(!isValid) {
                logger.error(toChangeString + " cannot be reset!", ErrorType.SEMANTIC_ERROR);
                return false;
            }
            return true;
        }

        // any other change mode will accept arguments; make sure the arguments we provide are valid for changing
        Optional<Class<?>[]> acceptedClasses = toChange.acceptsChange(mode);

        // in case we don't want arguments, and we get none, we return true
        if(acceptedClasses.isEmpty() && changeWith == null) {
            return true;
        }

        if(changeWith == null) {
            throw new IllegalStateException();
        }

        // if the accepted classes are simply empty, we cannot change them at all.
        // put this here to supply the user with a useful error message.
        if(acceptedClasses.isEmpty()) {
            switch (mode) {
                case SET:
                    logger.error(toChangeString + " cannot be set to anything", ErrorType.SEMANTIC_ERROR);
                    break;
                case ADD:
                    logger.error("Nothing can be added to " + toChangeString, ErrorType.SEMANTIC_ERROR);
                    break;
                case REMOVE_ALL:
                case REMOVE:
                    logger.error("Nothing can be removed from " + toChangeString, ErrorType.SEMANTIC_ERROR);
                    break;
                default:
                    throw new IllegalStateException();
            }

            return false;
        }

        Class<?>[] classes = acceptedClasses.get();
        Set<Class<?>> supportedClasses = Set.of(classes);

        Class<?> providedType = changeWith.getReturnType();
        // check if the changed expression accepts the provided type
        if(!supportedClasses.contains(providedType)) {
            var type = TypeManager.getByClassExact(changeWith.getReturnType());
            assert type.isPresent();
            String changeTypeName = type.get().getBaseName();

            switch (mode) {
                case SET:
                    logger.error(toChangeString + " cannot be set to " + changeTypeName, ErrorType.SEMANTIC_ERROR);
                    break;
                case ADD:
                    logger.error(changeTypeName + " cannot be added to " + toChangeString, ErrorType.SEMANTIC_ERROR);
                    break;
                case REMOVE_ALL:
                case REMOVE:
                    logger.error(changeTypeName + " cannot be removed from " + toChangeString, ErrorType.SEMANTIC_ERROR);
                    break;
                case DELETE:
                case RESET:
                    throw new IllegalStateException();
            }

            return false;
        }

        return true;
    }

    @Override
    public void execute(TriggerContext ctx) {
        if (changeWith == null) {
            toChange.change(ctx, new Object[0], mode);
        } else {
            var values = changeWith.getValues(ctx);
            if (values.length == 0)
                return;
            toChange.change(ctx, values, mode);
        }
    }

    @Override
    public String toString(TriggerContext ctx, boolean debug) {
        String changedString = toChange.toString(ctx, debug);
        String changedWithString = changeWith != null ? changeWith.toString(ctx, debug) : "";
        return switch (mode) {
            case SET -> String.format("set %s to %s", changedString, changedWithString);
            case ADD -> String.format("add %s to %s", changedWithString, changedString);
            case REMOVE -> String.format("remove %s from %s", changedWithString, changedString);
            case DELETE, RESET -> String.format("%s %s", mode.name().toLowerCase(), changedString);
            case REMOVE_ALL -> String.format("remove all %s from %s", changedWithString, changedString);
        };
    }
}
