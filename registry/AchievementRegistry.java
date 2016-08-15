package com.dyn.betterachievements.registry;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.AchievementPage;

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
			String itemName = itemStack.getItem().getRegistryName();
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
}
