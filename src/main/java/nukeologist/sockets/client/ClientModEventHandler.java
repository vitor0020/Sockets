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

package nukeologist.sockets.client;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import nukeologist.sockets.client.datagen.EnglishProvider;
import nukeologist.sockets.client.datagen.ItemModels;
import nukeologist.sockets.client.datagen.PortugueseProvider;
import nukeologist.sockets.client.gui.SocketRemoverScreen;
import nukeologist.sockets.common.registry.SocketsContainers;

public enum ClientModEventHandler {

    INSTANCE;

    @SubscribeEvent
    public void onData(GatherDataEvent event) {
        final DataGenerator gen = event.getGenerator();
        final ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeClient()) {
            gen.addProvider(new EnglishProvider(gen));
            gen.addProvider(new PortugueseProvider(gen));
            gen.addProvider(new ItemModels(gen, helper));
        }
        if (event.includeServer()) {

        }
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(SocketsContainers.SOCKET_REMOVER.get(), SocketRemoverScreen::new);
    }

}
