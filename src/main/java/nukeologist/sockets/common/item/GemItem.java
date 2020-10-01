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

package nukeologist.sockets.common.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import nukeologist.sockets.api.cap.IGem;
import nukeologist.sockets.api.cap.ISocketableItem;
import nukeologist.sockets.common.cap.CapabilityGemItem;
import nukeologist.sockets.common.util.StringTranslations;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GemItem extends Item {

    public GemItem(Properties properties) {
        super(properties);
    }

    private Predicate<ISocketableItem> canEquip;
    private Predicate<ISocketableItem> canUnequip;
    private Consumer<ISocketableItem> equip;
    private Consumer<ISocketableItem> unequip;
    private Consumer<ISocketableItem> tick;
    private Supplier<List<ITextComponent>> components;

    public GemItem canEquip(final Predicate<ISocketableItem> canEquip) {
        this.canEquip = canEquip;
        return this;
    }

    public GemItem canUnequip(final Predicate<ISocketableItem> canUnequip) {
        this.canUnequip = canUnequip;
        return this;
    }

    public GemItem onEquip(final Consumer<ISocketableItem> equip) {
        this.equip = equip;
        return this;
    }

    public GemItem onUnequip(final Consumer<ISocketableItem> unequip) {
        this.unequip = unequip;
        return this;
    }

    public GemItem tick(final Consumer<ISocketableItem> tick) {
        this.tick = tick;
        return this;
    }

    public GemItem components(final Supplier<List<ITextComponent>> components) {
        this.components = components;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT) //Exception to the rule
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent(stack.getTranslationKey() + ".shift.info").func_240699_a_(TextFormatting.GRAY));
        } else {
            tooltip.add(new TranslationTextComponent(StringTranslations.SHIFT_KEY_DOWN).func_240699_a_(TextFormatting.DARK_GRAY));
        }
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return CapabilityGemItem.createProvider(new IGem() {

            @Override
            public boolean canEquipOn(ISocketableItem item, LivingEntity entity) {
                return canEquip == null || canEquip.test(item);
            }

            @Override
            public boolean canUnequipOn(ISocketableItem item, LivingEntity entity) {
                return canUnequip == null || canUnequip.test(item);
            }

            @Override
            public void equipped(ISocketableItem item, LivingEntity entity) {
                this.playEquipSound(item, entity);
                if (equip != null) equip.accept(item);
            }

            @Override
            public void unequipped(ISocketableItem item, LivingEntity entity) {
                if (unequip != null) unequip.accept(item);
            }

            @Override
            public List<ITextComponent> getExtraTooltip() {
                return components == null ?
                        Collections.singletonList(new TranslationTextComponent(StringTranslations.DEFAULT_GEM_EXTRA_TOOLTIP).func_240699_a_(TextFormatting.GOLD)
                                .func_230529_a_(new TranslationTextComponent(stack.getTranslationKey())))
                        : components.get();
            }

            @Override
            public void socketTick(ISocketableItem item, LivingEntity entity) {
                if (tick != null) tick.accept(item);
            }
        });
    }
}
