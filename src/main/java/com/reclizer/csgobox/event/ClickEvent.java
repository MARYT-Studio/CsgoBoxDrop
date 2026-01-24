package com.reclizer.csgobox.event;

import com.reclizer.csgobox.CsgoBoxDrop;
import com.reclizer.csgobox.gui.client.CsLookItemScreen;
import com.reclizer.csgobox.gui.client.CsboxProgressScreen;
import com.reclizer.csgobox.gui.client.CsboxScreen;
import com.reclizer.csgobox.item.ItemCsgoBox;
import com.reclizer.csgobox.item.ModItems;
import com.reclizer.csgobox.sounds.ModSounds;
import com.reclizer.csgobox.utils.BlurHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CsgoBoxDrop.MOD_ID)
public class ClickEvent {


    @SubscribeEvent
    public static void mouseEvent(final InputEvent.MouseButton event) {

        LocalPlayer player = Minecraft.getInstance().player;
        if(player==null){
            return;
        }

        if(player.getMainHandItem().getItem()== ItemStack.EMPTY.getItem()){
            return;
        }

        if((Minecraft.getInstance().screen instanceof CsboxScreen)){
            return;
        }

        if((Minecraft.getInstance().screen instanceof CsboxProgressScreen)){
            return;
        }

        if((Minecraft.getInstance().screen instanceof CsLookItemScreen)){
            return;
        }

        if(player.getMainHandItem().getItem() instanceof ItemCsgoBox &&event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT){

            player.playSound(ModSounds.CS_OPEN.get(), 10F, 1F);

            ItemCsgoBox.BoxInfo info =ItemCsgoBox.getBoxInfo(player.getMainHandItem());
            if(info!=null){
                Minecraft.getInstance().setScreen(new CsboxScreen());
            }
        }


    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        InteractionHand hand = player.getUsedItemHand();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() == ModItems.ITEM_CSGOBOX.get() && event.getHand() == hand) {
            //ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);

            BlurHandler.isShaderOn=true;

            //System.out.println(entity.getLuck());
            player.playSound(ModSounds.CS_OPEN.get(), 10F, 1F);

            ItemCsgoBox.BoxInfo info =ItemCsgoBox.getBoxInfo(heldItem);
            if(info!=null){
                //System.out.println(Arrays.toString(info.boxRandom));
//            if(level.isClientSide){
//                DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
//
                Minecraft.getInstance().setScreen(new CsboxScreen());

//                });

                //}

                //execute(level, entity.getX(), entity.getY(), entity.getZ(), entity, stack);
            }
        }
    }
}
