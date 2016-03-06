package com.dyn.betterachievements.api.components.page;

public interface ICustomArrows {
	/**
	 * The integer value of the arrow for achievements that can't be unlocked
	 * yet Use
	 * {@link com.dyn.betterachievements.api.util.ColourHelper#RGB(String)} to
	 * use #RRGGBB format to integer
	 *
	 * @return the integer value of the arrow
	 */
	int getColourForCantUnlockArrow();

	/**
	 * The integer value of the arrow for achievements that can be unlocked Use
	 * {@link com.dyn.betterachievements.api.util.ColourHelper#RGB(String)} to
	 * use #RRGGBB format to integer
	 *
	 * @return the integer value of the arrow
	 */
	int getColourForCanUnlockArrow();

	/**
	 * The integer value of the arrow for unlocked achievements Use
	 * {@link com.dyn.betterachievements.api.util.ColourHelper#RGB(String)} to
	 * use #RRGGBB format to integer
	 *
	 * @return the integer value of the arrow
	 */
	int getColourForUnlockedArrow();
}
