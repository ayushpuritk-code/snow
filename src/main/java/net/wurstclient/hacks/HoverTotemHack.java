/*
 * Copyright (c) 2014-2026 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixin.AbstractContainerScreenAccessor;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({"hover totem", "inventory totem", "offhand", "off-hand"})
public final class HoverTotemHack extends Hack implements UpdateListener
{
	private final SliderSetting delay = new SliderSetting("Delay",
		"Amount of ticks to wait before moving the next totem.", 0, 0, 10, 1,
		ValueDisplay.INTEGER);
	
	private int timer;
	
	public HoverTotemHack()
	{
		super("HoverTotem");
		setCategory(Category.COMBAT);
		addSetting(delay);
	}
	
	@Override
	protected void onEnable()
	{
		timer = 0;
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(timer > 0)
		{
			timer--;
			return;
		}
		
		// only works while your own inventory is open
		if(!(MC.gui.screen() instanceof InventoryScreen screen))
			return;
		
		// nothing to do if there is already a totem in the off-hand
		if(MC.player.getOffhandItem().is(Items.TOTEM_OF_UNDYING))
			return;
		
		Slot slot = ((AbstractContainerScreenAccessor)screen).getHoveredSlot();
		if(slot == null || !slot.hasItem())
			return;
		
		// only totems
		if(!slot.getItem().is(Items.TOTEM_OF_UNDYING))
			return;
		
		// only the main inventory and hotbar slots (9-44)
		int slotId = slot.index;
		if(slotId < 9 || slotId > 44)
			return;
		
		// 40 = off-hand
		IMC.getGameMode().windowClick_SWAP(slotId, 40);
		timer = delay.getValueI();
	}
}
