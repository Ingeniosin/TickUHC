package me.juan.uhc.configuration;

import lombok.Getter;
import lombok.Setter;
import me.juan.uhc.utils.FileConfig;

public enum ConfigurationFile {

    HOTBAR(new FileConfig("items.yml")),
    MAIN(new FileConfig("config.yml")),
    LANG(new FileConfig("lang.yml")),
    MEETUP(new FileConfig("meetup.yml")),
    SCOREBOARD(new FileConfig("scoreboard.yml"));

        /*
        TODAS LAS CONFIGURACIONES (O LA MAYORÍA) VIENEN EN ENUMS, ASI SE HACE LA INSTANCIA UNA VEZ Y QUEDAN ESTÁTICAS.
        EL MÉTODO 'PluginUntil.getElement' SIRVE PARA HACER UN CHECK DE TODO TIPO DE DATO QUE SE NECESITE DE ESTA
        CONFIGURACIÓN, ES DECIR, SI HAY UN DATO QUE NO EXISTE HARA UNA EXCEPTION, ESA EXCEPTION SERVIRÁ PARA EL MANEJO DE EXCEPCIONES
        Y QUE EN EL JUEGO NO HAYA UN ERROR DE UN DATO NULO, PUES AL SER UN ENUM, Y REQUERIR ALGUNA VARIABLE SE "CARGAN" TODAS
        Y AL CARGARSE SE VE EL MÉTODO 'PluginUntil.getElement' Y SE REALIZARA LA EXCEPTION.

        */

    @Getter
    @Setter
    private FileConfig config;

    ConfigurationFile(FileConfig config) {
        this.config = config;
    }

}
