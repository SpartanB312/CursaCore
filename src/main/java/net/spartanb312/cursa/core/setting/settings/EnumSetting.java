package net.spartanb312.cursa.core.setting.settings;

import net.spartanb312.cursa.core.setting.Setting;

public class EnumSetting<E extends Enum<E>> extends Setting<Enum<E>> {

    public EnumSetting(String name, Enum<E> defaultValue) {
        super(name, defaultValue);
    }

    public void setByName(String name) {
        for (Enum<E> value : value.getDeclaringClass().getEnumConstants()) {
            if (value.name().equals(name)) this.value = value;
        }
    }

}