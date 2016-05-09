package com.dyn.betterachievements.registry;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.dyn.betterachievements.api.components.page.ICustomIcon;
import com.dyn.betterachievements.util.LogHelper;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class AchievementRegistry {
	private static AchievementRegistry instance;
	public static final AchievementPage mcPage = new AchievementPage("Minecraft");

	public static AchievementRegistry instance() {
		if (instance == null) {
			instance = new AchievementRegistry();
		}
		return instance;
	}

	private List<Achievement> mcAchievements;
	private Map<String, Achievement> statIdMap;
	private Map<String, ItemStack> iconMap;
	private Map<String, ItemStack> userSetIcons;

	private boolean firstLoad;

	private AchievementRegistry() {
		firstLoad = true;
		mcAchievements = new LinkedList<Achievement>();
		iconMap = new LinkedHashMap<String, ItemStack>();
		statIdMap = new LinkedHashMap<String, Achievement>();
		userSetIcons = new LinkedHashMap<String, ItemStack>();
	}

	public String[] dumpUserSetIcons() {
		List<String> list = new LinkedList<String>();
		for (Map.Entry<String, ItemStack> entry : userSetIcons.entrySet()) {
			String pageName = entry.getKey();
			ItemStack itemStack = entry.getValue();
			String itemName = GameRegistry.findUniqueIdentifierFor(itemStack.getItem()).toString();
			String nbtCompoundTag = itemStack.hasTagCompound() ? itemStack.getTagCompound().toString() : "";
			list.add(pageName + "->" + itemName + ":" + entry.getValue().getItemDamage() + ":" + nbtCompoundTag);
		}
		return list.toArray(new String[list.size()]);
	}

	public Achievement getAchievement(String statId) {
		return statIdMap.get(statId);
	}

	public List<Achievement> getAchievements(AchievementPage page) {
		if (firstLoad) {
			init();
		}
		return page == mcPage ? mcAchievements : page.getAchievements();
	}

	public List<AchievementPage> getAllPages() {
		if (firstLoad) {
			init();
		}
		List<AchievementPage> pages = new LinkedList<AchievementPage>();
		pages.add(mcPage);
		int size = AchievementPage.getAchievementPages().size();
		for (int i = 0; i < size; i++) {
			pages.add(AchievementPage.getAchievementPage(i));
		}
		return pages;
	}

	public ItemStack getItemStack(AchievementPage page) {
		if (page == null) {
			return null;
		}
		ItemStack itemStack = iconMap.get(page.getName());
		if (itemStack == null) {
			if (page instanceof ICustomIcon) {
				itemStack = ((ICustomIcon) page).getPageIcon();
			}
			if (itemStack == null) {
				for (Achievement achievement : page.getAchievements()) {
					if (achievement.parentAchievement == null) {
						itemStack = achievement.theItemStack;
						iconMap.put(page.getName(), itemStack);
						break;
					}
				}
			}
		}
		return itemStack;
	}

	private void init() {
		for (Object oa : AchievementList.achievementList) {
			Achievement achievement = (Achievement) oa;
			statIdMap.put(achievement.statId, achievement);
			if (!AchievementPage.isAchievementInPages(achievement)) {
				mcAchievements.add(achievement);
			}
		}
		iconMap.put(mcPage.getName(), new ItemStack(Blocks.grass));
		iconMap.putAll(userSetIcons);
		firstLoad = false;
	}

	public void registerIcon(String pageName, ItemStack itemStack, boolean userSet) {
		iconMap.put(pageName, itemStack);
		if (userSet) {
			userSetIcons.put(pageName, itemStack);
		}
	}

	public void setUserSetIcons(String[] array) {
		int i = 0;
		for (String entry : array) {
			String[] split = entry.split("->");
			if (split.length != 2) {
				continue;
			}
			String[] itemSplit = split[1].split(":", 4);
			if (itemSplit.length < 2) {
				continue;
			}
			Item item = GameRegistry.findItem(itemSplit[0], itemSplit[1]);
			int meta = 0;
			try {
				meta = itemSplit.length > 2 ? Integer.parseInt(itemSplit[2]) : 0;
			} catch (NumberFormatException e) {
				LogHelper.instance().error(e, "Invalid input for meta data on entry " + i);
			}
			NBTTagCompound nbtTag = null;
			try {
				nbtTag = (itemSplit.length > 3) && !itemSplit[3].equals("") ? JsonToNBT.getTagFromJson(itemSplit[3])
						: null;
			} catch (NBTException e) {
				LogHelper.instance().error(e, "Invalid input for nbt data on entry " + i);
			}
			ItemStack itemStack = null;
			if (item != null) {
				itemStack = new ItemStack(item, 0, meta);
			}
			if (itemStack != null) {
				if (nbtTag != null) {
					itemStack.setTagCompound(nbtTag);
				}
				userSetIcons.put(split[0], itemStack);
			}
			i++;
		}
	}
}
