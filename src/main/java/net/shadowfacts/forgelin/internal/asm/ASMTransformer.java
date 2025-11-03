package net.shadowfacts.forgelin.internal.asm;

import com.falsepattern.lib.turboasm.MergeableTurboTransformer;

import java.util.Collections;

/**
 * Referenced by {@link net.shadowfacts.forgelin.preloader.ForgelinPlugin}
 */
@SuppressWarnings("UnstableApiUsage")
public class ASMTransformer extends MergeableTurboTransformer {
    public ASMTransformer() {
        super(Collections.singletonList(new MinecraftServerThreadUtilTransformer()));
    }
}
