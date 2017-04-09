package com.siamant.datadumper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Evgeniy on 08.04.2017.
 */

@Mod(modid = "datadumper", name="DataDumper", version = "1.0")
public class DataDumperBase {

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event){
        KeyBindings.init();
        FMLCommonHandler.instance().bus().register(new KeyInputHandler());
    }

    public static void makeDump() {
        dumpBlocks();
        dumpMaterials();
        dumpItems();
        dumpEntities();
    }

    private static void dumpBlocks(){
        List<BlockInfo> dump = new ArrayList<BlockInfo>();
        for(int i=0;i<65536;i++){
            Block block = Block.getBlockById(i);
            if (i != 0 && block == Blocks.air)
                continue;
            BlockInfo blockInfo = new BlockInfo();
            blockInfo.id = i;

            try {
                Item blockItem = block.getItem(null, 0, 0, 0);
                blockInfo.name = blockItem.getItemStackDisplayName(new ItemStack(blockItem));
            } catch (Exception ignored) {
            }

            blockInfo.rawname = block.getUnlocalizedName();

            try {
                blockInfo.hardness = block.getBlockHardness(null, 0, 0, 0);
            } catch (Exception ignored) {
            }

            blockInfo.harvestTool = block.getHarvestTool(0);
            blockInfo.transparent = !block.getMaterial().blocksMovement();
            dump.add(blockInfo);
        }
        saveToJsonFile(dump, "blocks.json");
    }

    private static void dumpMaterials(){
        List<MaterialInfo> dump = new ArrayList<MaterialInfo>();
        for(Item.ToolMaterial material : Item.ToolMaterial.values()){
            MaterialInfo materialInfo = new MaterialInfo();
            materialInfo.name = material.toString();
            materialInfo.effectiveness = material.getEfficiencyOnProperMaterial();
            materialInfo.damage = material.getDamageVsEntity();
            dump.add(materialInfo);
        }
        saveToJsonFile(dump, "materials.json");
    }

    private static void dumpItems(){
        List<ItemInfo> dump = new ArrayList<ItemInfo>();
        for(int i = 0; i < 65536; i++){
            Item item = Item.getItemById(i);
            if(item == null)
                continue;
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.id = i;
            itemInfo.name = item.getItemStackDisplayName(new ItemStack(item));
            itemInfo.rawname = item.getUnlocalizedName();
            if(item instanceof ItemTool){
                ItemTool itemTool = (ItemTool)item;
                itemInfo.material = itemTool.getToolMaterialName();
                ItemStack toolStack = new ItemStack(itemTool);
                Set<String> toolClasses = itemTool.getToolClasses(toolStack);
                itemInfo.toolClass = new String[toolClasses.size()];
                toolClasses.toArray(itemInfo.toolClass);
                itemInfo.harvestLevel = new int[itemInfo.toolClass.length];
                for(int j = 0 ;j < itemInfo.toolClass.length; j++){
                    itemInfo.harvestLevel[j] = itemTool.getHarvestLevel(toolStack, itemInfo.toolClass[j]);
                }
            }

            dump.add(itemInfo);
        }
        saveToJsonFile(dump, "items.json");
    }

    private static void saveToJsonFile(List dump, String fileName){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try{
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.print(gson.toJson(dump));
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void dumpEntities(){
        List<EntityInfo> dump = new ArrayList<EntityInfo>();
        for(int i = 0; i < 65536; i++){
            String entityName = EntityList.getStringFromID(i);
            if(entityName == null)
                continue;
            EntityInfo entityInfo = new EntityInfo();
            entityInfo.name = entityName;
            entityInfo.id = i;
            dump.add(entityInfo);
        }
        saveToJsonFile(dump, "entities.json");
    }

    private static class BlockInfo
    {
        int id;
        String name;
        String rawname;
        float hardness;
        Boolean transparent;
        String harvestTool;
    }

    private static class MaterialInfo
    {
        String name;
        float effectiveness;
        float damage;
    }

    private static class ItemInfo
    {
        int id;
        String name;
        String rawname;
        String material;
        String[] toolClass;
        int[] harvestLevel;
    }

    private static class EntityInfo{
        int id;
        String name;
    }
}
