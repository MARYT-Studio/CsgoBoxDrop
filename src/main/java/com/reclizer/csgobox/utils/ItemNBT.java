package com.reclizer.csgobox.utils;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemNBT {
    public static String readTags(ItemStack stack){

        if(stack.getTag()==null){
            return null;
        }
        return stack.getTag().toString();
    }

    public static String tagsItemName(String itemDate){
        if(!itemDate.contains(".withTags")){
            return itemDate;
        }
        String itemName;
        int withTagsIndex = itemDate.indexOf(".withTags");
        if (withTagsIndex != -1) {
            itemName = itemDate.substring(0, withTagsIndex).trim();
            return itemName;
        }
        return itemDate;
    }

    public static String tagsItemData(String itemDate){
        if(!itemDate.contains(".withTags")){
            return null;
        }
        String itemName;
        int withTagsIndex = itemDate.indexOf(".withTags");
        if (withTagsIndex != -1) {
            itemName = itemDate.substring(withTagsIndex + ".withTags".length()).trim();
            return itemName;
        }
        return null;
    }



    public static String getStacksData(ItemStack stack){
        if(stack.isEmpty()){
            return null;
        }
        ResourceLocation rl = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (rl == null) return null;
        String data = rl.toString();
        if(stack.getTag() != null){
            String tag=ItemNBT.readTags(stack);
            data=data+".withTags"+tag;
        } else if (stack.getCount() > 1) data += ("*" + stack.getCount());
        return data;
    }
    public static ItemStack getStacks(String itemData){

        String[] itemString = itemData.split("\\*");
        int amount = 1;
        if (itemString.length > 1) {
            try {
                amount = Integer.parseInt(itemString[1]);
            } catch (NumberFormatException ignored) {}
        }

        String itemName = itemString[0];
        String itemTags = null;

        if(itemData.contains(".withTags")){
            itemName=ItemNBT.tagsItemName(itemData);
            itemTags=ItemNBT.tagsItemData(itemData);
        }

        ResourceLocation res=new ResourceLocation(itemName);
        if(ForgeRegistries.ITEMS.getValue(res)==null){
            return null;
        }

        Item item = ForgeRegistries.ITEMS.getValue(res);
        if (item == null) return null;
        ItemStack stack1=new ItemStack(item);

        stack1.setCount(amount);
        if(!isModLoaded("crafttweaker")){
            return stack1;
        }

        if(itemTags != null && itemTags.contains("{")) {
            CraftWeakerNBT.setTags(stack1, itemTags);
        }
        return stack1;
    }


    public static boolean isModLoaded(String modid) {
        try {
            return ModList.get().isLoaded(modid);
        } catch (Throwable e) {
            return false;
        }
    }



}
