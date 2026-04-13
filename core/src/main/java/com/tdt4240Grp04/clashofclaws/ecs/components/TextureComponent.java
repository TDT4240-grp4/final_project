package com.tdt4240Grp04.clashofclaws.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.gdx.graphics.Texture;

public class TextureComponent implements Component {
    public Texture texture;

    public static final ComponentMapper<TextureComponent> MAPPER =
        ComponentMapper.getFor(TextureComponent.class);
}

