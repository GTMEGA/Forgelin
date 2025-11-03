package net.shadowfacts.forgelin.internal.asm;

import com.falsepattern.lib.asm.ASMUtil;
import com.falsepattern.lib.turboasm.ClassNodeHandle;
import com.falsepattern.lib.turboasm.TurboClassTransformer;
import lombok.SneakyThrows;
import lombok.val;
import net.shadowfacts.forgelin.Tags;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

@SuppressWarnings("UnstableApiUsage")
public class MinecraftServerThreadUtilTransformer implements TurboClassTransformer {
    private static final String HANDLER_INTERNAL = "net/shadowfacts/forgelin/internal/ServerThreadUtil";
    private static final String SERVER_NAME = "net.minecraft.server.MinecraftServer";
    private static final String SERVER_INTERNAL = "net/minecraft/server/MinecraftServer";
    private static final String PROFILER_INTERNAL = "net/minecraft/profiler/Profiler";
    @Override
    public String owner() {
        return Tags.MOD_ID;
    }

    @Override
    public String name() {
        return "MinecraftServerThreadUtilTransformer";
    }

    @Override
    public boolean shouldTransformClass(@NotNull String className, @NotNull ClassNodeHandle classNode) {
        return SERVER_NAME.equals(className);
    }

    @SneakyThrows
    @Override
    public boolean transformClass(@NotNull String className, @NotNull ClassNodeHandle classNode) {
        val cn = classNode.getNode();
        if (cn == null)
            return false;
        patchRun(cn);
        patchUpdate(cn);
        return true;
    }
    private static void patchRun(ClassNode cn) {
        val runMethod = ASMUtil.findMethodFromMCP(cn, "run", "()V", false);

        val insns = runMethod.instructions.iterator();
        while (insns.hasNext()) {
            val insn = insns.next();
            if (insn.getOpcode() == Opcodes.RETURN) {
                insns.previous();
                insns.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HANDLER_INTERNAL, "clear", "()V", false));
                insns.next();
            }
        }
        val theCall = new InsnList();
        theCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
        theCall.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HANDLER_INTERNAL, "setup", "(L" + SERVER_INTERNAL + ";)V", false));
        runMethod.instructions.insert(theCall);
    }

    private static void patchUpdate(ClassNode cn) {
        val updateMethod = ASMUtil.findMethodFromMCP(cn, "updateTimeLightAndEntities", "()V", false);
        val profilerField = ASMUtil.findFieldFromMCP(cn, "theProfiler", true);
        val theCall = new InsnList();
        if (profilerField != null && ("L" + PROFILER_INTERNAL + ";").equals(profilerField.desc)) {
            theCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
            theCall.add(new FieldInsnNode(Opcodes.GETFIELD, SERVER_INTERNAL, profilerField.name, profilerField.desc));
        } else {
            theCall.add(new InsnNode(Opcodes.ACONST_NULL));
        }
        theCall.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HANDLER_INTERNAL, "runJobs", "(L" + PROFILER_INTERNAL + ";)V", false));
        updateMethod.instructions.insert(theCall);
    }
}
