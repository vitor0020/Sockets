/*
 *    Copyright 2020 Nukeologist
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package nukeologist.sockets.common.cap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import nukeologist.sockets.api.SocketsAPI;

import static nukeologist.sockets.Sockets.modLoc;

public enum CapabilityEventHandler {

    INSTANCE;

    @SubscribeEvent
    public void attachCap(AttachCapabilitiesEvent<ItemStack> event) {
        final ItemStack stack = event.getObject();
        if (stack.isEmpty() || stack.getItem() != Items.DIAMOND_SWORD) return;
        event.addCapability(modLoc("socket"), CapabilitySocketableItem.createProvider());
    }

    @SubscribeEvent
    public void gemTick(LivingEvent.LivingUpdateEvent event) {
        final LivingEntity entity = event.getEntityLiving();
        entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(this::tickGems);
    }

    private void tickGems(final IItemHandler inv) {
        for (int i = 0; i < inv.getSlots(); i++) {
            final ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            SocketsAPI.getSockets(stack).ifPresent(item -> item.getStackHandler().forEach(gem -> gem.socketTick(item)));
        }
    }
}
