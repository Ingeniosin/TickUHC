package me.juan.uhc.player;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.juan.uhc.Main;
import me.juan.uhc.event.PlayerChangeStateEvent;
import me.juan.uhc.utils.CountDown;
import me.juan.utils.database.MongoDB;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class UHCPlayer {

    @Getter
    private static final HashMap<UUID, UHCPlayer> uhcPlayers = new HashMap<>();
    private final UUID uuid;
    private final String name;

    private final PlayerData playerData;
    /*
     * 'playerData', esta clase esta ligada a la db, dentro de ella tiene las stats y demás cosas que se podrían añadir.
     *                              SE GUARDA CUANDO SE APAGA.
     */

    private final CurrentData currentData; //'currentData', esta clase es local, es decir, contiene atributos ligados al juego actual.
    private PlayerState playerState;

    private UHCPlayer(UUID uuid, String name) {         //ESTA INSTANCIA DEBE SER ASÍNCRONA!, pues, depende de una db
        this.uuid = uuid;
        this.name = name;
        this.playerData = PlayerData.load(uuid, name);  //SE REALIZA LA CARGA CORRESPONDIENTE EN LA db
        this.currentData = new CurrentData();
        uhcPlayers.put(uuid, this);
    }

    public static UHCPlayer getOrCreate(UUID uuid, String name) {
        UHCPlayer uhcPlayer = getPlayerByUUID(uuid);
        return uhcPlayer != null ? uhcPlayer : new UHCPlayer(uuid, name);
    }

    public static UHCPlayer getPlayerByUUID(UUID id) {
        return uhcPlayers.getOrDefault(id, null);
    }


    public void setPlayerState(PlayerState playerState) {
        Bukkit.getPluginManager().callEvent(new PlayerChangeStateEvent(this.playerState, playerState, this));
        this.playerState = playerState;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public void remove() {
        uhcPlayers.remove(uuid); //Esta pensado para borrarse, pues, en el caso de que el usuario entro pero no jugo, sus stats no se guarden, pues no fueron modificadas.
    }

    @Data
    public static class PlayerData {

        private final UUID uuid;
        private final Statistics statistics;

        private String name;

        public PlayerData(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;                               // ÚNICAMENTE SE REALIZA LA INSTANCIA CUANDO EL USUARIO ES NUEVO Y NO TIENE STATS.
            this.statistics = new Statistics();

        }

        public static PlayerData load(UUID uuid, String name) {
            MongoDB.MongoDatabase db = Main.getMain().getMongoDatabase();
            boolean hasUuid = uuid != null;
            PlayerData playerData = db.find(new String[]{hasUuid ? "uuid" : "name"}, new Object[]{hasUuid ? uuid.toString() : name}).get(PlayerData.class);
            if (playerData == null) {   //EN EL CASO DE QUE NO HAYA UN REGISTRO EN LA db:
                playerData = new PlayerData(uuid, name);    //NUEVA INSTANCIA
                playerData.save();                  //GUARDADO, esto se puede quitar pero dejalo pa mas facha
            } else {                //CASO CONTRARIO (SI EXISTE EN LA db):
                if (!playerData.getName().equals(name)) {
                    playerData.setName(name);  //Al obtenerse de la db, puede que el usuario haya cambiado su IGN, por lo tanto realizamos la verificación.
                    playerData.save();         // - FATAAAAAAAAAAA - QUE SE OMITA CUANDO EL USUARIO ESTA CON DISGUISE!
                }
            }
            Main.getMain().getLogger().info(name + "'s data was successfully loaded.");
            return playerData;
        }

        public void save() {
            MongoDB.MongoDatabase db = Main.getMain().getMongoDatabase();
            db.addOrSet(new String[]{"uuid"}, new Object[]{uuid.toString()}, this);
        }


        @Getter
        public static class Statistics {

            private final Stat
                    playedTime = new Stat(),
                    netherReturned = new Stat(),
                    netherEntered = new Stat(),
                    blocksBroken = new Stat(),
                    blocksPlaced = new Stat(),
                    bowHits = new Stat(),
                    bowShots = new Stat(),
                    swordHits = new Stat(),
                    swordSwings = new Stat(),
                    gamesPlayed = new Stat(),
                    kills = new Stat(),
                    deaths = new Stat(),
                    wins = new Stat(),
                    kdr = new Stat(),
                    goldenApplesEaten = new Stat(),
                    goldenHeadsEaten = new Stat(),
                    spawnersMined = new Stat(),
                    quartzMined = new Stat(),
                    coalMined = new Stat(),
                    ironMined = new Stat(),
                    goldMined = new Stat(),
                    lapisMined = new Stat(),
                    redstoneMined = new Stat(),
                    diamondMined = new Stat(),
                    emeraldMined = new Stat();

            @Data
            public static class Stat {

                @Setter
                private int amount;

                public void increment() {
                    this.amount++;
                }

                public void reset() {
                    this.amount = 0;
                }

                public void decrement() {
                    this.amount--;
                }
            }

        }


    }

    @Getter
    public static class CurrentData {

        private final PlayerData.Statistics.Stat kills = new PlayerData.Statistics.Stat();
        private final CountDown doNotDisturb = new CountDown(30), noClean = new CountDown(20);

        public CurrentData() {

        }
    }

}
