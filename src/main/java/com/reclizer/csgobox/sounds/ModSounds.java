package com.reclizer.csgobox.sounds;

import com.reclizer.csgobox.CsgoBoxDrop;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
//@OnlyIn(Dist.CLIENT)
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CsgoBoxDrop.MOD_ID);
    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CsgoBoxDrop.MOD_ID, name)));
    }
    public static RegistryObject<SoundEvent> CS_DITA = registerSoundEvent("cs_dita");
    public static RegistryObject<SoundEvent> CS_OPEN = registerSoundEvent("cs_open");
    public static RegistryObject<SoundEvent> CS_FINISH = registerSoundEvent("cs_finish");


}
