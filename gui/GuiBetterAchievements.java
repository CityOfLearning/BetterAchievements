package com.dyn.betterachievements.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.dyn.betterachievements.reference.Reference;
import com.dyn.betterachievements.registry.AchievementRegistry;
import com.dyn.betterachievements.util.ColourHelper;
import com.dyn.betterachievements.util.RenderHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBetterAchievements extends GuiScreen {

	public static final ResourceLocation SPRITES = new ResourceLocation(Reference.MOD_ID, "textures/gui/sprites.png");
	public static final ResourceLocation TABS = new ResourceLocation(Reference.MOD_ID, "textures/gui/tabs.png");

	private static final int blockSize = 16, maxTabs = 9, lineSize = 12, defaultTooltipWidth = 120, arrowHeadWidth = 11,
			arrowHeadHeight = 7, arrowOffset = 5, arrowRightX = 114, arrowRightY = 234, arrowLeftX = 107,
			arrowLeftY = 234, arrowDownX = 96, arrowDownY = 234, arrowUpX = 96, arrowUpY = 241, achievementX = 0,
			achievementY = 202, achievementTooltipOffset = 3, achievementTextureSize = 26, achievementOffset = 2,
			achievementSize = 24, achievementInnerSize = 22, buttonDone = 1, buttonPrev = 3, buttonNext = 4,
			buttonOffsetX = 24, buttonOffsetY = 92, guiWidth = 252, guiHeight = 202, tabWidth = 28, tabHeight = 32,
			borderWidthX = 8, borderWidthY = 17, tabOffsetX = 0, tabOffsetY = -12, innerWidth = 228, innerHeight = 158,
			minDisplayColumn = (AchievementList.minDisplayColumn * achievementSize) - (10 * achievementSize),
			minDisplayRow = (AchievementList.minDisplayRow * achievementSize) - (10 * achievementSize),
			maxDisplayColumn = AchievementList.maxDisplayColumn * achievementSize,
			maxDisplayRow = AchievementList.maxDisplayRow * achievementSize;
	private static final float scaleJump = 0.25F, minZoom = 1.0F, maxZoom = 2.0F;
	private static final Random random = new Random();
	public static int colourUnlocked, colourCanUnlock, colourCantUnlock;
	public static boolean scrollButtons, iconReset, userColourOverride, colourUnlockedRainbow, colourCanUnlockRainbow,
			colourCantUnlockRainbow;
	public static float[] colourUnlockedRainbowSettings, colourCanUnlockRainbowSettings,
			colourCantUnlockRainbowSettings;
	private static int lastPage = 0;
	private GuiScreen prevScreen;
	private StatFileWriter statFileWriter;
	private int top, left;
	private float scale;
	private boolean pause, newDrag;
	private int prevMouseX, prevMouseY;
	private List<AchievementPage> pages;
	private int currentPage, tabsOffset;
	private int xPos, yPos;
	private Achievement hoveredAchievement;

	public GuiBetterAchievements(GuiScreen currentScreen, int page) {
		prevScreen = currentScreen;
		currentPage = page == 0 ? lastPage : page;
		statFileWriter = Minecraft.getMinecraft().thePlayer.getStatFileWriter();
		pause = true;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case buttonDone:
			mc.displayGuiScreen(prevScreen);
			break;
		case buttonPrev: // this is causing an index out of bounds error
			int newOffset = tabsOffset;
			newOffset -= maxTabs;
			if (newOffset == -maxTabs) {
				newOffset = pages.size() - ((maxTabs / 3) * 2);
			} else if (newOffset < 0) {
				tabsOffset = 0;
			}
			break;
		case buttonNext:
			tabsOffset += maxTabs;
			if (tabsOffset > pages.size()) {
				tabsOffset = 0;
			} else if (tabsOffset > (pages.size() - ((maxTabs / 3) * 2))) {
				tabsOffset = pages.size() - ((maxTabs / 3) * 2);
			}
			break;
		default:
			break;
		}
	}

	private void doDrag(int mouseX, int mouseY) {
		if (Mouse.isButtonDown(0)) {
			if (inInnerScreen(mouseX, mouseY)) {
				if (newDrag) {
					newDrag = false;
				} else {
					xPos -= (mouseX - prevMouseX) * scale;
					yPos -= (mouseY - prevMouseY) * scale;
				}

				prevMouseX = mouseX;
				prevMouseY = mouseY;
			}
		} else {
			newDrag = true;
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return pause;
	}

	private void doTabScroll() {
		int dWheel = Mouse.getDWheel();

		if (dWheel < 0) {
			tabsOffset--;
		} else if (dWheel > 0) {
			tabsOffset++;
		}

		if (tabsOffset > (pages.size() - ((maxTabs / 3) * 2))) {
			tabsOffset = pages.size() - ((maxTabs / 3) * 2);
		}
		if (tabsOffset < 0) {
			tabsOffset = 0;
		}
	}

	private void doZoom(AchievementPage page) {
		int dWheel = Mouse.getDWheel();
		float prevScale = scale;

		if (dWheel < 0) {
			scale += scaleJump;
		} else if (dWheel > 0) {
			scale -= scaleJump;
		}

		float minZoom = GuiBetterAchievements.minZoom;
		float maxZoom = GuiBetterAchievements.maxZoom;
		scale = MathHelper.clamp_float(scale, minZoom, maxZoom);

		if (scale != prevScale) {
			float prevScaledWidth = prevScale * width;
			float prevScaledHeight = prevScale * height;
			float newScaledWidth = scale * width;
			float newScaledHeight = scale * height;
			xPos -= (newScaledWidth - prevScaledWidth) / 2;
			yPos -= (newScaledHeight - prevScaledHeight) / 2;
		}
	}

	private void drawAchievement(Achievement achievement) {
		int achievementXPos = (achievement.displayColumn * achievementSize) - xPos;
		int achievementYPos = (achievement.displayRow * achievementSize) - yPos;

		if (!onScreen(achievementXPos, achievementYPos)) {
			return;
		}

		int depth = statFileWriter.func_150874_c(achievement);
		boolean unlocked = statFileWriter.hasAchievementUnlocked(achievement);
		boolean canUnlock = statFileWriter.canUnlockAchievement(achievement);
		boolean special = achievement.getSpecial();
		float brightness;

		if (unlocked) {
			brightness = 0.75F;
		} else if (canUnlock) {
			brightness = 1.0F;
		} else if (depth < 3) {
			brightness = 0.3F;
		} else if (depth < 4) {
			brightness = 0.2F;
		} else if (depth < 5) {
			brightness = 0.1F;
		} else {
			return;
		}
		GlStateManager.color(brightness, brightness, brightness, 1.0F);
		mc.getTextureManager().bindTexture(SPRITES);
		GlStateManager.enableBlend();
		if (special) {
			this.drawTexturedModalRect(achievementXPos - achievementOffset, achievementYPos - achievementOffset,
					achievementX + achievementTextureSize, achievementY, achievementTextureSize,
					achievementTextureSize);
		} else {
			this.drawTexturedModalRect(achievementXPos - achievementOffset, achievementYPos - achievementOffset,
					achievementX, achievementY, achievementTextureSize, achievementTextureSize);
		}
		RenderItem renderItem = RenderHelper.getRenderItem();
		if (!canUnlock) {
			GlStateManager.color(0.1F, 0.1F, 0.1F, 1.0F);
			renderItem.func_175039_a(false); // Render with colour
		}

		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.enableCull();
		renderItem.renderItemAndEffectIntoGUI(achievement.theItemStack, achievementXPos + 3, achievementYPos + 3);

		if (!canUnlock) {
			renderItem.func_175039_a(true); // Render with colour
		}
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableLighting();
	}

	private void drawAchievements(AchievementPage page, int mouseX, int mouseY) {
		List<Achievement> achievements = new LinkedList<Achievement>(
				AchievementRegistry.instance().getAchievements(page));
		int colourCantUnlock = (GuiBetterAchievements.colourCantUnlockRainbow
				? ColourHelper.getRainbowColour(GuiBetterAchievements.colourCantUnlockRainbowSettings)
				: GuiBetterAchievements.colourCantUnlock);
		int colourCanUnlock = (GuiBetterAchievements.colourCanUnlockRainbow
				? ColourHelper.getRainbowColour(GuiBetterAchievements.colourCanUnlockRainbowSettings)
				: GuiBetterAchievements.colourCanUnlock);
		int colourUnlocked = (GuiBetterAchievements.colourUnlockedRainbow
				? ColourHelper.getRainbowColour(GuiBetterAchievements.colourUnlockedRainbowSettings)
				: GuiBetterAchievements.colourUnlocked);
		Collections.reverse(achievements);
		GlStateManager.pushMatrix();
		float inverseScale = 1.0F / scale;
		GlStateManager.scale(inverseScale, inverseScale, 1.0F);
		for (Achievement achievement : achievements) {
			if ((achievement.parentAchievement != null) && achievements.contains(achievement.parentAchievement)) {
				drawArrow(achievement, colourCantUnlock, colourCanUnlock, colourUnlocked);
			}
		}
		for (Achievement achievement : achievements) {
			drawAchievement(achievement);
			if (onAchievement(achievement, mouseX, mouseY)) {
				hoveredAchievement = achievement;
			}
		}
		GlStateManager.popMatrix();
	}

	private void drawAchievementsBackground(AchievementPage page) {
		GL11.glTranslatef(left, top + borderWidthY, -200.0F);
		GlStateManager.enableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		float scaleInverse = 1.0F / scale;
		GlStateManager.scale(scaleInverse, scaleInverse, 1.0F);
		float scale = blockSize / this.scale;
		int dragX = (xPos - minDisplayColumn) >> 4;
		int dragY = (yPos - minDisplayRow) >> 4;
		int antiJumpX = (xPos - minDisplayColumn) % 16;
		int antiJumpY = (yPos - minDisplayRow) % 16;
		// TODO: some smarter background gen
		mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		for (int y = 1; ((y * scale) - antiJumpY) < (innerHeight + borderWidthY); y++) {
			float darkness = 0.7F - ((dragY + y) / 80.0F);
			GL11.glColor4f(darkness, darkness, darkness, 1.0F);
			mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			for (int x = 1; ((x * scale) - antiJumpX) < (innerWidth + borderWidthX); x++) {
				random.setSeed(mc.getSession().getPlayerID().hashCode() + dragY + y + ((dragX + x) * 16));
				int r = random.nextInt(1 + dragY + y) + ((dragY + y) / 2);
				TextureAtlasSprite icon = RenderHelper.getIcon(Blocks.grass);
				if (r == 40) {
					if (random.nextInt(3) == 0) {
						icon = RenderHelper.getIcon(Blocks.diamond_ore);
					} else {
						icon = RenderHelper.getIcon(Blocks.redstone_ore);
					}
				} else if (r == 20) {
					icon = RenderHelper.getIcon(Blocks.iron_ore);
				} else if (r == 12) {
					icon = RenderHelper.getIcon(Blocks.coal_ore);
				} else if (r > 60) {
					icon = RenderHelper.getIcon(Blocks.bedrock);
				} else if (r > 4) {
					icon = RenderHelper.getIcon(Blocks.stone);
				} else if (r > 0) {
					icon = RenderHelper.getIcon(Blocks.dirt);
				}
				this.drawTexturedModalRect((x * blockSize) - antiJumpX, (y * blockSize) - antiJumpY, icon, blockSize,
						blockSize);
			}
		}
		GlStateManager.popMatrix();
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
	}

	private void drawArrow(Achievement achievement, int colourCantUnlock, int colourCanUnlock, int colourUnlocked) {
		int depth = statFileWriter.func_150874_c(achievement); // How far
																// is the
																// nearest
																// unlocked
																// parent

		if (depth < 5) {
			int achievementXPos = ((achievement.displayColumn * achievementSize) - xPos) + (achievementInnerSize / 2);
			int achievementYPos = ((achievement.displayRow * achievementSize) - yPos) + (achievementInnerSize / 2);
			int parentXPos = ((achievement.parentAchievement.displayColumn * achievementSize) - xPos)
					+ (achievementInnerSize / 2);
			int parentYPos = ((achievement.parentAchievement.displayRow * achievementSize) - yPos)
					+ (achievementInnerSize / 2);
			boolean unlocked = statFileWriter.hasAchievementUnlocked(achievement);
			boolean canUnlock = statFileWriter.canUnlockAchievement(achievement);
			int colour = colourCantUnlock;

			if (unlocked) {
				colour = colourUnlocked;
			} else if (canUnlock) {
				colour = colourCanUnlock;
			}

			drawHorizontalLine(achievementXPos, parentXPos, achievementYPos, colour);
			drawVerticalLine(parentXPos, achievementYPos, parentYPos, colour);

			mc.getTextureManager().bindTexture(SPRITES);
			GlStateManager.enableBlend();
			if (achievementXPos > parentXPos) {
				this.drawTexturedModalRect(achievementXPos - (achievementInnerSize / 2) - arrowHeadHeight,
						achievementYPos - arrowOffset, arrowRightX, arrowRightY, arrowHeadHeight, arrowHeadWidth);
			} else if (achievementXPos < parentXPos) {
				this.drawTexturedModalRect(achievementXPos + (achievementInnerSize / 2), achievementYPos - arrowOffset,
						arrowLeftX, arrowLeftY, arrowHeadHeight, arrowHeadWidth);
			} else if (achievementYPos > parentYPos) {
				this.drawTexturedModalRect(achievementXPos - arrowOffset,
						achievementYPos - (achievementInnerSize / 2) - arrowHeadHeight, arrowDownX, arrowDownY,
						arrowHeadWidth, arrowHeadHeight);
			} else if (achievementYPos < parentYPos) {
				this.drawTexturedModalRect(achievementXPos - arrowOffset, achievementYPos + (achievementInnerSize / 2),
						arrowUpX, arrowUpY, arrowHeadWidth, arrowHeadHeight);
			}
		}
	}

	private void drawCurrentTab(AchievementPage selected) {
		for (int i = tabsOffset; (i < (maxTabs + tabsOffset)) && (pages.size() > i); i++) {
			AchievementPage page = pages.get(i);
			if (page != selected) {
				continue;
			}
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int j = (i - tabsOffset) * tabWidth;
			mc.getTextureManager().bindTexture(TABS);
			this.drawTexturedModalRect(left + tabOffsetX + j, top + tabOffsetY, j, 32, tabWidth, tabHeight);
			drawPageIcon(page, left + tabOffsetX + j, top + tabOffsetY);
		}
	}

	private void drawMouseOverAchievement(int mouseX, int mouseY) {
		if ((hoveredAchievement == null) || !inInnerScreen(mouseX, mouseY)) {
			return;
		}

		if (iconReset && Mouse.isButtonDown(2)) {
			AchievementRegistry.instance().registerIcon(pages.get(currentPage).getName(),
					hoveredAchievement.theItemStack, true);
		}

		int tooltipX = mouseX + 12;
		int tooltipY = mouseY - 4;

		String title = hoveredAchievement.getStatName().getUnformattedText();
		String desc = hoveredAchievement.getDescription();

		int depth = statFileWriter.func_150874_c(hoveredAchievement);
		boolean unlocked = statFileWriter.hasAchievementUnlocked(hoveredAchievement);
		boolean canUnlock = statFileWriter.canUnlockAchievement(hoveredAchievement);
		boolean special = hoveredAchievement.getSpecial();
		int tooltipWidth = defaultTooltipWidth;

		if (!canUnlock) {
			if (depth > 3) {
				return;
			} else {
				desc = getChatComponentTranslation("achievement.requires",
						hoveredAchievement.parentAchievement.getStatName());
			}

			if (depth == 3) {
				title = I18n.format("achievement.unknown");
			}
		}

		tooltipWidth = Math.max(fontRendererObj.getStringWidth(title), tooltipWidth);
		int tooltipHeight = fontRendererObj.splitStringWidth(desc, tooltipWidth);

		if (unlocked) {
			tooltipHeight += lineSize;
		}

		drawGradientRect(tooltipX - achievementTooltipOffset, tooltipY - achievementTooltipOffset,
				tooltipX + tooltipWidth + achievementTooltipOffset,
				tooltipY + tooltipHeight + achievementTooltipOffset + lineSize, -1073741824, -1073741824);
		fontRendererObj.drawStringWithShadow(title, tooltipX, tooltipY,
				canUnlock ? (special ? -128 : -1) : (special ? -8355776 : -8355712));
		fontRendererObj.drawSplitString(desc, tooltipX, tooltipY + lineSize, tooltipWidth, -6250336);
		if (unlocked) {
			fontRendererObj.drawStringWithShadow(I18n.format("achievement.taken"), tooltipX,
					tooltipY + tooltipHeight + 4, -7302913);
		}

		hoveredAchievement = null;
	}

	private void drawMouseOverTab(int mouseX, int mouseY) {
		int onTab = onTab(mouseX, mouseY);
		if ((onTab == -1) || (pages.size() <= onTab)) {
			return;
		}
		AchievementPage page = pages.get(onTab);
		List<String> tooltip = new LinkedList<String>();
		tooltip.add(page.getName());
		this.drawHoveringText(tooltip, mouseX, mouseY, fontRendererObj);
	}

	private void drawPageIcon(AchievementPage page, int tabLeft, int tabTop) {

		ItemStack itemStack = AchievementRegistry.instance().getItemStack(page);
		if (itemStack != null) {
			zLevel = 100.0F;
			itemRender.zLevel = 100.0F;
			net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.enableRescaleNormal();
			itemRender.renderItemAndEffectIntoGUI(itemStack, tabLeft + 6, tabTop + 9);
			itemRender.zLevel = 0.0F;
			GlStateManager.disableLighting();
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			zLevel = 0.0F;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
		drawDefaultBackground();
		AchievementPage page = pages.get(currentPage);
		this.handleMouseInput(mouseX, mouseY, page);
		drawUnselectedTabs(page);
		GlStateManager.depthFunc(GL11.GL_GEQUAL);
		GlStateManager.pushMatrix();
		drawAchievementsBackground(page);
		drawAchievements(page, mouseX, mouseY);
		GlStateManager.popMatrix();
		GlStateManager.enableBlend();
		mc.getTextureManager().bindTexture(SPRITES);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(left, top + (tabHeight / 2), 0, 0, guiWidth, guiHeight);
		drawCurrentTab(page);
		fontRendererObj.drawString(page.getName() + " " + I18n.format("gui.achievements"), left + 15,
				top + (tabHeight / 2) + 5, 4210752);
		super.drawScreen(mouseX, mouseY, renderPartialTicks);
		drawMouseOverAchievement(mouseX, mouseY);
		drawMouseOverTab(mouseX, mouseY);
	}

	private void drawUnselectedTabs(AchievementPage selected) {
		for (int i = tabsOffset; (i < (maxTabs + tabsOffset)) && (pages.size() > i); i++) {
			AchievementPage page = pages.get(i);
			if (page == selected) {
				continue;
			}
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int j = (i - tabsOffset) * tabWidth;
			mc.getTextureManager().bindTexture(TABS);
			this.drawTexturedModalRect(left + tabOffsetX + j, top + tabOffsetY, j, 0, tabWidth, tabHeight);
			drawPageIcon(page, left + tabOffsetX + j, top + tabOffsetY);
		}
	}

	private String getChatComponentTranslation(String s, Object... objects) {
		return (new ChatComponentTranslation(s, objects)).getUnformattedTextForChat();
	}

	private void handleMouseClick(int mouseX, int mouseY) {
		int onTab = onTab(mouseX, mouseY);
		if ((onTab == -1) || (pages.size() <= onTab) || (currentPage == onTab)) {
			return;
		}
		currentPage = onTab;
		pages.get(currentPage);
	}

	private void handleMouseInput(int mouseX, int mouseY, AchievementPage page) {
		doDrag(mouseX, mouseY);
		if (onTab(mouseX, mouseY) != -1) {
			doTabScroll();
		} else {
			doZoom(page);
		}
		if (xPos < minDisplayColumn) {
			xPos = minDisplayColumn;
		}
		if (xPos > maxDisplayColumn) {
			xPos = maxDisplayColumn;
		}
		if (yPos < minDisplayRow) {
			yPos = minDisplayRow;
		}
		if (yPos > maxDisplayRow) {
			yPos = maxDisplayRow;
		}
		if (Mouse.isButtonDown(0)) {
			handleMouseClick(mouseX, mouseY);
		}
	}

	private boolean inInnerScreen(int mouseX, int mouseY) {
		return (mouseX > (left + borderWidthX)) && (mouseX < ((left + guiWidth) - borderWidthX))
				&& (mouseY > (top + borderWidthY)) && (mouseY < ((top + guiHeight) - borderWidthY));
	}

	@Override
	public void initGui() {
		left = (width - guiWidth) / 2;
		top = (height - guiHeight) / 2;
		scale = 1.0F;
		xPos = achievementSize * 3;
		yPos = achievementSize;

		buttonList.clear();
		buttonList.add(new GuiButton(buttonDone, (width / 2) + buttonOffsetX, (height / 2) + buttonOffsetY, 80, 20,
				I18n.format("gui.done")));
		// this.buttonList.add(new GuiButton(buttonOld, this.left +
		// buttonOffsetX, this.height / 2 + buttonOffsetY, 125, 20,
		// I18n.format("com.dyn.betterachievements.gui.old")));

		hoveredAchievement = null;
		pages = AchievementRegistry.instance().getAllPages();
		if (pages.size() > maxTabs) {
			scrollButtons = false;
		}
		if (scrollButtons) {
			buttonList.add(new GuiButton(buttonPrev, left - 24, top - 5, 20, 20, "<"));
			buttonList.add(new GuiButton(buttonNext, left + 256, top - 5, 20, 20, ">"));
		}

		tabsOffset = currentPage < ((maxTabs / 3) * 2) ? 0 : currentPage - ((maxTabs / 3) * 2);
		if (tabsOffset < 0) {
			tabsOffset = 0;
		}

		pages.get(currentPage);
	}

	@Override
	protected void keyTyped(char c, int i) throws IOException {
		if (i == mc.gameSettings.keyBindInventory.getKeyCode()) {
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		} else if (i == Keyboard.KEY_LEFT) {
			currentPage--;
			if (currentPage < 0) {
				currentPage = pages.size() - 1;
				tabsOffset += (pages.size() / maxTabs) * maxTabs;
			}
			if ((currentPage - tabsOffset) < 0) {
				tabsOffset -= maxTabs;
			}
			if (tabsOffset < 0) {
				tabsOffset = 0;
			}
		} else if (i == Keyboard.KEY_RIGHT) {
			currentPage++;
			if (currentPage >= pages.size()) {
				currentPage = 0;
				tabsOffset = 0;
			}
			if ((currentPage - tabsOffset) >= maxTabs) {
				tabsOffset += maxTabs;
			}
			if (pages.size() <= tabsOffset) {
				tabsOffset = pages.size() - 1;
			}
		} else {
			super.keyTyped(c, i);
		}
	}

	private boolean onAchievement(Achievement achievement, int mouseX, int mouseY) {
		int achievementXPos = (achievement.displayColumn * achievementSize) - xPos;
		int achievementYPos = ((achievement.displayRow * achievementSize) - yPos) + achievementInnerSize;
		return (mouseX > (left + (achievementXPos / scale)))
				&& (mouseX < (left + ((achievementXPos + achievementInnerSize) / scale)))
				&& (mouseY > (top + (achievementYPos / scale)))
				&& (mouseY < (top + ((achievementYPos + achievementInnerSize) / scale)));
	}

	@Override
	public void onGuiClosed() {
		lastPage = currentPage;
	}

	private boolean onScreen(int x, int y) {
		return (x > 0) && (x < ((guiWidth * scale) - achievementSize)) && (y > 0)
				&& (y < ((guiHeight * scale) - achievementSize));
	}

	/**
	 * Get the index of the tab the mouse is on
	 *
	 * @param mouseX
	 *            x coord of the mouse
	 * @param mouseY
	 *            y coord of the mouse
	 * @return -1 if not on a tab otherwise the index
	 */
	private int onTab(int mouseX, int mouseY) {
		if ((mouseX > (left + tabOffsetX)) && (mouseX < (left + guiWidth)) && (mouseY > (top + tabOffsetY))
				&& (mouseY < (top + tabOffsetY + tabHeight))) {
			return ((mouseX - (left + tabOffsetX)) / tabWidth) + tabsOffset;
		}
		return -1;
	}
}
