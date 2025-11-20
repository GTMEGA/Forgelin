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
public class MinecraftClientThreadUtilTransformer implements TurboClassTransformer {
    private static final String HANDLER_INTERNAL = "net/shadowfacts/forgelin/coroutines/MinecraftClientDispatcher";
    private static final String MC_NAME = "net.minecraft.client.Minecraft";
    private static final String MC_INTERNAL = "net/minecraft/client/Minecraft";
    private static final String PROFILER_INTERNAL = "net/minecraft/profiler/Profiler";

    @Override
    public String owner() {
        return Tags.MOD_ID;
    }

    @Override
    public String name() {
        return "MinecraftClientThreadUtilTransformer";
    }

    @Override
    public boolean shouldTransformClass(@NotNull String className, @NotNull ClassNodeHandle classNode) {
        return MC_NAME.equals(className);
    }

    @SneakyThrows
    @Override
    public boolean transformClass(@NotNull String className, @NotNull ClassNodeHandle classNode) {
        val cn = classNode.getNode();
        if (cn == null)
            return false;
        patchRunTick(cn);
        return true;
    }


    private static void patchRunTick(ClassNode cn) {
        val runTickMethod = ASMUtil.findMethodFromMCP(cn, "runTick", "()V", false);
        val profilerField = ASMUtil.findFieldFromMCP(cn, "mcProfiler", true);
        val theCall = new InsnList();
        if (profilerField != null && ("L" + PROFILER_INTERNAL + ";").equals(profilerField.desc)) {
            theCall.add(new VarInsnNode(Opcodes.ALOAD, 0));
            theCall.add(new FieldInsnNode(Opcodes.GETFIELD, MC_INTERNAL, profilerField.name, profilerField.desc));
        } else {
            theCall.add(new InsnNode(Opcodes.ACONST_NULL));
        }
        theCall.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HANDLER_INTERNAL, "runTasks", "(L" + PROFILER_INTERNAL + ";)V", false));
        runTickMethod.instructions.insert(theCall);
    }
}
