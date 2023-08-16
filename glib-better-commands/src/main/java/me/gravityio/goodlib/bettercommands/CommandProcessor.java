package me.gravityio.goodlib.bettercommands;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.gravityio.goodlib.util.AnsiCodes.*;

public class CommandProcessor {

    private static final List<LiteralArgumentBuilder<ServerCommandSource>> serverCommands = new ArrayList<>();
    private static final List<LiteralArgumentBuilder<FabricClientCommandSource>> clientCommands = new ArrayList<>();

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            for (LiteralArgumentBuilder<ServerCommandSource> serverCommand : serverCommands)
                dispatcher.register(serverCommand);
        });
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            for (LiteralArgumentBuilder<FabricClientCommandSource> clientCommand : clientCommands) {
                dispatcher.register(clientCommand);
            }
        });
    }

    public static void register(Class<?> clazz) {
        if (!isValid(clazz)) return;
        Commands commands = clazz.getAnnotation(Commands.class);
        boolean server = commands.environment() == EnvType.SERVER;
        if (server) {
            BetterCommandsMod.LOGGER.debug("Registering Server Sided Commands from Class '{}'", clazz.getSimpleName());
            var cmd = processServer(clazz, commands);
            serverCommands.add(cmd);
        } else {
            BetterCommandsMod.LOGGER.debug("Registering Client Sided Commands from Class '{}'", clazz.getSimpleName());
            var cmd = processClient(clazz, commands);
            clientCommands.add(cmd);
        }
    }

    public static boolean isValid(Class<?> clazz) {
        if (!isAnnon(clazz)) throw new CommandsClassMissingAnnotation(clazz);
        return true;
    }

    private static boolean isAnnon(Class<?> clazz) {
        return clazz.isAnnotationPresent(Commands.class);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> processServer(Class<?> ass, Commands classAnnon) {
        BetterCommandsMod.LOGGER.debug("{}Processing Server Sided Commands from Class {}'{}'{}", GR, BG, ass.getSimpleName(), RST);
        String namespace = ass.getSimpleName().toLowerCase();
        if (classAnnon != null)
            namespace = classAnnon.namespace();
        LiteralArgumentBuilder<ServerCommandSource> top = CommandManager.literal(namespace);
        for (Class<?> subClass : ass.getClasses()) {
            if (!isValid(subClass)) continue;
            Commands commands = subClass.getAnnotation(Commands.class);
            var cmd = processServer(subClass, commands);
            top.then(cmd);
        }
        Method ignoredAnnon = null;
        Method typeAnnon = null;
        List<Method> commandAnnon = new ArrayList<>();
        for (Method method : ass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ArgumentHandler.class))
                ignoredAnnon = method;
            else if (method.isAnnotationPresent(TypeProvider.class))
                typeAnnon = method;
            else if (method.isAnnotationPresent(Command.class))
                commandAnnon.add(method);
        }

        for (Method method : commandAnnon) {
            Command command = method.getAnnotation(Command.class);
            String cmdName = command.name();
            if (cmdName.equals(""))
                cmdName = method.getName();
            BetterCommandsMod.LOGGER.debug("  {}Compiling method command {}'{}'{} from method '{}'{}", BG, GR, cmdName, BG, method.getName(), RST);
            LiteralArgumentBuilder<ServerCommandSource> cmd = CommandManager.literal(cmdName);
            var handlerMap = getHandlersServer(ignoredAnnon, cmdName);
            var typeMap = getTypes(typeAnnon, cmdName);
            var typedArguments = getTypedArgumentsServer(method, typeMap);
            compileServer(cmd, method, typedArguments, handlerMap);
            top.then(cmd);
        }
        return top;
    }

    private static LiteralArgumentBuilder<FabricClientCommandSource> processClient(Class<?> ass, Commands classAnnon) {
        BetterCommandsMod.LOGGER.debug("Processing Client Sided Command from Class '{}'", ass.getSimpleName());
        String namespace = ass.getSimpleName().toLowerCase();
        if (classAnnon != null)
            namespace = classAnnon.namespace();
        LiteralArgumentBuilder<FabricClientCommandSource> top = ClientCommandManager.literal(namespace);
        for (Class<?> subClass : ass.getDeclaredClasses()) {
            if (!isValid(subClass)) continue;
            Commands commands = subClass.getAnnotation(Commands.class);
            LiteralArgumentBuilder<FabricClientCommandSource> cmd = processClient(subClass, commands);
            top.then(cmd);
        }
        Method ignoredAnnon = null;
        Method typeAnnon = null;
        List<Method> commandAnnon = new ArrayList<>();
        for (Method method : ass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(ArgumentHandler.class))
                ignoredAnnon = method;
            else if (method.isAnnotationPresent(TypeProvider.class))
                typeAnnon = method;
            else if (method.isAnnotationPresent(Command.class))
                commandAnnon.add(method);
        }

        for (Method method : commandAnnon) {
            Command command = method.getAnnotation(Command.class);
            String cmdName = command.name();
            if (cmdName.equals(""))
                cmdName = method.getName();
            BetterCommandsMod.LOGGER.debug("Registering command ({}) from method '{}'", cmdName, method.getName());
            LiteralArgumentBuilder<FabricClientCommandSource> cmd = ClientCommandManager.literal(cmdName);
            var handlerMap = getHandlersClient(ignoredAnnon, cmdName);
            var typeMap = getTypes(typeAnnon, cmdName);
            var typedArguments = getTypedArgumentsClient(method, typeMap);
            compileClient(cmd, method, typedArguments, handlerMap);
        }
        return top;
    }

    private static List<RequiredArgumentBuilder<ServerCommandSource, ?>> getTypedArgumentsServer(Method cmdMethod, Map<Class<?>, IArgumentTypeProvider> typeMap) {
        List<RequiredArgumentBuilder<ServerCommandSource, ?>> typedArgs = new ArrayList<>();
        for (Parameter parameter : cmdMethod.getParameters()) {
            Class<?> type = parameter.getType();
            Argument annon = parameter.getAnnotation(Argument.class);
            if (!typeMap.containsKey(type)) continue;
            typedArgs.add(CommandManager.argument(annon != null ? annon.name() : parameter.getName(), typeMap.get(type).provide()));
        }
        return typedArgs;
    }

    private static List<RequiredArgumentBuilder<FabricClientCommandSource, ?>> getTypedArgumentsClient(Method cmdMethod, Map<Class<?>, IArgumentTypeProvider> typeMap) {
        List<RequiredArgumentBuilder<FabricClientCommandSource, ?>> typedArgs = new ArrayList<>();
        for (Parameter parameter : cmdMethod.getParameters()) {
            Class<?> type = parameter.getType();
            Argument annon = parameter.getAnnotation(Argument.class);
            if (!typeMap.containsKey(type)) continue;
            typedArgs.add(ClientCommandManager.argument(annon != null ? annon.name() : parameter.getName(), typeMap.get(type).provide()));
        }
        return typedArgs;
    }

    private static Map<Class<?>, IArgumentTypeProvider> getTypes(Method typeAnnon, String cmdName) {
        return (Map<Class<?>, IArgumentTypeProvider>) invokeMethod(typeAnnon, null, cmdName);
    }

    private static Map<Class<?>, IArgumentHandler<ServerCommandSource>> getHandlersServer(Method handledAnnon, String cmdName) {
        return (Map<Class<?>, IArgumentHandler<ServerCommandSource>>) invokeMethod(handledAnnon, null, cmdName);
    }

    private static Map<Class<?>, IArgumentHandler<FabricClientCommandSource>> getHandlersClient(Method handledAnnon, String cmdName) {
        return (Map<Class<?>, IArgumentHandler<FabricClientCommandSource>>) invokeMethod(handledAnnon, null, cmdName);
    }

    private static void compileServer(LiteralArgumentBuilder<ServerCommandSource> cmd,
                                      Method method,
                                      List<RequiredArgumentBuilder<ServerCommandSource, ?>> typedArguments,
                                      Map<Class<?>, IArgumentHandler<ServerCommandSource>> handlers) {
        if (method.getParameters().length == 0)
            compileArglessMethodServer(cmd, method);
        else
            compileArgfulMethodServer(cmd, method, typedArguments, handlers);
    }

    private static void compileClient(LiteralArgumentBuilder<FabricClientCommandSource> cmd,
                                      Method method,
                                      List<RequiredArgumentBuilder<FabricClientCommandSource, ?>> typedArguments,
                                      Map<Class<?>, IArgumentHandler<FabricClientCommandSource>> handlers) {
        if (method.getParameters().length == 0)
            compileArglessMethodClient(cmd, method);
        else
            compileArgfulMethodClient(cmd, method, typedArguments, handlers);
    }

    private static void compileArglessMethodServer(LiteralArgumentBuilder<ServerCommandSource> cmd, Method method) {
        cmd.executes(context -> {
            invokeMethod(method, null);
            return 1;
        });
    }

    private static void compileArglessMethodClient(LiteralArgumentBuilder<FabricClientCommandSource> cmd, Method method) {
        cmd.executes(context -> {
            invokeMethod(method, null);
            return 1;
        });
    }

    private static void compileArgfulMethodServer(LiteralArgumentBuilder<ServerCommandSource> cmd,
                                                  Method method,
                                                  List<RequiredArgumentBuilder<ServerCommandSource, ?>> typedArguments,
                                                  Map<Class<?>, IArgumentHandler<ServerCommandSource>> handlers) {

        if (typedArguments.isEmpty()) {
            cmd.executes(context -> {
                invokeCommandServer(method, handlers, context);
                return 1;
            });
            return;
        }

        int lastIndex = typedArguments.size() - 1;
        var lastArg = typedArguments.get(lastIndex);
        lastArg.executes(context -> {
            invokeCommandServer(method, handlers, context);
            return 1;
        });

        if (typedArguments.size() == 1) {
            cmd.then(lastArg);
        }

        // We need to build each argument in reverse, if we do
        // it from front to back the first argument has no way
        // to build and add it's following arguments
        for (int i = lastIndex - 1; i >= 0; i--) {
            var compile = typedArguments.get(i);
            // If not at end
            if (i != lastIndex) {
                var temp = typedArguments.get(i + 1);
                compile.then(temp);
            }
            // If first argument then add it to the cmd
            if (i == 0) {
                cmd.then(compile);
            }
        }
    }

    private static void compileArgfulMethodClient(LiteralArgumentBuilder<FabricClientCommandSource> cmd,
                                                  Method method,
                                                  List<RequiredArgumentBuilder<FabricClientCommandSource, ?>> typedArguments,
                                                  Map<Class<?>, IArgumentHandler<FabricClientCommandSource>> handlers) {

        if (typedArguments.isEmpty()) {
            cmd.executes(context -> {
                invokeCommandClient(method, handlers, context);
                return 1;
            });
            return;
        }

        int lastIndex = typedArguments.size() - 1;
        var lastArg = typedArguments.get(lastIndex);
        lastArg.executes(context -> {
            invokeCommandClient(method, handlers, context);
            return 1;
        });

        if (typedArguments.size() == 1) {
            cmd.then(lastArg);
        }

        // We need to build each argument in reverse, if we do
        // it from front to back the first argument has no way
        // to build and add it's following arguments
        for (int i = lastIndex - 1; i >= 0; i--) {
            var compile = typedArguments.get(i);
            // If not at end
            if (i != lastIndex) {
                var temp = typedArguments.get(i + 1);
                compile.then(temp);
            }
            // If first argument then add it to the cmd
            if (i == 0) {
                cmd.then(compile);
            }
        }
    }

    private static Object invokeCommandServer(Method method, Map<Class<?>, IArgumentHandler<ServerCommandSource>> handlers, CommandContext<ServerCommandSource> context) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Argument annon = parameter.getAnnotation(Argument.class);
            Class<?> type = parameter.getType();
            IArgumentHandler<ServerCommandSource> handler = handlers.get(type);

            if (handler == null) {
                UnimplementedHandlerException er = new UnimplementedHandlerException("Unimplemented Handler for Class '%s'".formatted(type.getName()));
                BetterCommandsMod.LOGGER.error(er.toString());
                throw er;
            }
            Object temp = handler.handle(annon != null ? annon.name() : parameter.getName(), context);

            args[i] = temp;
        }
        return invokeMethod(method, null, args);
    }

    private static Object invokeCommandClient(Method method, Map<Class<?>, IArgumentHandler<FabricClientCommandSource>> handlers, CommandContext<FabricClientCommandSource> context) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Argument annon = parameter.getAnnotation(Argument.class);
            Class<?> type = parameter.getType();
            IArgumentHandler<FabricClientCommandSource> handler = handlers.get(type);

            if (handler == null) {
                UnimplementedHandlerException er = new UnimplementedHandlerException("Unimplemented Handler for Class '%s'".formatted(type.getName()));
                BetterCommandsMod.LOGGER.error(er.toString());
                throw er;
            }
            Object temp = handler.handle(annon != null ? annon.name() : parameter.getName(), context);

            args[i] = temp;
        }
        return invokeMethod(method, null, args);
    }


    private static Object invokeMethod(Method cmdMethod, Object instance, Object... args) {
        try {
            if (!cmdMethod.canAccess(null))
                cmdMethod.setAccessible(true);
            return cmdMethod.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }


    // For when I make the classes instance and inheritance based
    public interface CommandHandler<T extends CommandSource> {
        Map<Class<?>, IArgumentHandler<T>> getArgumentHandlers(String cmdName);
        Map<Class<?>, IArgumentTypeProvider> getArgumentTypeProviders(String cmdName);
    }

    public interface IArgumentHandler<T extends CommandSource> {
        Map<Class<?>, IArgumentHandler<FabricClientCommandSource>> defClientHandlers = Map.of(
                String.class, (argName, context) -> context.getArgument(argName, String.class),
                int.class, (argName, context) -> context.getArgument(argName, int.class),
                double.class, (argName, context) -> context.getArgument(argName, double.class),
                float.class, (argName, context) -> context.getArgument(argName, float.class),
                long.class, (argName, context) -> context.getArgument(argName, long.class),
                boolean.class, (argName, context) -> context.getArgument(argName, boolean.class)
        );

        Map<Class<?>, IArgumentHandler<ServerCommandSource>> defServerHandlers = Map.of(
                String.class, (argName, context) -> context.getArgument(argName, String.class),
                int.class, (argName, context) -> context.getArgument(argName, int.class),
                double.class, (argName, context) -> context.getArgument(argName, double.class),
                float.class, (argName, context) -> context.getArgument(argName, float.class),
                long.class, (argName, context) -> context.getArgument(argName, long.class),
                boolean.class, (argName, context) -> context.getArgument(argName, boolean.class)
        );

        static Map<Class<?>, IArgumentHandler<FabricClientCommandSource>> getDefaultClientHandlers() {
            return new HashMap<>(defClientHandlers);
        }

        static Map<Class<?>, IArgumentHandler<ServerCommandSource>> getDefaultServerHandlers() {
            return new HashMap<>(defServerHandlers);
        }

        Object handle(String name, CommandContext<T> context);
    }

    public interface IArgumentTypeProvider {
        Map<Class<?>, IArgumentTypeProvider> defProviders = Map.of(
                String.class, StringArgumentType::word,
                int.class, IntegerArgumentType::integer,
                double.class, DoubleArgumentType::doubleArg,
                float.class, FloatArgumentType::floatArg,
                long.class, LongArgumentType::longArg,
                boolean.class, BoolArgumentType::bool
        );

        static Map<Class<?>, IArgumentTypeProvider> getDefaultTypeProviders() {
            return new HashMap<>(defProviders);
        }

        ArgumentType<?> provide();
    }

    private static class UnimplementedHandlerException extends RuntimeException {
        public UnimplementedHandlerException(String message) {
            super(message);
        }
    }

    private static class CommandsClassMissingAnnotation extends RuntimeException {
        public CommandsClassMissingAnnotation(Class<?> clazz) {
            super("Trying to register a Commands Class '%s' without the required @Commands Annotation".formatted(clazz.getSimpleName()));
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Commands {
        String namespace() default "";

        EnvType environment() default EnvType.SERVER;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Command {
        String name() default "";
        String[] namespace() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Argument {
        String name() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ArgumentHandler {
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface TypeProvider {
    }

//    @Retention(RetentionPolicy.RUNTIME)
//    public @interface ArgumentTypeEntry {
//        String id() default "";
//    }

}
