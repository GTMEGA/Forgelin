package net.shadowfacts.forgelin.internal.asm;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

/**
 * Referenced by {@link net.shadowfacts.forgelin.preloader.ForgelinPlugin}
 */
@SuppressWarnings("unused")
public class ASMTransformer implements IClassTransformer {
    private static final String SERVER_NAME = "net.minecraft.server.MinecraftServer";
    private static final String SERVER_INTERNAL = "net/minecraft/server/MinecraftServer";
    private static final String MC_NAME = "net.minecraft.client.Minecraft";
    private static final String MC_INTERNAL = "net/minecraft/client/Minecraft";
    private static final String PROFILER_INTERNAL = "net/minecraft/profiler/Profiler";
    private static final String PROFILER_DESC = "L" + PROFILER_INTERNAL + ";";

    private static final String CLIENT_HANDLER_INTERNAL = "net/shadowfacts/forgelin/coroutines/MinecraftClientDispatcher";
    private static final String SERVER_HANDLER_INTERNAL = "net/shadowfacts/forgelin/coroutines/MinecraftServerDispatcher";
    private static final String HANDLER_METHOD_NAME = "runTasks";
    private static final String HANDLER_METHOD_DESC = "(L" + PROFILER_INTERNAL + ";)V";

    private final boolean deobf = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null)
            return null;
        if (SERVER_NAME.equals(transformedName)) {
            val node = bytesToNode(basicClass);
            patchServerRun(node);
            patchServerUpdate(node);
            return nodeToBytes(node);
        } else if (MC_NAME.equals(transformedName)) {
            val node = bytesToNode(basicClass);
            patchClientRunTick(node);
            return nodeToBytes(node);
        }
        return basicClass;
    }

    // region Client

    private void patchClientRunTick(ClassNode node) {
        val runTickMethod = findMethod(node, forEnv("runTick", "func_71407_l"), "()V");
        val profilerField = findField(node, forEnv("mcProfiler", "field_71424_I"));
        val theCall = new InsnList();
        callHandler(theCall, profilerField, MC_INTERNAL, CLIENT_HANDLER_INTERNAL);
        runTickMethod.instructions.insert(theCall);
    }

    // endregion

    // region Server

    private void patchServerRun(ClassNode node) {
        val runMethod = findMethod(node, "run", "()V");

        val insns = runMethod.instructions.iterator();
        while (insns.hasNext()) {
            val insn = insns.next();
            if (insn.getOpcode() == Opcodes.RETURN) {
                insns.previous();
                insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, SERVER_HANDLER_INTERNAL, "clear", "()V", false));
                insns.next();
            }
        }
        val theCall = new InsnList();
        theCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        theCall.add(new MethodInsnNode(Opcodes.INVOKESTATIC, SERVER_HANDLER_INTERNAL, "setup", "(L" + SERVER_INTERNAL + ";)V", false));
        runMethod.instructions.insert(theCall);
    }

    private void patchServerUpdate(ClassNode node) {
        val updateMethod = findMethod(node, forEnv("updateTimeLightAndEntities", "func_71190_q"), "()V");
        val profilerField = findField(node, forEnv("theProfiler", "field_71304_b"));
        val theCall = new InsnList();
        callHandler(theCall, profilerField, SERVER_INTERNAL, SERVER_HANDLER_INTERNAL);
        updateMethod.instructions.insert(theCall);
    }

    // endregion

    // region util

    private String forEnv(String deobf, String obf) {
        return this.deobf ? deobf : obf;
    }

    private static void callHandler(@NotNull InsnList theCall, @Nullable FieldNode profilerField, String fieldClass, String handler) {
        if (profilerField != null && PROFILER_DESC.equals(profilerField.desc)) {
            theCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
            theCall.add(new FieldInsnNode(Opcodes.GETFIELD, fieldClass, profilerField.name, profilerField.desc));
        } else {
            theCall.add(new InsnNode(Opcodes.ACONST_NULL));
        }
        theCall.add(new MethodInsnNode(Opcodes.INVOKESTATIC, handler, HANDLER_METHOD_NAME, HANDLER_METHOD_DESC, false));
    }

    private static @NotNull MethodNode findMethod(@NotNull ClassNode node, @NotNull String name, @SuppressWarnings("SameParameterValue") @NotNull String desc) {
        for (val method: node.methods) {
            if (name.equals(method.name) && desc.equals(method.desc)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Could not find method " + name + " " + desc + " in class " + node.name);
    }

    private static @Nullable FieldNode findField(@NotNull ClassNode node, @NotNull String name) {
        for (val field: node.fields) {
            if (name.equals(field.name)) {
                return field;
            }
        }
        return null;
    }

    private static @NotNull ClassNode bytesToNode(byte @NotNull [] basicClass) {
        val node = new ClassNode();
        new ClassReader(basicClass).accept(node, 0);
        return node;
    }

    private static byte @NotNull [] nodeToBytes(@NotNull ClassNode node) {
        val writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }

    // endregion
}
